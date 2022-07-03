package com.semaai.agent.model.order;

import java.io.Serializable;

public class OrderListModel implements Serializable {

    String id;
    String salesperson;
    String date;
    String invoiceId;
    String paymentStatus;
    String orderStatus;
    String amount;
    String image;


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public OrderListModel() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSalesperson() {
        return salesperson;
    }

    public void setSalesperson(String salesperson) {
        this.salesperson = salesperson;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public OrderListModel(String id, String salesperson, String date, String invoiceId, String paymentStatus, String orderStatus, String amount,String image) {
        this.id = id;
        this.salesperson = salesperson;
        this.date = date;
        this.invoiceId = invoiceId;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.amount = amount;
        this.image=image;
    }
}
