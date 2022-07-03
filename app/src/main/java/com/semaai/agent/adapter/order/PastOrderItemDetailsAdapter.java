package com.semaai.agent.adapter.order;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
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
import com.semaai.agent.activity.order.PastOrderDetailsActivity;
import com.semaai.agent.databinding.ItemPastOrederItemDetailBinding;
import com.semaai.agent.model.order.OrderDetailsModel;
import com.semaai.agent.utils.Common;

import java.util.ArrayList;

public class PastOrderItemDetailsAdapter extends RecyclerView.Adapter<PastOrderItemDetailsAdapter.ViewHolder> {
    PastOrderDetailsActivity pastOrderDetailsActivity;
    LayoutInflater inflater;
    ArrayList<OrderDetailsModel> orderDetailListArray;

    public PastOrderItemDetailsAdapter(PastOrderDetailsActivity customerPastOrderDetailsActivity, ArrayList<OrderDetailsModel> orderDetailListArray) {
        this.pastOrderDetailsActivity = customerPastOrderDetailsActivity;
        this.inflater = LayoutInflater.from(customerPastOrderDetailsActivity);
        this.orderDetailListArray = orderDetailListArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPastOrederItemDetailBinding itemPastOrederItemDetailBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_past_oreder_item_detail, parent, false);
        return new ViewHolder(itemPastOrederItemDetailBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.e("TAG", "onBindViewHolder: " + new Gson().toJson(orderDetailListArray));
        if (position == 0) {
            holder.itemPastOrederItemDetailBinding.tvName.setVisibility(View.VISIBLE);
            if (!orderDetailListArray.get(position).getMarketplaceSeller().equals("false")) {
                holder.itemPastOrederItemDetailBinding.tvName.setText(orderDetailListArray.get(position).getMarketplaceSeller());
            }
        } else {
            holder.itemPastOrederItemDetailBinding.tvName.setVisibility(View.GONE);
        }
        String amountLine = "<strike><font>" + "Rp " + Common.currencyFormatOnlyZero(orderDetailListArray.get(position).getOriginalPrice()) + "</font></strike>";
        holder.itemPastOrederItemDetailBinding.tvItemAmountLine.setText(Html.fromHtml(amountLine));
        if (!orderDetailListArray.get(position).getPricePaid().equals("false")) {
            holder.itemPastOrederItemDetailBinding.tvItemAmountLine.setText(" Rp " + Common.currencyFormatOnlyZero(orderDetailListArray.get(position).getPricePaid()));
        }
        if (!orderDetailListArray.get(position).getProductQty().equals("false")) {
            holder.itemPastOrederItemDetailBinding.tvNumOfItem.setText("x" + Common.currencyFormatOnlyZero(orderDetailListArray.get(position).getProductQty()));
        }
        if (!orderDetailListArray.get(position).getReferenceCode().equals("false")) {
            holder.itemPastOrederItemDetailBinding.tvItemDetailsCode.setText(orderDetailListArray.get(position).getReferenceCode() + ", ");
        }
        if (!orderDetailListArray.get(position).getProductCatName().equals("false")) {
            holder.itemPastOrederItemDetailBinding.tvItemDetails.setText(orderDetailListArray.get(position).getProductCatName());
        }
        if (!orderDetailListArray.get(position).getProductName().equals("false")) {
            holder.itemPastOrederItemDetailBinding.tvItemName.setText(orderDetailListArray.get(position).getProductName());
        }
        if (!orderDetailListArray.get(position).getImage().equals("false")) {
            byte[] decodedString = Base64.decode(orderDetailListArray.get(position).getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(pastOrderDetailsActivity).load(decodedByte).dontTransform().into(holder.itemPastOrederItemDetailBinding.ivProduct);
        }

        if ((orderDetailListArray.size() - 1) == position) {
            holder.itemPastOrederItemDetailBinding.view2.setVisibility(View.VISIBLE);
        } else {
            holder.itemPastOrederItemDetailBinding.view2.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return orderDetailListArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPastOrederItemDetailBinding itemPastOrederItemDetailBinding;

        public ViewHolder(ItemPastOrederItemDetailBinding itemPastOrederItemDetailBinding) {
            super(itemPastOrederItemDetailBinding.getRoot());
            this.itemPastOrederItemDetailBinding = itemPastOrederItemDetailBinding;
        }
    }
}
