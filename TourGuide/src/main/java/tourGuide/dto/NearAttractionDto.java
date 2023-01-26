package tourGuide.dto;


import gpsUtil.location.Location;

// Name of Tourist attraction,
// Tourist attractions lat/long,
// The user's location lat/long,
// The distance in miles between the user's location and each of the attractions.
// The reward points for visiting each Attraction.
//    Note: Attraction reward points can be gathered from RewardsCentral
public class NearAttractionDto {
    private String name;
    private Location attractionLocation;
    private Location userLocation;
    private Double distance;
    private Integer rewardPoint;

    public NearAttractionDto(String name, Location attractionLocation, Location userLocation, Double distance, Integer rewardPoint) {
        this.name = name;
        this.attractionLocation = attractionLocation;
        this.userLocation = userLocation;
        this.distance = distance;
        this.rewardPoint = rewardPoint;
    }
}
