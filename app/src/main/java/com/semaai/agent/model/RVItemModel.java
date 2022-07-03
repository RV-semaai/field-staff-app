package com.semaai.agent.model;

public class RVItemModel {
    public String item;
    public Number id;
    public String zipCode;

    public RVItemModel(String item, Number id, String zipCode) {
        this.item = item;
        this.id = id;
        this.zipCode = zipCode;
    }

    public RVItemModel() {
        this.item = "item";
        this.id = 0;
    }

    public RVItemModel(String item, Number id) {
        this.item = item;
        this.id = id;
    }

    public Number getId() {
        return id;
    }

    public void setId(Number id) {
        this.id = id;
    }


    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
