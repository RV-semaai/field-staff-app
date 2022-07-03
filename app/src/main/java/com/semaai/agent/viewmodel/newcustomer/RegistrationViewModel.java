package com.semaai.agent.viewmodel.newcustomer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.semaai.agent.model.newcustomer.CustomerRegisterModel;

public class RegistrationViewModel extends ViewModel {
    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> phone = new MutableLiveData<>();
    public MutableLiveData<CustomerRegisterModel> registerUserData;

    public MutableLiveData<CustomerRegisterModel> getUser() {
        if (registerUserData == null) {
            registerUserData = new MutableLiveData<>();
        }
        return registerUserData;
    }
}
