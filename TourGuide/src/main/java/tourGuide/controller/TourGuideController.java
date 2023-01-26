package tourGuide.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import gpsUtil.location.Attraction;


import gpsUtil.location.Location;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.dto.NearAttractionDto;
import tourGuide.service.GpsUtilService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tripPricer.Provider;

@RestController
public class TourGuideController {


	private TourGuideService tourGuideService;

    private GpsUtilService gpsUtilService;

    public TourGuideController(TourGuideService tourGuideService, GpsUtilService gpsUtilService) {
        this.tourGuideService = tourGuideService;
        this.gpsUtilService = gpsUtilService;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation;

            visitedLocation=this.gpsUtilService.getUserLocation(getUser(userName).getUserId());

		return JsonStream.serialize(visitedLocation.location);
    }
    
    //  TODO: Change this method to no longer return a List of Attractions.
 	//  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
 	//  Return a new JSON object that contains:
    	// Name of Tourist attraction, 
        // Tourist attractions lat/long, 
        // The user's location lat/long, 
        // The distance in miles between the user's location and each of the attractions.
        // The reward points for visiting each Attraction.
        //    Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) {

        User user = getUser(userName);
        Map<Double, Attraction> distanceAttraction = tourGuideService.distanceAttractions(user.getUserId());

        // cree une liste vide NearByLocationDTO
        List<NearAttractionDto> nearByAttractions = new ArrayList<>();
        // parcourir ta map
        distanceAttraction.forEach((distance, attraction) -> {
            NearAttractionDto nearAttractionDto = new NearAttractionDto(
                    attraction.attractionName,
                    new Location(attraction.latitude, attraction.longitude),
                    user.getLastVisitedLocation().location,
                    distance,
                    this.tourGuideService.getRewardPoints(attraction, user));
            nearByAttractions.add(nearAttractionDto);
            // pour chaque entree de la map
            // creer un NearByDTO avec les infos de l'attraction et de la distance
            // ajouter a la liste
        });
        return JsonStream.serialize(nearByAttractions);


        // renvoyer la liste

    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
    	// done: Get a list of every user's most recent location as JSON
    	//- Note: does not use gpsUtil to query for their current location, 
    	//        but rather gathers the user's current location from their stored location history.
    	//
    	// Return object should be the just a JSON mapping of userId to Locations similar to:
    	//     {
    	//        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    	//        ...
    	//     }


        // récuperer dans une map la liste des users
        Map<String, Location> userLastLocation = tourGuideService.getAllCurrentLocation();


        //retourner la map
    	
    	return JsonStream.serialize(userLastLocation);
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }
    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}