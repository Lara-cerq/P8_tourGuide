package tourGuide;

public class LocationDto {

    double longitude;

    double latitude;
    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public LocationDto() {
    }

    public LocationDto(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return '{' +
                "\'longitude\'" + ":"+ longitude + ',' +
                        "\'latitude\'" + ":"+ latitude +
                '}';
    }
}
