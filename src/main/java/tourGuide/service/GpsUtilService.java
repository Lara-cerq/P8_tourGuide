package tourGuide.service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.stereotype.Service;
import tourGuide.user.User;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GpsUtilService {

	private GpsUtil gpsUtil;
	
	private ExecutorService executor = Executors.newFixedThreadPool(10000); 

	public GpsUtilService() {
		gpsUtil = new GpsUtil();
	}
	
	public List<Attraction> getAttractions() {
		return gpsUtil.getAttractions();
	}
	public VisitedLocation getUserLocation(UUID userId) {
		try {   
			 gpsUtil.getUserLocation(userId);
			} catch (NumberFormatException nfe) {
			
			}
		return gpsUtil.getUserLocation(userId);
	}
	
//	public void userLocation(List<User> userList, TourGuideService tourGuideService) {
//		ExecutorService executor = Executors.newFixedThreadPool(1000); 
//		for (User user : userList) {
//			Runnable runnableTask = () -> {
//				tourGuideService.getUserLocation(user);
//			};
//			executor.execute(runnableTask);
//		}
//		executor.shutdown();
//	}
	
	public void userLocation(User user, TourGuideService tourGuideService) {
		CompletableFuture.supplyAsync(() -> {
		    return gpsUtil.getUserLocation(user.getUserId());
		}, executor)
			.thenAccept(visitedLocation -> { tourGuideService.trackUserLocation(user); });
	}

}
