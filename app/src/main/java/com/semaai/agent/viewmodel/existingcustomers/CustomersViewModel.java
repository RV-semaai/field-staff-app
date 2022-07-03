package com.semaai.agent.viewmodel.existingcustomers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.semaai.agent.model.RVItemModel;

import java.util.ArrayList;
import java.util.List;

public class CustomersViewModel extends ViewModel {

    String TAG = CustomersViewModel.class.getSimpleName() + "-->";

    public MutableLiveData<String> ecSort = new MutableLiveData<>();

    public MutableLiveData<List<RVItemModel>> mutableLiveDataEcSortList = new MutableLiveData<>();

    public LiveData<List<RVItemModel>> getMutableLiveDataEcSortList(){
        List<RVItemModel> datModelList = new ArrayList<>();
        datModelList.add(new RVItemModel("Name(A-Z)",0,"352"));
        datModelList.add(new RVItemModel("Name(Z-A)",0,"325"));
        mutableLiveDataEcSortList.setValue(datModelList);
        return mutableLiveDataEcSortList;
    }

}
