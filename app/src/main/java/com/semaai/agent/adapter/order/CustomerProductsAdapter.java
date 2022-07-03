package com.semaai.agent.adapter.order;

import static com.semaai.agent.utils.Common.currencyFormatOnlyZero;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.semaai.agent.R;
import com.semaai.agent.activity.order.OrdersListActivity;
import com.semaai.agent.databinding.ItemCustomerOrderProductBinding;
import com.semaai.agent.model.order.ProductsListModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomerProductsAdapter extends RecyclerView.Adapter<CustomerProductsAdapter.ViewHolder> {
    OrdersListActivity ordersListActivity;
    LayoutInflater inflater;
    ArrayList<ProductsListModel> pastProductsArrayList;

    public CustomerProductsAdapter(OrdersListActivity ordersListActivity, ArrayList<ProductsListModel> pastProductsArrayList) {
        this.ordersListActivity = ordersListActivity;
        this.inflater = LayoutInflater.from(ordersListActivity);
        this.pastProductsArrayList = pastProductsArrayList;
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
        ItemCustomerOrderProductBinding itemCustomerOrderProductBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.item_customer_order_product, parent, false);
        return new ViewHolder(itemCustomerOrderProductBinding);
    }

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!pastProductsArrayList.get(position).getDefaultCode().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductCode.setText(pastProductsArrayList.get(position).getDefaultCode());
        } else {
            holder.itemCustomerOrderProductBinding.tvProductCode.setText("--");
        }
        if (currencyFormatOnlyZero(pastProductsArrayList.get(position).getStock()).equals("0")) {
            holder.itemCustomerOrderProductBinding.tvProductStock.setTextColor(Color.parseColor("#E13600"));
            holder.itemCustomerOrderProductBinding.tvProductRp.setTextColor(Color.parseColor("#E13600"));
        } else {
            holder.itemCustomerOrderProductBinding.tvProductStock.setTextColor(Color.parseColor("#298A3E"));
            holder.itemCustomerOrderProductBinding.tvProductRp.setTextColor(Color.parseColor("#298A3E"));
        }
        Glide.with(ordersListActivity).load(R.drawable.imagview_bg).dontTransform().into(holder.itemCustomerOrderProductBinding.ivProductImg);
        if (!pastProductsArrayList.get(position).getProductName().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductName.setText(pastProductsArrayList.get(position).getProductName());
        } else {
            holder.itemCustomerOrderProductBinding.tvProductName.setText("--");
        }
        if (!pastProductsArrayList.get(position).getProductBrand().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductBrand.setText(pastProductsArrayList.get(position).getProductBrand());
        } else {
            holder.itemCustomerOrderProductBinding.tvProductBrand.setText("--");
        }
        if (!pastProductsArrayList.get(position).getProductCategory().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductCategory.setText(pastProductsArrayList.get(position).getProductCategory());
        } else {
            holder.itemCustomerOrderProductBinding.tvProductCategory.setText("--");
        }
        if (!pastProductsArrayList.get(position).getProductCustomerPrice().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductRp.setText("Rp " + currencyFormatOnlyZero(pastProductsArrayList.get(position).getProductCustomerPrice()));
        } else {
            holder.itemCustomerOrderProductBinding.tvProductRp.setText("Rp --");
        }
        if (!pastProductsArrayList.get(position).getStock().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductStock.setText(ordersListActivity.getString(R.string.stock) + " " + currencyFormatOnlyZero(pastProductsArrayList.get(position).getStock()));
        } else {
            holder.itemCustomerOrderProductBinding.tvProductStock.setText(ordersListActivity.getString(R.string.stock) + " " + "--");
        }
        if (!pastProductsArrayList.get(position).getProductImage().equals("false")) {
            byte[] decodedString = Base64.decode(pastProductsArrayList.get(position).getProductImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            Glide.with(ordersListActivity).load(decodedByte).dontTransform().into(holder.itemCustomerOrderProductBinding.ivProductImg);
        }
        if (!pastProductsArrayList.get(position).getLastPurchasedQty().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductLastQty.setText(ordersListActivity.getString(R.string.qty) + " " + currencyFormatOnlyZero(pastProductsArrayList.get(position).getLastPurchasedQty()));
        } else {
            holder.itemCustomerOrderProductBinding.tvProductLastQty.setText(ordersListActivity.getString(R.string.qty) + " " + "--");
        }
        if (!pastProductsArrayList.get(position).getTotalPurchasedProductQty().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductQtyNo.setText(currencyFormatOnlyZero(pastProductsArrayList.get(position).getTotalPurchasedProductQty()));
        } else {
            holder.itemCustomerOrderProductBinding.tvProductQtyNo.setText("--");
        }
        if (!pastProductsArrayList.get(position).getSalesperson().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductSoldByName.setText(pastProductsArrayList.get(position).getSalesperson());
        } else {
            holder.itemCustomerOrderProductBinding.tvProductSoldByName.setText("--");
        }
        if (!pastProductsArrayList.get(position).getLastPurchasedDate().equals("false")) {
            String deliveryDate = pastProductsArrayList.get(position).getLastPurchasedDate();
            SimpleDateFormat dateFormatPrev = new SimpleDateFormat("dd.mm.yyyy");
            Date d = null;
            try {
                d = dateFormatPrev.parse(deliveryDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");
            String changedDate = dateFormat.format(d);
            Log.i("TAG", "onBindViewHolder: check date " + changedDate);
            holder.itemCustomerOrderProductBinding.tvProductLpDate.setText(changedDate);
        } else {
            holder.itemCustomerOrderProductBinding.tvProductLpDate.setText("--");
        }
        if (!pastProductsArrayList.get(position).getLastPurchasedPrice().equals("false")) {
            holder.itemCustomerOrderProductBinding.tvProductLastPrice.setText("Rp " + currencyFormatOnlyZero(pastProductsArrayList.get(position).getLastPurchasedPrice()));
        } else {
            holder.itemCustomerOrderProductBinding.tvProductLastPrice.setText("Rp " + "--");
        }
        holder.itemCustomerOrderProductBinding.cvPastProductItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return pastProductsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCustomerOrderProductBinding itemCustomerOrderProductBinding;

        public ViewHolder(ItemCustomerOrderProductBinding itemCustomerOrderProductBinding) {
            super(itemCustomerOrderProductBinding.getRoot());
            this.itemCustomerOrderProductBinding = itemCustomerOrderProductBinding;
        }
    }
}
