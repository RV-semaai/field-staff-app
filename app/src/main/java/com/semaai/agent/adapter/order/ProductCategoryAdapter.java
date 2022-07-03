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
import com.semaai.agent.model.order.ProductBranModel;
import com.semaai.agent.utils.Constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class ProductCategoryAdapter extends RecyclerView.Adapter<ProductCategoryAdapter.ViewHolder> {
    OrdersListActivity ordersListActivity;
    ArrayList<ProductBranModel> productCategoryArrayList;
    LayoutInflater inflater;
    ActivityCustomerOrdersBinding binding;
    ArrayList<String> selectCategory = new ArrayList<>();
    ArrayList<String> selectedFilterArray = new ArrayList<>();

    public ProductCategoryAdapter(OrdersListActivity ordersListActivity, ArrayList<ProductBranModel> productCategoryArrayList, ActivityCustomerOrdersBinding binding) {
        this.ordersListActivity = ordersListActivity;
        this.productCategoryArrayList = productCategoryArrayList;
        this.binding = binding;
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
    public ProductCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvOrderFilterBinding itemRvOrderFilterBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_rv_order_filter, parent, false);
        return new ViewHolder(itemRvOrderFilterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.rvOrderFilterItemViewBinding.clSelectionItem.setVisibility(View.VISIBLE);
        holder.rvOrderFilterItemViewBinding.tvSetItemName.setText(productCategoryArrayList.get(position).getBrandName());
        holder.rvOrderFilterItemViewBinding.clOrderfilteritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = holder.rvOrderFilterItemViewBinding.tvSetItemName.getCurrentTextColor();
                String textColor = String.format("#%06X", (0xFFFFFF & color));
                String selectColor = String.format("#%06X", (0xFFFFFF & Color.rgb(0, 0, 0)));
                if (textColor.equals(selectColor)) {
                    //remove
                    selectedFilterArray.remove(productCategoryArrayList.get(position).getId());
                    selectCategory.remove(productCategoryArrayList.get(position).getBrandName());
                    holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.rgb(243, 148, 4));
                    holder.rvOrderFilterItemViewBinding.clSelectionItem.setBackgroundColor(Color.rgb(229, 229, 229));
                } else {
                    //add
                    selectCategory.add(productCategoryArrayList.get(position).getBrandName());
                    selectedFilterArray.add(productCategoryArrayList.get(position).getId());
                    holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.rgb(0, 0, 0));
                    holder.rvOrderFilterItemViewBinding.clSelectionItem.setBackgroundColor(Color.rgb(243, 148, 4));
                }

                HashSet<String> filter = new HashSet(selectedFilterArray);
                HashSet<String> name = new HashSet(selectCategory);
                selectedFilterArray = new ArrayList<String>(filter);
                selectCategory = new ArrayList<String>(name);

                if (!selectCategory.isEmpty()) {
                    binding.tvCategory.setText(Arrays.toString(new ArrayList[]{selectCategory}).replaceAll("\\[|\\]", ""));
                    binding.tvCategory.setTextColor(Color.parseColor("#000000"));
                    Constant.productCategory = "[\"product-category\"," + selectedFilterArray + "]";
                } else {
                    binding.tvCategory.setText(R.string.category);
                    binding.tvCategory.setTextColor(Color.parseColor("#F39404"));
                    Constant.productCategory = "";
                }

                Log.e("TAG", "onClick:category >>>>>>>>>>>>>>>" + Constant.productCategory);
                Log.e("TAG", "onClick: >>>>>>>>" + new Gson().toJson(selectCategory));
            }
        });

    }

    @Override
    public int getItemCount() {
        return productCategoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemRvOrderFilterBinding rvOrderFilterItemViewBinding;

        public ViewHolder(ItemRvOrderFilterBinding rvOrderFilterItemViewBinding) {
            super(rvOrderFilterItemViewBinding.getRoot());
            this.rvOrderFilterItemViewBinding = rvOrderFilterItemViewBinding;
        }
    }
}
