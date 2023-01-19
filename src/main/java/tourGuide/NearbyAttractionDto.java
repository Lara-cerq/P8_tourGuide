package tourGuide;

import java.util.List;

import tourGuide.user.UserReward;

public class NearbyAttractionDto {
	
	private String attractionName;
	
	private double attractionLatLon;
	
	private double userLatLon;
	
	private double distanceAttractionUser;
	
	private int userRewards;

	public int getUserRewards() {
		return userRewards;
	}

	public void setUserRewards(int userRewards) {
		this.userRewards = userRewards;
	}

	public String getAttractionName() {
		return attractionName;
	}

	public void setAttractionName(String attractionName) {
		this.attractionName = attractionName;
	}

	public double getAttractionLatLon() {
		return attractionLatLon;
	}

	public void setAttractionLatLon(double attractionLatLon) {
		this.attractionLatLon = attractionLatLon;
	}

	public double getUserLatLon() {
		return userLatLon;
	}

	public void setUserLatLon(double userLatLon) {
		this.userLatLon = userLatLon;
	}

	public double getDistanceAttractionUser() {
		return distanceAttractionUser;
	}

	public void setDistanceAttractionUser(double distanceAttractionUser) {
		this.distanceAttractionUser = distanceAttractionUser;
	}

	public NearbyAttractionDto(String attractionName, double attractionLatLon, double userLatLon,
			double distanceAttractionUser, int userRewards) {
		super();
		this.attractionName = attractionName;
		this.attractionLatLon = attractionLatLon;
		this.userLatLon = userLatLon;
		this.distanceAttractionUser = distanceAttractionUser;
		this.userRewards = userRewards;
	}

	public NearbyAttractionDto() {
		super();
	}

	@Override
	public String toString() {
		return "Tourist attraction:" + attractionName + ", Tourist attractions lat/long :" + attractionLatLon
				+ ", User's location lat/long:" + userLatLon + ", Distance between the user's location and attraction:" + distanceAttractionUser + ", Reward Points:"
				+ userRewards;
	}

}
