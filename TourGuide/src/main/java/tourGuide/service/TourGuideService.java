package tourGuide.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import gpsUtil.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.repository.TourGuideRepository;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtilService gpsUtilService;
	private final RewardsService rewardsService;
	private final TripPriceService tripPriceService;

	public final Tracker tracker;

	@Autowired
	private TourGuideRepository tourGuideRepository;





	boolean testMode = true;
	
	public TourGuideService(GpsUtilService gpsUtilService, RewardsService rewardsService, TripPriceService tripPriceService) {
		this.gpsUtilService = gpsUtilService;
		this.rewardsService = rewardsService;
		this.tripPriceService = tripPriceService;


		if(testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}

	private void initializeInternalUsers() {

		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			this.gpsUtilService.generateUserLocationHistory(user);

			tourGuideRepository.addUser(user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	public User getUser(String userName) {
		return tourGuideRepository.getUser(userName);
	}
	
	public List<User> getAllUsers() {
		return tourGuideRepository.getAllUsers();
	}
	
	public void addUser(User user) {
		tourGuideRepository.addUser(user);
	}
	
	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPriceService.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}
	
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtilService.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		calculateRewards(user);
		return visitedLocation;
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		for(Attraction attraction : gpsUtilService.getAllAttraction()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		
		return nearbyAttractions;
	}
	
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() { 
		      public void run() {
		        tracker.stopTracking();
		      } 
		    }); 
	}
	
	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory

	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtilService.getAllAttraction();

		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(rewardsService.nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, rewardsService.getRewardPoints(attraction, user)));
					}
				}
			}
		}
	}

	public Map<Double, Attraction> distanceAttractions (UUID userId){
		// recupere la position de l'utilisateur
		VisitedLocation visitedLocation = gpsUtilService.getUserLocation(userId);
		// recupere toutes les attractions
		List<Attraction> attractions = gpsUtilService.getAllAttraction();
		// pour chaque attraction
		   // calculer distance entre ustilisateur et attraction

// stocker le resultat
		Map<Double, Attraction> distances = new TreeMap<>(new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return o1.compareTo(o2);
			}
		});

		for(Attraction attr: attractions) {
			double distance = gpsUtilService.getDistance(visitedLocation.location, attr);
			distances.put(distance, attr);
		}


		// renvoyer les 5 attractions les plus proches
		return distances;

	}
	public int getRewardPoints(Attraction attraction, User user) {

		return this.rewardsService.getRewardPoints(attraction, user);
	}

	public Map<String, Location> getAllCurrentLocation(){
		// récuperer dans une map la liste des users
		Map<String, Location> userLastLocation = new HashMap<String, Location>();
		List<User> users = getAllUsers();

		// récupere le dernier élément de la liste dans la boucle

		for(User user: users) {
			VisitedLocation  lastLocation = user.getVisitedLocations().get(user.getVisitedLocations().size()-1);
			userLastLocation.put(user.getUserId().toString(), lastLocation.location);
		}
		return userLastLocation;
	}


}
