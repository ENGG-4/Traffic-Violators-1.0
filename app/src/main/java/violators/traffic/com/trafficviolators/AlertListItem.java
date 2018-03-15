package violators.traffic.com.trafficviolators;

public class AlertListItem {

    private String AlertID;
    private String VehicleNo;
    private String VehicleModel;
    private String VehicleColor;
    private int Photo;

    public AlertListItem(String alertID, String vehicleNo, String vehicleModel, String vehicleColor, int photo) {
        AlertID = alertID;
        VehicleNo = vehicleNo;
        VehicleModel = vehicleModel;
        VehicleColor = vehicleColor;
        Photo = photo;
    }

    public String getAlertID() {
        return AlertID;
    }

    public void setAlertID(String alertID) {
        AlertID = alertID;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public String getVehicleModel() {
        return VehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        VehicleModel = vehicleModel;
    }

    public String getVehicleColor() {
        return VehicleColor;
    }

    public void setVehicleColor(String vehicleColor) {
        VehicleColor = vehicleColor;
    }

    public int getPhoto() {
        return Photo;
    }

    public void setPhoto(int photo) {
        Photo = photo;
    }
}
