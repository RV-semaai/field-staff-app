package com.semaai.agent.model.order;

public class OrderSellerNameModel {

    String sellerId;
    String sellerName;


    public OrderSellerNameModel(String sellerId, String sellerName) {
        this.sellerId = sellerId;
        this.sellerName = sellerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }


}
