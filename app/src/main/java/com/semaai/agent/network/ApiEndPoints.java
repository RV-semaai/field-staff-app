package com.semaai.agent.network;


import com.semaai.agent.BuildConfig;

public class ApiEndPoints {


    public static String baseURL = BuildConfig.BASE_URL;

    // Loyalty
    public static String mobikulAddUser = baseURL + "res-user";

    public static String logInURL = baseURL + "field-app/login";
    public static String customerURL = baseURL + "field-app/customer";
    public static String statesURL = baseURL + "states?company_id=";
    public static String statesURLex = baseURL + "states";
    public static String districtURL = baseURL + "districts?state_id=";
    public static String subDistrictURL = baseURL + "subdistricts?district_id=";
    public static String villageURL = baseURL + "villages?sub_district_id=";
    public static String groupURL = baseURL + "field-app/customer/group";
    public static String customerApproveURL = baseURL + "field-app/approve-customer";

    public static String existInfCustomer = baseURL + "field-app/customers";
    public static String existingCustomerDetails = baseURL + "field-app/customer/";
    public static String updateCustomer = baseURL + "field-app/customer";
    public static String orders = baseURL + "field-app/orders";
    public static String orderDetails = baseURL + "field-app/order/";
    public static String salesOrder = baseURL + "field-app/analytics/sales-orders";
    public static String deliveryStatus = baseURL + "field-app/delivery";
    public static String sellers = baseURL + "field-app/sellers";
    public static String paymentStatus = baseURL + "field-app/payment";

    public static String products = baseURL + "field-app/products";
    public static String productBrand = baseURL + "field-app/product-brands";
    public static String productCategory = baseURL + "field-app/product-category";
    public static String productSubCategory = baseURL + "field-app/product-sub-category";
    public static String profile = baseURL + "field-app/profile/";
    public static String password = baseURL + "field-app/password-update";

    public static String clockInOut = baseURL+ "field-app/attendance";
    public static String challengesGoals = baseURL+ "field-app/challenges";

}
