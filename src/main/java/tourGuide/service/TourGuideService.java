package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.dto.FiveNearAttractionByUserDto;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtilService gpsUtil;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;
	private List<VisitedLocation> visitedLocations = new ArrayList<>();
	private List<Provider> tripDeals = new ArrayList<>();
	private Lock userLocationListLock= new ReentrantLock();
	
	public TourGuideService(GpsUtilService gpsUtil, RewardsService rewardsService) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		
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
	
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}
	
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}
	
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	public void addUser(User user) {
		if(!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}
	
	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(), user.getUserPreferences().getNumberOfAdults(), 
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}
	
	public VisitedLocation trackUserLocation(User user) {
		Location location = new Location(generateRandomLatitude(), generateRandomLongitude());
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), location, getRandomTime());
		try {
			visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		} catch (NumberFormatException nfe) {
		}
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	public void userLocation(User user) {
		gpsUtil.userLocation(user, this);
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		ArrayList<Attraction> nearbyAttractions = new ArrayList<>();
		List<Attraction> nearFiveByAttractions = new ArrayList<>();
		for (Attraction attraction : gpsUtil.getAttractions()) {
			if (rewardsService.isWithinAttractionProximityDistance(attraction, visitedLocation.location)>0) {
				nearbyAttractions.add(attraction);
			}
		}
		for (int i = 0; i <5; i++) {
			Attraction attractionInProximity = nearbyAttractions.get(i);
			nearFiveByAttractions.add(attractionInProximity);
		}
		return nearFiveByAttractions;
	}

	public List<FiveNearAttractionByUserDto> getFiveNearAttractions(User user) {
		List<Attraction> attractions = gpsUtil.getAttractions();
		List<FiveNearAttractionByUserDto> fiveNearAttractionByUserDtoList = new ArrayList<FiveNearAttractionByUserDto>();
		for (Attraction attraction : attractions) {
			FiveNearAttractionByUserDto fiveNearAttractionByUserDto = new FiveNearAttractionByUserDto();
			VisitedLocation lastLocation = user.getLastVisitedLocation();
			Double distanceFromUser = rewardsService.getDistance(lastLocation.location, attraction);
			double attractionLatitude = attraction.latitude;
			double attractionLongitude = attraction.longitude;

			int rewardPoints = rewardsService.getRewardPoints(attraction, user);

			fiveNearAttractionByUserDto = new FiveNearAttractionByUserDto(attraction.attractionName,attractionLatitude,attractionLongitude,lastLocation.location.latitude,lastLocation.location.longitude, distanceFromUser, rewardPoints);

			fiveNearAttractionByUserDtoList.add(fiveNearAttractionByUserDto);
		}

		Collections.sort(fiveNearAttractionByUserDtoList, new Comparator<FiveNearAttractionByUserDto>(){
			public int compare(FiveNearAttractionByUserDto P1, FiveNearAttractionByUserDto P2) {
				return P1.getDistanceFromUser().compareTo(P2.getDistanceFromUser());
			}
		});
		return fiveNearAttractionByUserDtoList.stream().limit(5).collect(Collectors.toList());
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
	private final Map<String, User> internalUserMap = new HashMap<>();
	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);
			
			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}
	
	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(), new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}
	
	private double generateRandomLongitude() {
		double leftLimit = -180;
	    double rightLimit = 180;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
	    double rightLimit = 85.05112878;
	    return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}
	
	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
	    return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}
	
}
