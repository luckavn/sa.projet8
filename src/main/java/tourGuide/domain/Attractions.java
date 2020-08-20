package tourGuide.domain;

import gpsUtil.location.Attraction;

public class Attractions {
    private Attraction attraction;
    private Double attractionLat;
    private Double attractionLong;
    private Double distance;
    private Integer rewardsPoints;

    public Attraction getAttraction() {
        return attraction;
    }

    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
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

    public Attractions(Attraction attraction, Double attractionLat, Double attractionLong, Double distance, Integer rewardsPoints) {
        this.attraction = attraction;
        this.attractionLat = attractionLat;
        this.attractionLong = attractionLong;
        this.distance = distance;
        this.rewardsPoints = rewardsPoints;
    }
}
