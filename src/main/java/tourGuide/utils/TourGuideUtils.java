package tourGuide.utils;

import tourGuide.domain.Attractions;
import tourGuide.domain.NearbyAttractions;

import java.util.ArrayList;
import java.util.List;

public class TourGuideUtils {

    public List<NearbyAttractions> createFinalList (List<Attractions> wipList) {
        List<NearbyAttractions> finalList = new ArrayList<>();
        NearbyAttractions attraction1 = new NearbyAttractions(wipList.get(0).getAttraction().attractionName, wipList.get(0).getAttractionLat(), wipList.get(0).getAttractionLat(), wipList.get(0).getDistance(), wipList.get(0).getRewardsPoints());
        NearbyAttractions attraction2 = new NearbyAttractions(wipList.get(1).getAttraction().attractionName, wipList.get(1).getAttractionLat(), wipList.get(1).getAttractionLat(), wipList.get(1).getDistance(), wipList.get(1).getRewardsPoints());
        NearbyAttractions attraction3 = new NearbyAttractions(wipList.get(2).getAttraction().attractionName, wipList.get(2).getAttractionLat(), wipList.get(2).getAttractionLat(), wipList.get(2).getDistance(), wipList.get(2).getRewardsPoints());
        NearbyAttractions attraction4 = new NearbyAttractions(wipList.get(3).getAttraction().attractionName, wipList.get(3).getAttractionLat(), wipList.get(3).getAttractionLat(), wipList.get(3).getDistance(), wipList.get(3).getRewardsPoints());
        NearbyAttractions attraction5 = new NearbyAttractions(wipList.get(4).getAttraction().attractionName, wipList.get(4).getAttractionLat(), wipList.get(4).getAttractionLat(), wipList.get(4).getDistance(), wipList.get(4).getRewardsPoints());
        finalList.add(attraction1);
        finalList.add(attraction2);
        finalList.add(attraction3);
        finalList.add(attraction4);
        finalList.add(attraction5);
        return finalList;
    }
    }

