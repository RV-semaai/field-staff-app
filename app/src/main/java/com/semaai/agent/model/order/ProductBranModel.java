package com.semaai.agent.model.order;

public class ProductBranModel {

    String id;
    String brandName;

    public String getId() {
        return id;
    }

    public String getBrandName() {
        return brandName;
    }

    public ProductBranModel(String id, String brandName) {
        this.id = id;
        this.brandName = brandName;
    }
}
