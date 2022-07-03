package com.semaai.agent.adapter.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.order.OrdersListActivity;
import com.semaai.agent.activity.order.PastOrderDetailsActivity;
import com.semaai.agent.databinding.ItemCustomerOrderBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.order.OrderListModel;
import com.semaai.agent.utils.Common;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    OrdersListActivity ordersListActivity;
    LayoutInflater inflater;
    ArrayList<OrderListModel> orderListArray;
    OrderListModel orderListModel = new OrderListModel();
    CustomerModel customerModel;

    public OrderAdapter(OrdersListActivity ordersListActivity, ArrayList<OrderListModel> orderListArray, CustomerModel customerModel) {
        this.ordersListActivity = ordersListActivity;
        this.orderListArray = orderListArray;
        this.customerModel = customerModel;
        this.inflater = LayoutInflater.from(ordersListActivity);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCustomerOrderBinding itemCustomerOrderBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_customer_order, parent, false);
        return new ViewHolder(itemCustomerOrderBinding);
    }


    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Glide.with(ordersListActivity).load(R.drawable.imagview_bg).dontTransform().into(holder.itemCustomerOrderBinding.ivProduct);
        Log.e("TAG", "onClick: check img" + position + " " + orderListArray.get(position).getImage());
        if (!orderListArray.get(position).getSalesperson().equals("false")) {
            holder.itemCustomerOrderBinding.tvName.setText(orderListArray.get(position).getSalesperson());
        }
        if (!orderListArray.get(position).getDate().equals("false")) {
            holder.itemCustomerOrderBinding.tvSetDate.setText(ordersListActivity.getString(R.string.date) + " " + orderListArray.get(position).getDate());
        } else {
            holder.itemCustomerOrderBinding.tvSetDate.setText(ordersListActivity.getString(R.string.date) + " --");
        }
        if (!orderListArray.get(position).getInvoiceId().equals("false")) {
            holder.itemCustomerOrderBinding.tvSetInvoice.setText(ordersListActivity.getString(R.string.InvoiceNumber) + " " + orderListArray.get(position).getInvoiceId());
        } else {
            holder.itemCustomerOrderBinding.tvSetInvoice.setText(ordersListActivity.getString(R.string.InvoiceNumber) + " --");
        }
        if (!orderListArray.get(position).getOrderStatus().equals("false")) {
            holder.itemCustomerOrderBinding.tvSetOrderStatus.setText(ordersListActivity.getString(R.string.OrderStatus) + " " + orderListArray.get(position).getOrderStatus());
        } else {
            holder.itemCustomerOrderBinding.tvSetOrderStatus.setText(ordersListActivity.getString(R.string.OrderStatus) + " --");
        }
        if (!orderListArray.get(position).getAmount().equals("false")) {
            holder.itemCustomerOrderBinding.tvSetTotalAmount.setText(ordersListActivity.getString(R.string.TotalAmount) + " Rp " + Common.currencyFormatPoint(orderListArray.get(position).getAmount()));
        } else {
            holder.itemCustomerOrderBinding.tvSetTotalAmount.setText(ordersListActivity.getString(R.string.TotalAmount) + " Rp 0.00");
        }
        if (!orderListArray.get(position).getPaymentStatus().equals("false")) {
            holder.itemCustomerOrderBinding.tvSetPaymentStatus.setText(ordersListActivity.getString(R.string.PaymentMethod) + " " + orderListArray.get(position).getPaymentStatus());
        } else {
            holder.itemCustomerOrderBinding.tvSetPaymentStatus.setText(ordersListActivity.getString(R.string.PaymentStatus) + " --");
        }
        if (!orderListArray.get(position).getImage().equals("false")) {
            byte[] decodedString = Base64.decode(orderListArray.get(position).getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(ordersListActivity).load(decodedByte).dontTransform().into(holder.itemCustomerOrderBinding.ivProduct);
        }
        holder.itemCustomerOrderBinding.cvPastOrderItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                orderListModel.setId(orderListArray.get(position).getId());
                orderListModel.setSalesperson(holder.itemCustomerOrderBinding.tvName.getText().toString());
                orderListModel.setDate(holder.itemCustomerOrderBinding.tvSetDate.getText().toString());
                orderListModel.setInvoiceId(holder.itemCustomerOrderBinding.tvSetInvoice.getText().toString());
                orderListModel.setOrderStatus(holder.itemCustomerOrderBinding.tvSetOrderStatus.getText().toString());
                orderListModel.setAmount(holder.itemCustomerOrderBinding.tvSetTotalAmount.getText().toString());
                orderListModel.setPaymentStatus(holder.itemCustomerOrderBinding.tvSetPaymentStatus.getText().toString());
                Log.e("TAG", "onClick: check model " + new Gson().toJson(orderListModel));
                customerModel.setOrderListModel(orderListModel);
                Intent intent = new Intent(ordersListActivity, PastOrderDetailsActivity.class);
                intent.putExtra("sample", customerModel);
                ordersListActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderListArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCustomerOrderBinding itemCustomerOrderBinding;

        public ViewHolder(ItemCustomerOrderBinding itemCustomerOrderBinding) {
            super(itemCustomerOrderBinding.getRoot());
            this.itemCustomerOrderBinding = itemCustomerOrderBinding;
        }
    }
}
