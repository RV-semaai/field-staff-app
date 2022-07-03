package com.semaai.agent.model.order;

import java.io.Serializable;

public class OrderDetailsModel implements Serializable {
    String id;
    String date;
    String invoiceId;
    String paymentStatus;
    String paymentMethod;
    String deliveryStatus;
    String deliveryDate;
    String salesperson;
    String untaxedAmount;
    String tax;
    String totalAmount;
    String productDetail;
    String loyaltyTotal;

    //order in order details
    String productId;
    String productCatName;
    String productName;
    String referenceCode;
    String marketplaceSeller;
    String image;
    String productQty;
    String originalPrice;
    String pricePaid;

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }

    public String getMarketplaceSeller() {
        return marketplaceSeller;
    }

    public void setMarketplaceSeller(String marketplaceSeller) {
        this.marketplaceSeller = marketplaceSeller;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCatName() {
        return productCatName;
    }

    public void setProductCatName(String productCatName) {
        this.productCatName = productCatName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getPricePaid() {
        return pricePaid;
    }

    public void setPricePaid(String pricePaid) {
        this.pricePaid = pricePaid;
    }

    public OrderDetailsModel(String productId, String productCatName, String productName, String referenceCode,String marketplaceSeller,String image, String productQty, String originalPrice, String pricePaid) {
        this.productId = productId;
        this.productCatName = productCatName;
        this.productName = productName;
        this.referenceCode = referenceCode;
        this.marketplaceSeller = marketplaceSeller;
        this.image = image;
        this.productQty = productQty;
        this.originalPrice = originalPrice;
        this.pricePaid = pricePaid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getSalesperson() {
        return salesperson;
    }

    public void setSalesperson(String salesperson) {
        this.salesperson = salesperson;
    }

    public String getUntaxedAmount() {
        return untaxedAmount;
    }

    public void setUntaxedAmount(String untaxedAmount) {
        this.untaxedAmount = untaxedAmount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(String productDetail) {
        this.productDetail = productDetail;
    }


    public String getLoyaltyTotal() {
        return loyaltyTotal;
    }

    public void setLoyaltyTotal(String loyalty_total) {
        this.loyaltyTotal = loyaltyTotal;
    }

    public OrderDetailsModel(String id, String date, String invoiceId, String paymentStatus, String paymentMethod, String deliveryStatus, String deliveryDate, String salesperson, String untaxedAmount, String tax, String totalAmount, String productDetail, String loyaltyTotal) {

        this.id = id;
        this.date = date;
        this.invoiceId = invoiceId;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.deliveryStatus = deliveryStatus;
        this.deliveryDate = deliveryDate;
        this.salesperson = salesperson;
        this.untaxedAmount = untaxedAmount;
        this.tax = tax;
        this.totalAmount = totalAmount;
        this.productDetail = productDetail;

        this.loyaltyTotal = loyaltyTotal;

    }
}
