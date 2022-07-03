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

public class ProductSubCategoryAdapter extends RecyclerView.Adapter<ProductSubCategoryAdapter.ViewHolder> {
    OrdersListActivity ordersListActivity;
    ArrayList<ProductBranModel> productSubCategoryArrayList;
    LayoutInflater inflater;
    ActivityCustomerOrdersBinding binding;
    ArrayList<String> selectSubCategory = new ArrayList<>();
    ArrayList<String> selectedFilterArray = new ArrayList<>();


    public ProductSubCategoryAdapter(OrdersListActivity ordersListActivity, ArrayList<ProductBranModel> productSubCategoryArrayList, ActivityCustomerOrdersBinding binding) {
        this.ordersListActivity = ordersListActivity;
        this.productSubCategoryArrayList = productSubCategoryArrayList;
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
    public ProductSubCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvOrderFilterBinding rvOrderFilterBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_rv_order_filter, parent, false);
        return new ViewHolder(rvOrderFilterBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSubCategoryAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.rvOrderFilterBinding.clSelectionItem.setVisibility(View.VISIBLE);
        holder.rvOrderFilterBinding.tvSetItemName.setText(productSubCategoryArrayList.get(position).getBrandName());
        holder.rvOrderFilterBinding.clOrderfilteritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = holder.rvOrderFilterBinding.tvSetItemName.getCurrentTextColor();
                String textColor = String.format("#%06X", (0xFFFFFF & color));
                String selectColor = String.format("#%06X", (0xFFFFFF & Color.rgb(0, 0, 0)));
                if (textColor.equals(selectColor)) {
                    //remove
                    selectedFilterArray.remove(productSubCategoryArrayList.get(position).getId());
                    selectSubCategory.remove(productSubCategoryArrayList.get(position).getBrandName());
                    holder.rvOrderFilterBinding.tvSetItemName.setTextColor(Color.rgb(243, 148, 4));
                    holder.rvOrderFilterBinding.clSelectionItem.setBackgroundColor(Color.rgb(229, 229, 229));
                } else {
                    //add
                    selectSubCategory.add(productSubCategoryArrayList.get(position).getBrandName());
                    selectedFilterArray.add(productSubCategoryArrayList.get(position).getId());
                    holder.rvOrderFilterBinding.tvSetItemName.setTextColor(Color.rgb(0, 0, 0));
                    holder.rvOrderFilterBinding.clSelectionItem.setBackgroundColor(Color.rgb(243, 148, 4));
                }

                HashSet<String> filter = new HashSet(selectedFilterArray);
                HashSet<String> name = new HashSet(selectSubCategory);
                selectedFilterArray = new ArrayList<String>(filter);
                selectSubCategory = new ArrayList<String>(name);

                if (!selectSubCategory.isEmpty()) {
                    binding.tvSubcategory.setText(Arrays.toString(new ArrayList[]{selectSubCategory}).replaceAll("\\[|\\]", ""));
                    binding.tvSubcategory.setTextColor(Color.parseColor("#000000"));
                    Constant.productSubCategory = "[\"product-category\"," + selectedFilterArray + "]";
                } else {
                    binding.tvSubcategory.setText(R.string.subcategory);
                    binding.tvSubcategory.setTextColor(Color.parseColor("#F39404"));
                    Constant.productSubCategory = "";
                }

                Log.e("TAG", "onClick:brand >>>>>>>>>>>>>>>" + Constant.productSubCategory);
                Log.e("TAG", "onClick: >>>>>>>>" + new Gson().toJson(selectSubCategory));

            }
        });

    }

    @Override
    public int getItemCount() {
        return productSubCategoryArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemRvOrderFilterBinding rvOrderFilterBinding;

        public ViewHolder(ItemRvOrderFilterBinding rvOrderFilterItemViewBinding) {
            super(rvOrderFilterItemViewBinding.getRoot());
            this.rvOrderFilterBinding = rvOrderFilterItemViewBinding;
        }
    }
}
