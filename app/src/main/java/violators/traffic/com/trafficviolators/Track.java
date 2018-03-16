package violators.traffic.com.trafficviolators;

import java.util.Date;

public class Track {
    private String UID;
    private Date DateTime;
    private Double Latitude;
    private Double Longitude;

    public Track() {
    }

    public Track(String UID, Date dateTime, Double latitude, Double longitude) {
        this.UID = UID;
        DateTime = dateTime;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public Date getDateTime() {
        return DateTime;
    }

    public void setDateTime(Date dateTime) {
        DateTime = dateTime;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public void setLatitude(Double latitude) {
        Latitude = latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }
}
