package com.semaai.agent.model.order;

public class OrderPaymentStatusModel {


    String paymentState;
    String paymentStateName;

    public OrderPaymentStatusModel(String paymentState, String paymentStateName) {
        this.paymentState = paymentState;
        this.paymentStateName = paymentStateName;
    }

    public OrderPaymentStatusModel() {

    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getPaymentStateName() {
        return paymentStateName;
    }

    public void setPaymentStateName(String paymentStateName) {
        this.paymentStateName = paymentStateName;
    }

}
