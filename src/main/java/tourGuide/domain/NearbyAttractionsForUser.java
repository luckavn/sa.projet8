package tourGuide.domain;

import java.util.List;

public class NearbyAttractionsForUser {
    private Double userLat;
    private Double userLong;
    private List<NearbyAttractions> nearbyAttractionList;

    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLong() {
        return userLong;
    }

    public void setUserLong(Double userLong) {
        this.userLong = userLong;
    }

    public List<NearbyAttractions> getNearbyAttractionList() {
        return nearbyAttractionList;
    }

    public void setNearbyAttractionList(List<NearbyAttractions> nearbyAttractionList) {
        this.nearbyAttractionList = nearbyAttractionList;
    }
}
