package com.semaai.agent.viewmodel.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.semaai.agent.model.RVItemModel;

import java.util.List;

public class AccountViewModel extends ViewModel {

    public MutableLiveData<String> acName=new MutableLiveData<>();
    public MutableLiveData<String> acPhone=new MutableLiveData<>();
    public MutableLiveData<String> acJobTitle=new MutableLiveData<>();
    public MutableLiveData<String> acTeam=new MutableLiveData<>();
    public MutableLiveData<String> acGender=new MutableLiveData<>();
    public MutableLiveData<String> acDate=new MutableLiveData<>();
    public MutableLiveData<String> acState=new MutableLiveData<>();
    public MutableLiveData<String> acPinCode=new MutableLiveData<>();
    public MutableLiveData<String> acStreet=new MutableLiveData<>();
    public MutableLiveData<String> acEmail=new MutableLiveData<>();
    public MutableLiveData<String> acCurrentPass=new MutableLiveData<>();
    public MutableLiveData<String> acNewPass=new MutableLiveData<>();
    public MutableLiveData<String> acConfNewPass=new MutableLiveData<>();
    public MutableLiveData<String> acEnId=new MutableLiveData<>();
    public MutableLiveData<List<RVItemModel>> mutableLiveDataGenderList = new MutableLiveData<>();



    public LiveData<String> onGenderClick() {
        Log.e("TAG", "onProvinceClick: " );
        if (acGender.getValue() == null | acGender.getValue() == "" | acGender.getValue() == "visible")
            acGender.setValue("invisible");
        else
            acGender.setValue("visible");
        return acGender;
    }


}
