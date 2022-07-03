package com.semaai.agent.model;

import com.semaai.agent.model.clockinout.ClockInOutCustomerModel;
import com.semaai.agent.model.existingcustomers.CustomerDetailsModel;
import com.semaai.agent.model.existingcustomers.CustomerListModel;
import com.semaai.agent.model.existingcustomers.UpdateCustomerModel;
import com.semaai.agent.model.login.LoginDataModel;
import com.semaai.agent.model.newcustomer.AddressDataModel;
import com.semaai.agent.model.newcustomer.CameraGPSDataModel;
import com.semaai.agent.model.newcustomer.CustomerRegisterModel;
import com.semaai.agent.model.order.OrderDetailsModel;
import com.semaai.agent.model.order.OrderListModel;

import java.io.Serializable;

public class CustomerModel implements Serializable {

    private LoginDataModel loginDataModel;

    public LoginDataModel getLoginDataModel() {
        return loginDataModel;
    }

    public void setLoginDataModel(LoginDataModel loginDataModel) {
        this.loginDataModel = loginDataModel;
    }

    private CustomerRegisterModel customerRegisterModel;
    private AddressDataModel addressDataModel;
    private CameraGPSDataModel cameraGPSDataModel;
    private CustomerListModel customerListModel;
    private CustomerDetailsModel customerDetailsModel;
    private UpdateCustomerModel updateCustomerModel;
    private RVItemModel rvItemModel;
    private OrderListModel orderListModel;
    private OrderDetailsModel orderDetailsModel;
    private ClockInOutCustomerModel clockInOutCustomerModel;

    public ClockInOutCustomerModel getClockInOutCustomerModel() {
        return clockInOutCustomerModel;
    }

    public void setClockInOutCustomerModel(ClockInOutCustomerModel clockInOutCustomerModel) {
        this.clockInOutCustomerModel = clockInOutCustomerModel;
    }

    public OrderListModel getOrderListModel() {
        return orderListModel;
    }

    public void setOrderListModel(OrderListModel orderListModel) {
        this.orderListModel = orderListModel;
    }

    public OrderDetailsModel getOrderDetailsModel() {
        return orderDetailsModel;
    }

    public void setOrderDetailsModel(OrderDetailsModel orderDetailsModel) {
        this.orderDetailsModel = orderDetailsModel;
    }

    public RVItemModel getRvItemModel() {
        return rvItemModel;
    }

    public void setRvItemModel(RVItemModel rvItemModel) {
        this.rvItemModel = rvItemModel;
    }

    public UpdateCustomerModel getUpdateCustomerModel() {
        return updateCustomerModel;
    }

    public void setUpdateCustomerModel(UpdateCustomerModel updateCustomerModel) {
        this.updateCustomerModel = updateCustomerModel;
    }

    public CustomerDetailsModel getCustomerDetailsModel() {
        return customerDetailsModel;
    }

    public void setCustomerDetailsModel(CustomerDetailsModel customerDetailsModel) {
        this.customerDetailsModel = customerDetailsModel;
    }

    public CustomerListModel getCustomerListModel() {
        return customerListModel;
    }

    public void setCustomerListModel(CustomerListModel customerListModel) {
        this.customerListModel = customerListModel;
    }

    public CustomerRegisterModel getCustomerRegisterModel() {
        return customerRegisterModel;
    }

    public void setCustomerRegisterModel(CustomerRegisterModel customerRegisterModel) {
        this.customerRegisterModel = customerRegisterModel;
    }

    public AddressDataModel getAddressDataModel() {
        return addressDataModel;
    }

    public void setAddressDataModel(AddressDataModel addressDataModel) {
        this.addressDataModel = addressDataModel;
    }

    public CameraGPSDataModel getCameraGPSDataModel() {
        return cameraGPSDataModel;
    }

    public void setCameraGPSDataModel(CameraGPSDataModel cameraGPSDataModel) {
        this.cameraGPSDataModel = cameraGPSDataModel;
    }
}
