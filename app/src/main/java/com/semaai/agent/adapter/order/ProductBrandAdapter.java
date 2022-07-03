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

public class ProductBrandAdapter extends RecyclerView.Adapter<ProductBrandAdapter.ViewHolder> {
    OrdersListActivity ordersListActivity;
    ArrayList<ProductBranModel> productBrandArrayList;
    LayoutInflater inflater;
    ActivityCustomerOrdersBinding binding;
    ArrayList<String> selectBrand = new ArrayList<>();
    ArrayList<String> selectedFilterArray = new ArrayList<>();

    public ProductBrandAdapter(OrdersListActivity ordersListActivity, ArrayList<ProductBranModel> productBrandArrayList, ActivityCustomerOrdersBinding binding) {
        this.ordersListActivity = ordersListActivity;
        this.productBrandArrayList = productBrandArrayList;
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
    public ProductBrandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRvOrderFilterBinding rvOrderFilterItemViewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_rv_order_filter, parent, false);
        return new ViewHolder(rvOrderFilterItemViewBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductBrandAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.rvOrderFilterItemViewBinding.clSelectionItem.setVisibility(View.VISIBLE);
        holder.rvOrderFilterItemViewBinding.tvSetItemName.setText(productBrandArrayList.get(position).getBrandName());
        holder.rvOrderFilterItemViewBinding.clOrderfilteritem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = holder.rvOrderFilterItemViewBinding.tvSetItemName.getCurrentTextColor();
                String textColor = String.format("#%06X", (0xFFFFFF & color));
                String selectColor = String.format("#%06X", (0xFFFFFF & Color.rgb(0, 0, 0)));
                if (textColor.equals(selectColor)) {
                    //remove
                    selectedFilterArray.remove(productBrandArrayList.get(position).getId());
                    selectBrand.remove(productBrandArrayList.get(position).getBrandName());
                    holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.rgb(243, 148, 4));
                    holder.rvOrderFilterItemViewBinding.clSelectionItem.setBackgroundColor(Color.rgb(229, 229, 229));
                } else {
                    //add
                    selectBrand.add(productBrandArrayList.get(position).getBrandName());
                    selectedFilterArray.add(productBrandArrayList.get(position).getId());
                    holder.rvOrderFilterItemViewBinding.tvSetItemName.setTextColor(Color.rgb(0, 0, 0));
                    holder.rvOrderFilterItemViewBinding.clSelectionItem.setBackgroundColor(Color.rgb(243, 148, 4));
                }

                HashSet<String> filter = new HashSet(selectedFilterArray);
                HashSet<String> name = new HashSet(selectBrand);
                selectedFilterArray = new ArrayList<String>(filter);
                selectBrand = new ArrayList<String>(name);

                if (!selectBrand.isEmpty()) {
                    binding.tvBrand.setText(Arrays.toString(new ArrayList[]{selectBrand}).replaceAll("\\[|\\]", ""));
                    binding.tvBrand.setTextColor(Color.parseColor("#000000"));
                    Constant.productBrand = "[\"product-brand\"," + selectedFilterArray + "]";
                } else {
                    binding.tvBrand.setText(R.string.brand);
                    binding.tvBrand.setTextColor(Color.parseColor("#F39404"));
                    Constant.productBrand = "";
                }

                Log.e("TAG", "onClick:brand >>>>>>>>>>>>>>>" + Constant.productBrand);
                Log.e("TAG", "onClick: >>>>>>>>" + new Gson().toJson(selectBrand));

            }
        });

    }

    @Override
    public int getItemCount() {
        return productBrandArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemRvOrderFilterBinding rvOrderFilterItemViewBinding;

        public ViewHolder(ItemRvOrderFilterBinding rvOrderFilterItemViewBinding) {
            super(rvOrderFilterItemViewBinding.getRoot());
            this.rvOrderFilterItemViewBinding = rvOrderFilterItemViewBinding;
        }
    }
}
