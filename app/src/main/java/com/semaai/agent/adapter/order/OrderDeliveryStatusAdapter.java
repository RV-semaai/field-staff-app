package com.semaai.agent.adapter.order;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.semaai.agent.R;
import com.semaai.agent.activity.order.OrdersListActivity;
import com.semaai.agent.databinding.ActivityCustomerOrdersBinding;
import com.semaai.agent.databinding.ItemRvOrderFilterBinding;
import com.semaai.agent.model.order.OrderDeliveryStatusModel;
import com.semaai.agent.utils.Constant;

import java.util.ArrayList;

public class OrderDeliveryStatusAdapter extends RecyclerView.Adapter<OrderDeliveryStatusAdapter.ViewHolder> {
    OrdersListActivity ordersListActivity;
    ArrayList<OrderDeliveryStatusModel> deliveryStatusArrayList;
    ActivityCustomerOrdersBinding binding;
    private String[] deliveryDataIn;


    public OrderDeliveryStatusAdapter(OrdersListActivity ordersListActivity, ArrayList<OrderDeliveryStatusModel> deliveryStatusArrayList, ActivityCustomerOrdersBinding binding) {
        this.ordersListActivity = ordersListActivity;
        this.deliveryStatusArrayList = deliveryStatusArrayList;
        this.binding = binding;
        Log.e("TAG", "onBindViewHolder: >>>>>> 0" + deliveryStatusArrayList.size());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvOrderFilterBinding rvOrderFilterItemViewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_rv_order_filter, parent, false);
        return new ViewHolder(rvOrderFilterItemViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.rvOrderFilterItemViewBinding.clSelectionItem.setVisibility(View.GONE);
        if (Constant.setLanguage.equals("in")) {
            deliveryDataIn = new String[]{"Harap Pilih", "Terkirim", "Mempersiapkan pesanan", "Menunggu penjemputan"};
            holder.rvOrderFilterItemViewBinding.tvSetItemName.setText(deliveryDataIn[position]);
        } else {
            holder.rvOrderFilterItemViewBinding.tvSetItemName.setText(deliveryStatusArrayList.get(position).getDeliveryName());
        }


        holder.rvOrderFilterItemViewBinding.clOrderfilteritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.cvDeliverStatusList.setVisibility(View.GONE);
                binding.ivDeliveryStatusUpIcon.setVisibility(View.GONE);
                binding.ivDeliveryStatusDownIcon.setVisibility(View.VISIBLE);
                if (Constant.setLanguage.equals("in")) {
                    binding.tvDeliveryStatus.setText(deliveryDataIn[position]);
                } else {
                    binding.tvDeliveryStatus.setText(deliveryStatusArrayList.get(position).getDeliveryName());
                }

                binding.tvDeliveryStatus.setTextColor(Color.parseColor("#000000"));
                Constant.isSelectDeliveryStatus = position;
                notifyDataSetChanged();
                if (position == 0) {
                    Constant.deliveryStatus = deliveryStatusArrayList.get(position).getDeliveryState();
                } else {
                    Constant.deliveryStatus = "[\"delivery-status\",[\"" + deliveryStatusArrayList.get(position).getDeliveryName() + "\"]]";
                }
                Log.e("TAG", "onClick:delivery >>>>>>>>>>>>>>>" + Constant.deliveryStatus);
            }
        });


        if (Constant.isSelectDeliveryStatus == position) {
            holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.parseColor("#000000"));
        } else {
            holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.parseColor("#F39404"));
        }
        Log.e("TAG", "onBindViewHolder: >>>>>> 1" + deliveryStatusArrayList.size());
    }

    @Override
    public int getItemCount() {
        Log.e("TAG", "onBindViewHolder: >>>>>> 2");
        return deliveryStatusArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemRvOrderFilterBinding rvOrderFilterItemViewBinding;

        public ViewHolder(ItemRvOrderFilterBinding rvOrderFilterItemViewBinding) {
            super(rvOrderFilterItemViewBinding.getRoot());
            this.rvOrderFilterItemViewBinding = rvOrderFilterItemViewBinding;
        }
    }
}
