package com.semaai.agent.viewmodel.clockinout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.semaai.agent.model.clockinout.ClockInOutCustomerModel;

public class OtherActivityViewModel extends ViewModel {

    public MutableLiveData<String> clockIn=new MutableLiveData<>();
    public MutableLiveData<String> etInputName=new MutableLiveData<>();
    public MutableLiveData<String> etInputLocation=new MutableLiveData<>();
    public MutableLiveData<ClockInOutCustomerModel> customerModel;

    public MutableLiveData<ClockInOutCustomerModel> getUser() {

        if (customerModel == null) {
            customerModel = new MutableLiveData<>();
        }
        return customerModel;
    }
}
