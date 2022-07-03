package com.semaai.agent.viewmodel.newcustomer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.semaai.agent.model.RVItemModel;

import java.util.List;

public class AddressViewModel extends ViewModel {
    String TAG = AddressViewModel.class.getSimpleName() + "-->";
    String viewID;


    public MutableLiveData<String> Province = new MutableLiveData<>();
    public MutableLiveData<String> District = new MutableLiveData<>();
    public MutableLiveData<String> County = new MutableLiveData<>();
    public MutableLiveData<String> Village = new MutableLiveData<>();
    public MutableLiveData<String> PostCode = new MutableLiveData<>();
    public MutableLiveData<String> StreetName = new MutableLiveData<>();
    public MutableLiveData<String> Group = new MutableLiveData<>();
    public MutableLiveData<List<RVItemModel>> mutableLiveDataProvinceList = new MutableLiveData<>();
    public MutableLiveData<List<RVItemModel>> mutableLiveDataDistrictList = new MutableLiveData<>();
    public MutableLiveData<List<RVItemModel>> mutableLiveDataCountyList = new MutableLiveData<>();
    public MutableLiveData<List<RVItemModel>> mutableLiveDataVillageList = new MutableLiveData<>();
    public MutableLiveData<List<RVItemModel>> mutableLiveDataGroupList = new MutableLiveData<>();


}
