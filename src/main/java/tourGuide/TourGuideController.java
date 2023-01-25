package tourGuide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gpsUtil.location.Attraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.VisitedLocation;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;

@RestController
public class TourGuideController {

	@Autowired
	TourGuideService tourGuideService;

    @Autowired
    RewardsService rewardsService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
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
    //http://localhost:8080/getNearbyAttractions?userName=internalUser1
    @RequestMapping("/getNearbyAttractions") 
    public ResponseEntity getNearbyAttractions(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        NearbyAttractionDto nearbyAttraction= new NearbyAttractionDto();
        List<NearbyAttractionDto> attractionList=new ArrayList<>();
        for(Attraction attraction : tourGuideService.getNearByAttractions(visitedLocation) ) {
            double attractionLatLon=attraction.latitude/attraction.longitude;
            double userLocationLatLon= visitedLocation.location.latitude/visitedLocation.location.longitude;
            double distanceUserAttraction= rewardsService.getDistance(attraction, visitedLocation.location);
            int userRewards= rewardsService.getRewardPoints(attraction, getUser(userName));
            nearbyAttraction= new NearbyAttractionDto(attraction.attractionName, attractionLatLon, userLocationLatLon, distanceUserAttraction, userRewards);
            attractionList.add(nearbyAttraction);
        }
        return ResponseEntity.status(200).body(attractionList);
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {

    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
    	// TODO: Get a list of every user's most recent location as JSON
    	//- Note: does not use gpsUtil to query for their current location, 
    	//        but rather gathers the user's current location from their stored location history.
    	//
    	// Return object should be the just a JSON mapping of userId to Locations similar to:
    	//     {
    	//        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    	//        ...
    	//     }

        List<User> allUsers= tourGuideService.getAllUsers();
        UserLocationDto userLocation= new UserLocationDto();
        List<UserLocationDto> userLocationDtoList=new ArrayList<>();
        LocationDto location= new LocationDto();
        for (User user : allUsers) {
            VisitedLocation visitedLocation= user.getLastVisitedLocation();
            location=new LocationDto(visitedLocation.location.longitude ,visitedLocation.location.latitude);
            userLocation=new UserLocationDto(user.getUserId().toString(), location);
            userLocationDtoList.add(userLocation);
        }

        return JsonStream.serialize(userLocationDtoList.toString());
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