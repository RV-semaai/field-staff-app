package com.semaai.agent.model.newcustomer;

import java.io.Serializable;

public class CameraGPSDataModel implements Serializable {

    String imgPath;
    double latitude;
    double longitude;

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public CameraGPSDataModel() {

    }
}
