package com.semaai.agent.adapter.existingcustomers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.semaai.agent.R;
import com.semaai.agent.activity.clockinout.NewCustomerActivity;
import com.semaai.agent.activity.clockinout.StorePhotoAndGPSActivity;
import com.semaai.agent.activity.existingcustomers.CustomersListActivity;
import com.semaai.agent.activity.existingcustomers.MyCustomersActivity;
import com.semaai.agent.databinding.ItemCustomerListBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.clockinout.ClockInOutCustomerModel;
import com.semaai.agent.model.existingcustomers.CustomerListModel;
import com.semaai.agent.utils.Constant;

import java.util.ArrayList;

public class CustomerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<CustomerListModel> nameList;
    CustomersListActivity customersListActivity;
    LayoutInflater inflater;

    public CustomerListAdapter(ArrayList<CustomerListModel> nameList, CustomersListActivity customersListActivity) {
        this.nameList = nameList;
        this.customersListActivity = customersListActivity;
        this.inflater = LayoutInflater.from(customersListActivity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerListBinding itemCustomerListBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_customer_list, parent, false);
        return new ViewHolder(itemCustomerListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        if (Constant.clockInClick == 1 || Constant.clockInClick == 2) {
            viewHolder.itemCustomerListBinding.ivClockIn.setVisibility(View.VISIBLE);
        }
        String mask = nameList.get(position).getPhoneNumber().replaceAll("\\w(?=\\w{3})", "*");
        viewHolder.itemCustomerListBinding.tvName.setText(nameList.get(position).getName());
        viewHolder.itemCustomerListBinding.tvNumber.setText(mask);
        viewHolder.itemCustomerListBinding.tvCompany.setText(nameList.get(position).getCompanyName());
        viewHolder.itemCustomerListBinding.tvDistrict.setText(nameList.get(position).getDistrict());
        viewHolder.itemCustomerListBinding.tvSubDistrict.setText(nameList.get(position).getSubDistricts());
        viewHolder.itemCustomerListBinding.tvVillage.setText(nameList.get(position).getVillage());
        viewHolder.itemCustomerListBinding.cvCustomerItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.clockInClick == 1) {
                    ClockInOutCustomerModel clockInOutCustomerModel = new ClockInOutCustomerModel();
                    CustomerModel customerModel = new CustomerModel();
                    clockInOutCustomerModel.setNewCustomer(nameList.get(position).getName());
                    clockInOutCustomerModel.setId(nameList.get(position).getId());
                    customerModel.setClockInOutCustomerModel(clockInOutCustomerModel);
                    Constant.actionFor = "existing-customer";
                    Intent intent = new Intent(customersListActivity, StorePhotoAndGPSActivity.class);
                    intent.putExtra("sample", customerModel);
                    customersListActivity.startActivity(intent);
                } else if (Constant.clockInClick == 2) {
                    ClockInOutCustomerModel clockInOutCustomerModel = new ClockInOutCustomerModel();
                    clockInOutCustomerModel.setNewCustomer(nameList.get(position).getName());
                    clockInOutCustomerModel.setId(nameList.get(position).getId());
                    NewCustomerActivity.customerModel.setClockInOutCustomerModel(clockInOutCustomerModel);
                    Constant.actionFor = "existing-customer";
                    Constant.clockInChangeNameNull = false;
                    Intent returnIntent = new Intent();
                    customersListActivity.setResult(Activity.RESULT_OK, returnIntent);
                    customersListActivity.finish();
                } else {
                    CustomerModel customerModel = new CustomerModel();
                    CustomerListModel customerListModel = new CustomerListModel();
                    customerListModel.setId(nameList.get(position).getId());
                    customerListModel.setName(nameList.get(position).getName());
                    customerListModel.setPhoneNumber(nameList.get(position).getPhoneNumber());
                    customerListModel.setDistrict(nameList.get(position).getDistrict());
                    customerListModel.setSubDistricts(nameList.get(position).getSubDistricts());
                    customerListModel.setVillage(nameList.get(position).getVillage());
                    customerListModel.setProfileImage(nameList.get(position).getProfileImage());
                    customerModel.setCustomerListModel(customerListModel);
                    Intent intent = new Intent(customersListActivity, MyCustomersActivity.class);
                    intent.putExtra("sample", customerModel);
                    customersListActivity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCustomerListBinding itemCustomerListBinding;

        public ViewHolder(ItemCustomerListBinding itemCustomerListBinding) {
            super(itemCustomerListBinding.getRoot());
            this.itemCustomerListBinding = itemCustomerListBinding;
        }
    }
}
