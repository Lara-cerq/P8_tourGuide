package tourGuide.user;

import gpsUtil.location.Attraction;

public class Position {

    private Attraction attraction;
    private Double distanceFromUser;
    private int rewardPoints;

    public int getRewardPoints() {
        return rewardPoints;
    }
    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
    public Attraction getAttraction() {
        return attraction;
    }
    public void setAttraction(Attraction attraction) {
        this.attraction = attraction;
    }
    public Double getDistanceFromUser() {
        return distanceFromUser;
    }
    public void setDistanceFromUser(Double distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

}
