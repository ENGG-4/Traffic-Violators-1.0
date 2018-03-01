package violators.traffic.com.trafficviolators;

import android.net.Uri;

import java.net.URL;
import java.util.Date;

/**
 * Created by Ivan D'Souza on 01-Mar-18.
 */

public class Report {

    private String reportID;
    private String userID;
    private String licenseNo;
    private String reason;
    private String description;
    private int fine;
    private Date datetime;
    private String photoURL;

    public Report(String reportID, String licenseNo, String reason, String description, int fine, Date datetime, String photoURL,String userID) {
        this.reportID = reportID;
        this.licenseNo = licenseNo;
        this.reason = reason;
        this.description = description;
        this.fine = fine;
        this.datetime = datetime;
        this.photoURL = photoURL;
        this.userID = userID;
    }

    public String getReportID() {
        return reportID;
    }

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFine() {
        return fine;
    }

    public void setFine(int fine) {
        this.fine = fine;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getPhoto() {
        return photoURL;
    }

    public void setPhoto(String photoURL) {
        this.photoURL = photoURL;
    }
}
