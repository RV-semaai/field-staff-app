package com.semaai.agent.model.order;

public class ProductsListModel {

    String productName;
    String productBrand;
    String productCategory;
    String productCustomerPrice;
    String stock;
    String productImage;
    String lastPurchasedQty;
    String totalPurchasedProductQty;
    String salesperson;
    String lastPurchasedDate;
    String lastPurchasedPrice;
    String defaultCode;

    public String getProductName() {
        return productName;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public String getDefaultCode() {
        return defaultCode;
    }

    public String getProductCustomerPrice() {
        return productCustomerPrice;
    }

    public String getStock() {
        return stock;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getLastPurchasedQty() {
        return lastPurchasedQty;
    }

    public String getTotalPurchasedProductQty() {
        return totalPurchasedProductQty;
    }

    public String getSalesperson() {
        return salesperson;
    }

    public void setSalesperson(String salesperson) {
        this.salesperson = salesperson;
    }

    public String getLastPurchasedDate() {
        return lastPurchasedDate;
    }

    public String getLastPurchasedPrice() {
        return lastPurchasedPrice;
    }

    public ProductsListModel(String productName, String productBrand, String productCategory, String productCustomerPrice, String stock, String productImage, String lastPurchasedQty, String totalPurchasedProductQty,String defaultCode, String salesperson, String lastPurchasedDate, String lastPurchasedPrice) {
        this.productName = productName;
        this.productBrand = productBrand;
        this.productCategory = productCategory;
        this.productCustomerPrice = productCustomerPrice;
        this.stock = stock;
        this.productImage = productImage;
        this.lastPurchasedQty = lastPurchasedQty;
        this.totalPurchasedProductQty = totalPurchasedProductQty;
        this.defaultCode = defaultCode;
        this.salesperson = salesperson;
        this.lastPurchasedDate = lastPurchasedDate;
        this.lastPurchasedPrice = lastPurchasedPrice;

    }
}
