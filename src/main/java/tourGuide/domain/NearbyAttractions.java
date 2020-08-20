package tourGuide.domain;

public class NearbyAttractions {
    private String attractionName;
    private Double attractionLat;
    private Double attractionLong;
    private Double distance;
    private Integer rewardsPoints;

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public Double getAttractionLat() {
        return attractionLat;
    }

    public void setAttractionLat(Double attractionLat) {
        this.attractionLat = attractionLat;
    }

    public Double getAttractionLong() {
        return attractionLong;
    }

    public void setAttractionLong(Double attractionLong) {
        this.attractionLong = attractionLong;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getRewardsPoints() {
        return rewardsPoints;
    }

    public void setRewardsPoints(Integer rewardsPoints) {
        this.rewardsPoints = rewardsPoints;
    }

    public NearbyAttractions(String attractionName, Double attractionLat, Double attractionLong, Double distance, Integer rewardsPoints) {
        this.attractionName = attractionName;
        this.attractionLat = attractionLat;
        this.attractionLong = attractionLong;
        this.distance = distance;
        this.rewardsPoints = rewardsPoints;
    }
}
