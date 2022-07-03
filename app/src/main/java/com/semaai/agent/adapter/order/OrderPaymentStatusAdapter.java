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
import com.semaai.agent.model.order.OrderPaymentStatusModel;
import com.semaai.agent.utils.Constant;

import java.util.ArrayList;

public class OrderPaymentStatusAdapter extends RecyclerView.Adapter<OrderPaymentStatusAdapter.ViewHolder> {
    OrdersListActivity ordersListActivity;
    ArrayList<OrderPaymentStatusModel> paymentStatusArrayList;
    ActivityCustomerOrdersBinding binding;
    private String[] paymentDataIn;

    public OrderPaymentStatusAdapter(OrdersListActivity ordersListActivity, ArrayList<OrderPaymentStatusModel> paymentStatusArrayList, ActivityCustomerOrdersBinding binding) {
        this.ordersListActivity = ordersListActivity;
        this.paymentStatusArrayList = paymentStatusArrayList;
        this.binding = binding;
        Log.e("TAG", "onBindViewHolder: >>>>>> 0" + paymentStatusArrayList.size());
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
            paymentDataIn = new String[]{"Harap Pilih", "Belum dibayar", "Sudah dibayar", "Dibayar sebagian"};
            holder.rvOrderFilterItemViewBinding.tvSetItemName.setText(paymentDataIn[position]);
        } else {
            holder.rvOrderFilterItemViewBinding.tvSetItemName.setText(paymentStatusArrayList.get(position).getPaymentStateName());
        }

        Log.e("TAG", "onBindViewHolder: >>>>>> 1" + paymentStatusArrayList.size());
        holder.rvOrderFilterItemViewBinding.clOrderfilteritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Constant.setLanguage.equals("in")) {
                    binding.tvPaymentStatus.setText(paymentDataIn[position]);
                } else {
                    binding.tvPaymentStatus.setText(paymentStatusArrayList.get(position).getPaymentStateName());
                }
                binding.tvPaymentStatus.setTextColor(Color.parseColor("#000000"));
                binding.cvPaymentStatusList.setVisibility(View.GONE);
                binding.ivPaymentStatusUpIcon.setVisibility(View.GONE);
                binding.ivPaymentStatusDownIcon.setVisibility(View.VISIBLE);
                Constant.isSelectPaymentStatus = position;
                notifyDataSetChanged();
                Log.e("TAG", "onClick: chek pos >>>>>>>>>>>>>>" + position);
                if (position == 0) {
                    Constant.paymentStatus = "";
                } else {
                    Constant.paymentStatus = "[\"payment-status\",[\"" + paymentStatusArrayList.get(position).getPaymentStateName() + "\"]]";
                }
                Log.e("TAG", "onClick:payment >>>>>>>>>>>>>>>" + Constant.paymentStatus);
            }
        });


        if (Constant.isSelectPaymentStatus == position) {
            holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.parseColor("#000000"));
        } else {
            holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.parseColor("#F39404"));
        }
    }

    @Override
    public int getItemCount() {
        Log.e("TAG", "onBindViewHolder: >>>>>> 2");
        return paymentStatusArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemRvOrderFilterBinding rvOrderFilterItemViewBinding;

        public ViewHolder(ItemRvOrderFilterBinding rvOrderFilterItemViewBinding) {
            super(rvOrderFilterItemViewBinding.getRoot());
            this.rvOrderFilterItemViewBinding = rvOrderFilterItemViewBinding;
        }
    }
}
