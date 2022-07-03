package com.semaai.agent.model.order;

public class OrderDeliveryStatusModel {

    String deliveryState;
    String deliveryName;

    public OrderDeliveryStatusModel(String deliveryState, String deliveryName) {
        this.deliveryState = deliveryState;
        this.deliveryName = deliveryName;
    }

    public OrderDeliveryStatusModel() {

    }

    public String getDeliveryState() {
        return deliveryState;
    }

    public void setDeliveryState(String deliveryState) {
        this.deliveryState = deliveryState;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }


}
