package com.semaai.agent.model.clockinout;

import java.io.Serializable;

public class ClockInOutCustomerModel implements Serializable {

    String newCustomer;
    String id;

    public String getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(String newCustomer) {
        this.newCustomer = newCustomer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
