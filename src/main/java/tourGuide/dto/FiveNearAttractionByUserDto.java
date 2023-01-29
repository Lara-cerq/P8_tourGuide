package tourGuide.dto;

public class FiveNearAttractionByUserDto
{
    private String attractionName;
    private double attractionLatitude;
    private double attractionLongitude;
    private double userLatitude;
    private double userLongitude;
    private Double distanceFromUser;
    private int rewardPoints;

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public void setAttractionLatitude(double attractionLatitude) {
        this.attractionLatitude = attractionLatitude;
    }

    public void setAttractionLongitude(double attractionLongitude) {
        this.attractionLongitude = attractionLongitude;
    }

    public void setUserLatitude(double userLatitude) {
        this.userLatitude = userLatitude;
    }

    public void setUserLongitude(double userLongitude) {
        this.userLongitude = userLongitude;
    }

    public void setDistanceFromUser(Double distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public double getAttractionLatitude() {
        return attractionLatitude;
    }

    public double getAttractionLongitude() {
        return attractionLongitude;
    }

    public double getUserLatitude() {
        return userLatitude;
    }

    public double getUserLongitude() {
        return userLongitude;
    }

    public Double getDistanceFromUser() {
        return distanceFromUser;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public FiveNearAttractionByUserDto(String attractionName, double attractionLatitude, double attractionLongitude, double userLatitude, double userLongitude, Double distanceFromUser, int rewardPoints) {
        this.attractionName = attractionName;
        this.attractionLatitude = attractionLatitude;
        this.attractionLongitude = attractionLongitude;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.distanceFromUser = distanceFromUser;
        this.rewardPoints = rewardPoints;
    }

    public FiveNearAttractionByUserDto() {
    }

    @Override
    public String toString() {
        return "Tourist attraction:" + attractionName + ", Tourist attractions latitude :" + attractionLatitude + ", Tourist attractions longitude :" + attractionLongitude
                + ", User's location latitude:" + userLatitude + ", User's location longitude:" + userLongitude + ", Distance between the user's location and attraction:" + distanceFromUser + ", Reward Points:"
                + rewardPoints;
    }
}

