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

public class OrderSellerNameAdapter extends RecyclerView.Adapter<OrderSellerNameAdapter.ViewHolder> {
    OrdersListActivity ordersListActivity;
    static ArrayList<OrderSellerNameModel> sellerNameArrayList;
    ArrayList<String> selectSellerName = new ArrayList<>();
    ArrayList<String> selectedFilterArray = new ArrayList<>();
    private int isSelected = -1;
    ActivityCustomerOrdersBinding binding;

    public OrderSellerNameAdapter(OrdersListActivity ordersListActivity, ArrayList<OrderSellerNameModel> sellerNameArrayList, ActivityCustomerOrdersBinding binding) {
        this.ordersListActivity = ordersListActivity;
        this.sellerNameArrayList = sellerNameArrayList;
        this.binding = binding;
        Log.e("TAG", "onBindViewHolder: >>>>>> 0" + sellerNameArrayList.size());
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

        holder.itemRvOrderFilterBinding.clSelectionItem.setVisibility(View.VISIBLE);
        holder.itemRvOrderFilterBinding.tvSetItemName.setText(sellerNameArrayList.get(position).getSellerName());

        holder.itemRvOrderFilterBinding.clOrderfilteritem.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                isSelected = position;
                int color = holder.itemRvOrderFilterBinding.tvSetItemName.getCurrentTextColor();
                String textColor = String.format("#%06X", (0xFFFFFF & color));
                String selectColor = String.format("#%06X", (0xFFFFFF & Color.rgb(0, 0, 0)));
                if (textColor.equals(selectColor)) {
                    //remove
                    selectedFilterArray.remove(sellerNameArrayList.get(position).getSellerId());
                    selectSellerName.remove(sellerNameArrayList.get(position).getSellerName());
                    holder.itemRvOrderFilterBinding.tvSetItemName.setTextColor(Color.rgb(243, 148, 4));
                    holder.itemRvOrderFilterBinding.clSelectionItem.setBackgroundColor(Color.rgb(229, 229, 229));
                } else {
                    //add
                    selectSellerName.add(sellerNameArrayList.get(position).getSellerName());
                    selectedFilterArray.add(sellerNameArrayList.get(position).getSellerId());
                    holder.itemRvOrderFilterBinding.tvSetItemName.setTextColor(Color.rgb(0, 0, 0));
                    holder.itemRvOrderFilterBinding.clSelectionItem.setBackgroundColor(Color.rgb(243, 148, 4));
                }

                HashSet<String> filter = new HashSet(selectedFilterArray);
                HashSet<String> name = new HashSet(selectSellerName);
                selectedFilterArray = new ArrayList<String>(filter);
                selectSellerName = new ArrayList<String>(name);

                if (!selectSellerName.isEmpty()) {
                    binding.tvSellerName.setText(Arrays.toString(new ArrayList[]{selectSellerName}).replaceAll("\\[|\\]", ""));
                    binding.tvSellerName.setTextColor(Color.parseColor("#000000"));
                    Constant.sellerName = "[\"seller\"," + "\"" + selectSellerName + "\"" + "]";
                } else {
                    binding.tvSellerName.setText(R.string.sellerName);
                    binding.tvSellerName.setTextColor(Color.parseColor("#F39404"));
                    Constant.sellerName = "";
                }

                Log.e("TAG", "onClick:seller >>>>>>>>>>>>>>>" + Constant.sellerName);
                Log.e("TAG", "onClick: >>>>>>>>" + new Gson().toJson(selectSellerName));

            }
        });
        Log.e("TAG", "onBindViewHolder: >>>>>> 1" + sellerNameArrayList.size());
    }

    @Override
    public int getItemCount() {
        Log.e("TAG", "onBindViewHolder: >>>>>> 2");
        return sellerNameArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemRvOrderFilterBinding itemRvOrderFilterBinding;

        public ViewHolder(ItemRvOrderFilterBinding itemRvOrderFilterBinding) {
            super(itemRvOrderFilterBinding.getRoot());
            this.itemRvOrderFilterBinding = itemRvOrderFilterBinding;
        }
    }
}
