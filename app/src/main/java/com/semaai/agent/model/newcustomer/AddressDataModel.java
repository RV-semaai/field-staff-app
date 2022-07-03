package com.semaai.agent.model.newcustomer;

import java.io.Serializable;

public class AddressDataModel implements Serializable {

    private String Province;
    private String District;
    private String SubDistrict;
    private String Village;
    private String PostCode;
    private String StreetName;
    private String Group;
    private String ProvinceId;
    private String DistrictId;
    private String SubDistrictId;
    private String VillageId;
    private String GroupId;

    public String getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(String provinceId) {
        ProvinceId = provinceId;
    }

    public String getDistrictId() {
        return DistrictId;
    }

    public void setDistrictId(String districtId) {
        DistrictId = districtId;
    }

    public String getSubDistrictId() {
        return SubDistrictId;
    }

    public void setSubDistrictId(String subDistrictId) {
        SubDistrictId = subDistrictId;
    }

    public String getVillageId() {
        return VillageId;
    }

    public void setVillageId(String villageId) {
        VillageId = villageId;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getSubDistrict() {
        return SubDistrict;
    }

    public void setSubDistrict(String subDistrict) {
        SubDistrict = subDistrict;
    }

    public String getVillage() {
        return Village;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public String getPostCode() {
        return PostCode;
    }

    public void setPostCode(String postCode) {
        PostCode = postCode;
    }

    public String getStreetName() {
        return StreetName;
    }

    public void setStreetName(String streetName) {
        StreetName = streetName;
    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }

    public AddressDataModel()
    {

    }
}
