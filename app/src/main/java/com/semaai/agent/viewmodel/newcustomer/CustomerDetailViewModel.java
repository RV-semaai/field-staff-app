package com.semaai.agent.viewmodel.newcustomer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.semaai.agent.model.existingcustomers.CustomerDetailsModel;

public class CustomerDetailViewModel extends ViewModel {
    MutableLiveData<String> name= new MutableLiveData<>();
    MutableLiveData<String> number= new MutableLiveData<>();
    MutableLiveData<String> group= new MutableLiveData<>();
    MutableLiveData<String> street= new MutableLiveData<>();
    MutableLiveData<String> address= new MutableLiveData<>();
    MutableLiveData<CustomerDetailsModel> customerDetailsModelMutableLiveData;

    public MutableLiveData<CustomerDetailsModel> getAc_user()
    {
        if (customerDetailsModelMutableLiveData ==null)
        {
            customerDetailsModelMutableLiveData=new MutableLiveData<>();
        }
        return customerDetailsModelMutableLiveData;
    }
}
