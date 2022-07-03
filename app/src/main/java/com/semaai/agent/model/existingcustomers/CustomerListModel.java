package com.semaai.agent.model.existingcustomers;

import java.io.Serializable;

public class CustomerListModel implements Serializable {
    private String name;
    private String id;
    private String phoneNumber;
    private String district;
    private String subDistricts;
    private String village;
    private String profileImage;
    private String companyName;

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public CustomerListModel()
    {

    }

    public CustomerListModel(String name,String id, String phoneNumber, String district, String subDistrict, String village, String profileImage, String companyName) {
        this.name = name;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.district = district;
        this.subDistricts = subDistrict;
        this.village = village;
        this.profileImage = profileImage;
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }

    public String getSubDistricts() {
        return subDistricts;
    }

    public void setSubDistricts(String subDistricts) {
        this.subDistricts = subDistricts;
    }

}
