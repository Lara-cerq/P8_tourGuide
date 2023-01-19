package tourGuide.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int proximityMilesBuffer = 10;

	private int attractionProximityRange = 200;

	private ExecutorService executor = Executors.newFixedThreadPool(100000
	);
	private final GpsUtilService gpsUtil;
	private final RewardCentral rewardsCentral;
	
	public RewardsService(GpsUtilService gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
	}
	
	public void setProximityMilesBuffer(int proximityMilesBuffer) {
		this.proximityMilesBuffer = proximityMilesBuffer;
	}

	public void calculateRewards(User user) {
		List<Attraction> attractions = gpsUtil.getAttractions();
		List<VisitedLocation> visitedLocationList = user.getVisitedLocations();
		for(VisitedLocation visitedLocation : visitedLocationList) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					getUserReward(user, visitedLocation, attraction);
				}
			}
		}
	}

	public void getUserReward(User user, VisitedLocation visitedLocation, Attraction attraction) {
		Double distance = getDistance(attraction, visitedLocation.location);
		if(distance <= proximityMilesBuffer) {
			UserReward userReward = new UserReward(visitedLocation, attraction, distance.intValue());
			setRewardPoints(userReward, attraction, user);
		}
	}

	private void setRewardPoints(UserReward userReward, Attraction attraction, User user) {
		CompletableFuture.supplyAsync(() -> {
					return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
				}, executor)
				.thenAccept(points -> {
					userReward.setRewardPoints(points);
					user.addUserReward(userReward);
					executor.shutdown();
				});
	}


	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	public double isWithinAttractionProximityDistance(Attraction attraction, Location location) {
		return getDistance(attraction, location);
	}

	public boolean isAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > 0 ? false : true;
	}
	
	public boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityMilesBuffer ? false : true;
	}

	// change this to public ?
	public int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}
	
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
	}

}
