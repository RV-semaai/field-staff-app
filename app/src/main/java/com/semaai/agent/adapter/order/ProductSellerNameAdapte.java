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

import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.order.OrdersListActivity;
import com.semaai.agent.databinding.ActivityCustomerOrdersBinding;
import com.semaai.agent.databinding.ItemRvOrderFilterBinding;
import com.semaai.agent.model.order.OrderSellerNameModel;
import com.semaai.agent.utils.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ProductSellerNameAdapte extends RecyclerView.Adapter<ProductSellerNameAdapte.ViewHolder> {
    OrdersListActivity ordersListActivity;
    ArrayList<OrderSellerNameModel> productSellerArrayList;
    ArrayList<String> selectSellerName = new ArrayList<>();
    ArrayList<String> selectedFilterArray = new ArrayList<>();
    private int isSelected = -1;
    ActivityCustomerOrdersBinding binding;

    public ProductSellerNameAdapte(OrdersListActivity ordersListActivity, ArrayList<OrderSellerNameModel> productSellerArrayList, ActivityCustomerOrdersBinding binding) {
        this.ordersListActivity = ordersListActivity;
        this.productSellerArrayList = productSellerArrayList;
        this.binding = binding;
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
        ItemRvOrderFilterBinding itemRvOrderFilterBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_rv_order_filter, parent, false);
        return new ViewHolder(itemRvOrderFilterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.rvOrderFilterItemViewBinding.clSelectionItem.setVisibility(View.VISIBLE);
        holder.rvOrderFilterItemViewBinding.tvSetItemName.setText(productSellerArrayList.get(position).getSellerName());

        holder.rvOrderFilterItemViewBinding.clOrderfilteritem.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                isSelected = position;
                int color = holder.rvOrderFilterItemViewBinding.tvSetItemName.getCurrentTextColor();
                String textColor = String.format("#%06X", (0xFFFFFF & color));
                String selectColor = String.format("#%06X", (0xFFFFFF & Color.rgb(0, 0, 0)));
                if (textColor.equals(selectColor)) {
                    //remove
                    selectedFilterArray.remove(productSellerArrayList.get(position).getSellerId());
                    selectSellerName.remove(productSellerArrayList.get(position).getSellerName());
                    holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.rgb(243, 148, 4));
                    holder.rvOrderFilterItemViewBinding.clSelectionItem.setBackgroundColor(Color.rgb(229, 229, 229));
                } else {
                    //add
                    selectSellerName.add(productSellerArrayList.get(position).getSellerName());
                    selectedFilterArray.add(productSellerArrayList.get(position).getSellerId());
                    holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.rgb(0, 0, 0));
                    holder.rvOrderFilterItemViewBinding.clSelectionItem.setBackgroundColor(Color.rgb(243, 148, 4));
                }

                HashSet<String> filter = new HashSet(selectedFilterArray);
                HashSet<String> name = new HashSet(selectSellerName);
                selectedFilterArray = new ArrayList<String>(filter);
                selectSellerName = new ArrayList<String>(name);

                if (!selectSellerName.isEmpty()) {
                    binding.tvSellerNameProduct.setText(Arrays.toString(new ArrayList[]{selectSellerName}).replaceAll("\\[|\\]", ""));
                    binding.tvSellerNameProduct.setTextColor(Color.parseColor("#000000"));
                    Constant.productSellerName = "[\"seller\"," + "\"" + selectSellerName + "\"" + "]";
                } else {
                    binding.tvSellerNameProduct.setText(R.string.sellerName);
                    binding.tvSellerNameProduct.setTextColor(Color.parseColor("#F39404"));
                    Constant.productSellerName = "";
                }

                Log.e("TAG", "onClick:seller >>>>>>>>>>>>>>>" + Constant.productSellerName);
                Log.e("TAG", "onClick: >>>>>>>>" + new Gson().toJson(selectSellerName));

            }
        });
        Log.e("TAG", "onBindViewHolder: >>>>>> 1" + productSellerArrayList.size());
    }

    @Override
    public int getItemCount() {
        Log.e("TAG", "onBindViewHolder: >>>>>> 2");
        return productSellerArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemRvOrderFilterBinding rvOrderFilterItemViewBinding;

        public ViewHolder(ItemRvOrderFilterBinding rvOrderFilterItemViewBinding) {
            super(rvOrderFilterItemViewBinding.getRoot());
            this.rvOrderFilterItemViewBinding = rvOrderFilterItemViewBinding;
        }
    }
}
