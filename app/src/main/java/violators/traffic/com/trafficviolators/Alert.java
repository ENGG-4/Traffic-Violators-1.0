package violators.traffic.com.trafficviolators;

import java.util.Date;

public class Alert {

    private String startUID;
    private Date startDateTime;
    private Double startLatitude;
    private Double startLongitude;

    private String closeUID;
    private String closeDateTime;
    private Double closeLatitude;
    private Double closeLongitude;

    private String vehicleNo;
    private String vehicleModel;
    private String vehicleColor;
    private String vehicleType;
    private String description;

    public Alert() {

    }

    public Alert(String startUID, Date startDateTime, Double startLatitude, Double startLongitude, String closeUID, String closeDateTime, Double closeLatitude, Double closeLongitude, String vehicleNo, String vehicleModel, String vehicleColor, String vehicleType, String description) {
        this.startUID = startUID;
        this.startDateTime = startDateTime;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.closeUID = closeUID;
        this.closeDateTime = closeDateTime;
        this.closeLatitude = closeLatitude;
        this.closeLongitude = closeLongitude;
        this.vehicleNo = vehicleNo;
        this.vehicleModel = vehicleModel;
        this.vehicleColor = vehicleColor;
        this.vehicleType = vehicleType;
        this.description = description;
    }

    public String getStartUID() {
        return startUID;
    }

    public void setStartUID(String startUID) {
        this.startUID = startUID;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public String getCloseUID() {
        return closeUID;
    }

    public void setCloseUID(String closeUID) {
        this.closeUID = closeUID;
    }

    public String getCloseDateTime() {
        return closeDateTime;
    }

    public void setCloseDateTime(String closeDateTime) {
        this.closeDateTime = closeDateTime;
    }

    public Double getCloseLatitude() {
        return closeLatitude;
    }

    public void setCloseLatitude(Double closeLatitude) {
        this.closeLatitude = closeLatitude;
    }

    public Double getCloseLongitude() {
        return closeLongitude;
    }

    public void setCloseLongitude(Double closeLongitude) {
        this.closeLongitude = closeLongitude;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehicleColor() {
        return vehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        this.vehicleColor = vehicleColor;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
