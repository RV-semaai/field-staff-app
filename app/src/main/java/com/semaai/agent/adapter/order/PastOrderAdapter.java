package com.semaai.agent.adapter.order;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.semaai.agent.R;
import com.semaai.agent.activity.order.PastOrderDetailsActivity;
import com.semaai.agent.databinding.ItemCustomerPastOrderDetailBinding;
import com.semaai.agent.model.order.OrderDetailsModel;
import com.semaai.agent.utils.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PastOrderAdapter extends RecyclerView.Adapter<PastOrderAdapter.ViewHolder> {
    PastOrderDetailsActivity customerPastOrderDetailsActivity;
    LayoutInflater inflater;
    ArrayList<OrderDetailsModel> orderDetailslistArray;
    ArrayList<OrderDetailsModel> in_orderDetailslistArray;


    public PastOrderAdapter(PastOrderDetailsActivity customerPastOrderDetailsActivity, ArrayList<OrderDetailsModel> orderDetailslistArray, ArrayList<OrderDetailsModel> in_orderDetailslistArray) {
        this.customerPastOrderDetailsActivity = customerPastOrderDetailsActivity;
        this.inflater = LayoutInflater.from(customerPastOrderDetailsActivity);
        this.orderDetailslistArray = orderDetailslistArray;
        this.in_orderDetailslistArray = in_orderDetailslistArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerPastOrderDetailBinding itemCustomerPastOrderDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_customer_past_order_detail, parent, false);
        return new ViewHolder(itemCustomerPastOrderDetailBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("TAG", "onBindViewHolder: >>>>>>" + orderDetailslistArray.get(position).getProductDetail());
        if (!orderDetailslistArray.get(position).getDate().equals("false")) {
            String deliveryDate = orderDetailslistArray.get(position).getDate();
            SimpleDateFormat dateFormatPrev = new SimpleDateFormat("dd.mm.yyyy");
            Date d = null;
            try {
                d = dateFormatPrev.parse(deliveryDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
            String changedDate = dateFormat.format(d);
            Log.e("TAG", "onBindViewHolder: check date " + changedDate);
            holder.itemCustomerPastOrderDetailBinding.tvDate.setText(" " + changedDate);
        }
        if (!orderDetailslistArray.get(position).getInvoiceId().equals("false")) {
            holder.itemCustomerPastOrderDetailBinding.tvInvoiceNo.setText(" " + orderDetailslistArray.get(position).getInvoiceId());
        }
        if (!orderDetailslistArray.get(position).getSalesperson().equals("false")) {
            holder.itemCustomerPastOrderDetailBinding.tvSalesPerson.setText(" " + orderDetailslistArray.get(position).getSalesperson());
        }
        if (!orderDetailslistArray.get(position).getPaymentStatus().equals("false")) {
            holder.itemCustomerPastOrderDetailBinding.tvSetPaymentStatus1.setText(orderDetailslistArray.get(position).getPaymentStatus());
        }
        if (!orderDetailslistArray.get(position).getPaymentMethod().equals("false")) {
            holder.itemCustomerPastOrderDetailBinding.tvSetPaymentMethod.setText(orderDetailslistArray.get(position).getPaymentMethod());
        }
        if (!orderDetailslistArray.get(position).getDeliveryStatus().equals("false")) {
            holder.itemCustomerPastOrderDetailBinding.tvSetDeliveryStatus.setText(orderDetailslistArray.get(position).getDeliveryStatus());
        }
        if (!orderDetailslistArray.get(position).getDeliveryDate().equals("false")) {
            String deliveryDate = orderDetailslistArray.get(position).getDeliveryDate();
            SimpleDateFormat dateFormatPrev = new SimpleDateFormat("dd.mm.yyyy");
            Date d = null;
            try {
                d = dateFormatPrev.parse(deliveryDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy");
            String changedDate = dateFormat.format(d);
            Log.e("TAG", "onBindViewHolder: check date " + changedDate);
            holder.itemCustomerPastOrderDetailBinding.tvSetDeliveryDate.setText(changedDate);
        }
        if (!orderDetailslistArray.get(position).getUntaxedAmount().equals("false")) {
            holder.itemCustomerPastOrderDetailBinding.tvSetUnAmount.setText("Rp " + Common.currencyFormatOnlyZero(orderDetailslistArray.get(position).getUntaxedAmount()));
        }
        if (!orderDetailslistArray.get(position).getLoyaltyTotal().equals("false")) {
            holder.itemCustomerPastOrderDetailBinding.tvSetSemaaiPoint.setText(Common.currencyFormatOnlyZero(orderDetailslistArray.get(position).getLoyaltyTotal()));

        }
        if (!orderDetailslistArray.get(position).getTax().equals("false")) {
            holder.itemCustomerPastOrderDetailBinding.tvSetTexes.setText("Rp " + Common.currencyFormatOnlyZero(orderDetailslistArray.get(position).getTax()));
        }
        if (!orderDetailslistArray.get(position).getTotalAmount().equals("false")) {
            holder.itemCustomerPastOrderDetailBinding.tvSetTotalAmount.setText("Rp " + Common.currencyFormatOnlyZero(orderDetailslistArray.get(position).getTotalAmount()));
        }

        //IN Item Recycleview
        PastOrderItemDetailsAdapter pastOrderItemDetailsAdapter = new PastOrderItemDetailsAdapter(customerPastOrderDetailsActivity, in_orderDetailslistArray);
        LinearLayoutManager manager = new LinearLayoutManager(customerPastOrderDetailsActivity, LinearLayoutManager.VERTICAL, false);
        holder.itemCustomerPastOrderDetailBinding.rvItemDataList.setAdapter(pastOrderItemDetailsAdapter);
        holder.itemCustomerPastOrderDetailBinding.rvItemDataList.setLayoutManager(manager);

        holder.itemCustomerPastOrderDetailBinding.clPastOrderDetailsItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderDetailslistArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCustomerPastOrderDetailBinding itemCustomerPastOrderDetailBinding;

        public ViewHolder(ItemCustomerPastOrderDetailBinding itemCustomerPastOrderDetailBinding) {
            super(itemCustomerPastOrderDetailBinding.getRoot());
            this.itemCustomerPastOrderDetailBinding = itemCustomerPastOrderDetailBinding;
        }
    }
}
