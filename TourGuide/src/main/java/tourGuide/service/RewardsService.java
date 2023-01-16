package tourGuide.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.user.User;

@Service
public class RewardsService {

    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	@Autowired
	private GpsUtilService gpsUtilService;
	@Autowired
	private RewardCentral rewardsCentral;

	public RewardsService(RewardCentral rewardsCentral) {
		this.rewardsCentral = rewardsCentral;
	}


	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}
	
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	

	
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return gpsUtilService.getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return gpsUtilService.getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}
	
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	


}
