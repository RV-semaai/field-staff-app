package com.semaai.agent.activity.order;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.semaai.agent.R;
import com.semaai.agent.activity.DashboardActivity;
import com.semaai.agent.activity.existingcustomers.CustomersListActivity;
import com.semaai.agent.activity.login.AccountDashboardActivity;
import com.semaai.agent.adapter.order.CustomerProductsAdapter;
import com.semaai.agent.adapter.order.OrderAdapter;
import com.semaai.agent.adapter.order.OrderDeliveryStatusAdapter;
import com.semaai.agent.adapter.order.OrderPaymentStatusAdapter;
import com.semaai.agent.adapter.order.OrderSellerNameAdapter;
import com.semaai.agent.adapter.order.ProductBrandAdapter;
import com.semaai.agent.adapter.order.ProductCategoryAdapter;
import com.semaai.agent.adapter.order.ProductSellerNameAdapte;
import com.semaai.agent.adapter.order.ProductSubCategoryAdapter;
import com.semaai.agent.databinding.ActivityCustomerOrdersBinding;
import com.semaai.agent.model.CustomerModel;
import com.semaai.agent.model.order.OrderDeliveryStatusModel;
import com.semaai.agent.model.order.OrderListModel;
import com.semaai.agent.model.order.OrderPaymentStatusModel;
import com.semaai.agent.model.order.OrderSellerNameModel;
import com.semaai.agent.model.order.ProductBranModel;
import com.semaai.agent.model.order.ProductsListModel;
import com.semaai.agent.network.ApiEndPoints;
import com.semaai.agent.network.ApiStringModel;
import com.semaai.agent.utils.Common;
import com.semaai.agent.utils.Constant;
import com.semaai.agent.viewmodel.order.OrdersViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrdersListActivity extends AppCompatActivity {

    OrdersViewModel ordersViewModel;
    private ActivityCustomerOrdersBinding binding;
    private String orderTitle = "order";
    CustomerProductsAdapter customerProductsAdapter;
    OrderAdapter customerOrderAdapter;
    private CustomerModel customerModel;
    private Dialog progressDialog;
    ArrayList<OrderListModel> orderListArray = new ArrayList<>();
    private String item_sort = "asc";
    private String sortName = "";
    private String sortDate = "desc";
    private String sortQuantity = "";
    int page = 1;
    int itemLimit = 10;
    ArrayList<OrderDeliveryStatusModel> deliveryStatusArrayList = new ArrayList<>();
    ArrayList<OrderSellerNameModel> sellerNameArrayList = new ArrayList<>();
    ArrayList<OrderSellerNameModel> productSellerArrayList = new ArrayList<>();
    ArrayList<OrderPaymentStatusModel> paymentStatusArrayList = new ArrayList<>();
    ArrayList<ProductBranModel> productBrandArrayList = new ArrayList<>();
    ArrayList<ProductBranModel> productCategoryArrayList = new ArrayList<>();
    ArrayList<ProductBranModel> productSubCategoryArrayList = new ArrayList<>();
    private LinearLayoutManager manager;
    int filters = 0;
    String orderFilter;
    int currentItems, totalItems, scrollOutItem, previousTotal;
    private Boolean isScrolling = true, call = true, callProduct = true;
    ArrayList<ProductsListModel> pastProductsArrayList = new ArrayList<>();
    String productSortCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ordersViewModel = ViewModelProviders.of(OrdersListActivity.this).get(OrdersViewModel.class);
        binding = DataBindingUtil.setContentView(OrdersListActivity.this, R.layout.activity_customer_orders);
        binding.setLifecycleOwner(this);
        binding.setOrdersViewModel(ordersViewModel);

        intiView();
        getData();
        onClick();
    }

    private void getData() {

        manager = new LinearLayoutManager(OrdersListActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rvOrderListData.setLayoutManager(manager);
        apiCall();
        filterApiCallSellerName();
        filterApiCallDeliveryStatus();
        filterApiCallPaymentStatus();
        productBrandApiCall();
        productSellerNameApiCall();
        productCategoryApiCall();
        setPagination();
    }

    private void intiView() {
        //getData
        Intent i = getIntent();
        customerModel = (CustomerModel) i.getSerializableExtra("sample");
        binding.clTitle.tvHeader.setText(getString(R.string.pastOrders));
        binding.clTitle.tvTopBar.setText(customerModel.getCustomerListModel().getName());
        Log.e("TAG", "intView: model" + new Gson().toJson(customerModel));

        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.layout_progressbar);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

    }

    private void apiCall() {
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        ANRequest.GetRequestBuilder androidNetwork = AndroidNetworking.get(ApiEndPoints.orders);
        androidNetwork.addQueryParameter(ApiStringModel.customerId, customerModel.getCustomerListModel().getId());
        androidNetwork.addQueryParameter(ApiStringModel.page, String.valueOf(page));
        androidNetwork.addQueryParameter(ApiStringModel.limit, String.valueOf(itemLimit));
        androidNetwork.addQueryParameter(ApiStringModel.sort, item_sort);
        if (filters == 1) {
            androidNetwork.addQueryParameter(ApiStringModel.filter, orderFilter);
        }
        androidNetwork.setTag("test");
        androidNetwork.addHeaders("X-Openerp-Session-Id", separated[1]);
        androidNetwork.setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: >>> " + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject dataObject = response.getJSONObject(ApiStringModel.data);
                                String count = dataObject.getString(ApiStringModel.count);
                                String prev = dataObject.getString(ApiStringModel.prev);
                                String current = dataObject.getString(ApiStringModel.current);
                                String next = dataObject.getString(ApiStringModel.next);
                                String total_pages = dataObject.getString(ApiStringModel.totalPages);
                                JSONArray resultJsonArray = dataObject.getJSONArray(ApiStringModel.result);

                                binding.rvOrderListData.setVisibility(View.VISIBLE);
                                binding.tvNoRecord.setVisibility(View.GONE);
                                for (int i = 0; i < resultJsonArray.length(); i++) {
                                    JSONObject resultObject = resultJsonArray.getJSONObject(i);
                                    Log.e("TAG", "onResponse: ID >>>>>>" + resultObject.getString(ApiStringModel.id));
                                    String orderStatus = "";
                                    if (resultObject.has(ApiStringModel.orderStatus)) {
                                        orderStatus = resultObject.getString(ApiStringModel.orderStatus);
                                    }
                                    orderListArray.add(new OrderListModel(resultObject.getString(ApiStringModel.id),
                                            resultObject.getString(ApiStringModel.salesperson),
                                            resultObject.getString(ApiStringModel.date),
                                            resultObject.getString(ApiStringModel.invoiceId),
                                            resultObject.getString(ApiStringModel.paymentStatus),
                                            orderStatus,
                                            resultObject.getString(ApiStringModel.amount),
                                            resultObject.getString(ApiStringModel.image)));
                                }
                                Log.e("TAG", "onResponse: array >>" + new Gson().toJson(orderListArray));

                                if (orderTitle.equals("order")) {
                                    if (call) {
                                        if (orderListArray.size() != 0) {
                                            call = false;
                                            customerOrderAdapter = new OrderAdapter(OrdersListActivity.this, orderListArray, customerModel);
                                            binding.rvOrderListData.setAdapter(customerOrderAdapter);
                                        } else {
                                            binding.rvOrderListData.setVisibility(View.GONE);
                                            binding.tvNoRecord.setVisibility(View.VISIBLE);
                                        }
                                    } else {
                                        customerOrderAdapter.notifyDataSetChanged();
                                    }
                                }

                                progressDialog.dismiss();
                                Log.e("TAG", "onResponse: >>>>" + new Gson().toJson(orderListArray));
                            }

                            if (orderListArray.size() != 0) {
                                binding.rvOrderListData.setVisibility(View.VISIBLE);
                                binding.tvNoRecord.setVisibility(View.GONE);
                            } else {
                                binding.rvOrderListData.setVisibility(View.GONE);
                                binding.tvNoRecord.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            Log.e("TAG", "onResponse: catch >>>" + e.getMessage());
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("TAG", "onError: >>> " + error.getMessage());
                    }
                });
    }

    private void setPagination() {
        binding.rvOrderListData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItem = manager.findFirstVisibleItemPosition();

                Log.i("Api -->", "currentItems : " + currentItems + " , scrollOutItem : " + scrollOutItem + " , totalItems : " + totalItems);

                if (isScrolling) {
                    if (totalItems > previousTotal) {
                        previousTotal = totalItems;
                        page++;
                        isScrolling = false;
                    }
                }
                if (!isScrolling && (scrollOutItem + currentItems) >= totalItems) {
                    isScrolling = true;
                    apiCall();
                }
            }
        });
    }

    private void onClick() {
        binding.cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutViewGone();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
            }
        });

        binding.clTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutViewGone();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
            }
        });
        binding.clList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutViewGone();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));

            }
        });
        binding.clListingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutViewGone();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));

            }
        });
        binding.rvOrderListData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                layoutViewGone();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                return false;
            }
        });

        binding.cvPostOrders.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                callProduct = true;
                orderListArray.clear();
                orderTitle = "order";
                layoutViewGone();
                sortByFun();
                page = 1;
                filters = 0;
                orderFilter = "";
                filterLayoutGone();
                binding.clFilterList.setVisibility(View.GONE);
                Constant.isSelectDeliveryStatus = -1;
                Constant.isSelectPaymentStatus = -1;
                Constant.isSelectBrand = -1;
                Constant.isSelectCategory = -1;
                binding.tvDeliveryStatus.setText(R.string.deliveryStatus);
                binding.tvSellerName.setText(R.string.sellerName);
                binding.tvPaymentStatus.setText(R.string.paymentStatus);
                binding.tvDeliveryStatus.setTextColor(Color.parseColor("#F39404"));
                binding.tvSellerName.setTextColor(Color.parseColor("#F39404"));
                binding.tvPaymentStatus.setTextColor(Color.parseColor("#F39404"));
                binding.tvNewOld.setTextColor(Color.parseColor("#F39404"));
                binding.tvOldNew.setTextColor(Color.parseColor("#F39404"));
                filterApiCallSellerName();
                filterApiCallDeliveryStatus();
                filterApiCallPaymentStatus();
                productBrandApiCall();
                productSellerNameApiCall();
                productCategoryApiCall();
                productSubCategoryApiCall();
                binding.clTitle.tvHeader.setText(getString(R.string.pastOrders));
                binding.rvOrderListData.setAdapter(customerOrderAdapter);
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                binding.rvOrderListData.setVisibility(View.VISIBLE);
                binding.tvNoRecord.setVisibility(View.GONE);
                binding.cvSortByCard.setCardBackgroundColor(getColor(R.color.white));
                binding.cvPostOrders.setCardBackgroundColor(getColor(R.color.button_bg));
                binding.tvPostOrder.setTextColor(getColor(R.color.white));
                binding.cvPastProducts.setCardBackgroundColor(getColor(R.color.white));
                binding.tvPastProducts.setTextColor(getColor(R.color.black));
                apiCall();
            }
        });
        binding.cvPastProducts.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                productSortCheck = "date";
                pastProductsArrayList.clear();
                orderTitle = "product";
                orderFilter = "";
                layoutViewGone();
                sortByFun();
                page = 1;
                filters = 0;
                productFilterGone();
                binding.clFilterList.setVisibility(View.GONE);
                binding.ivBrandDownIcon.setVisibility(View.VISIBLE);
                binding.ivSellerNameProductDownIcon.setVisibility(View.VISIBLE);
                binding.ivCategoryDownIcon.setVisibility(View.VISIBLE);
                binding.ivSubcategoryDownIcon.setVisibility(View.VISIBLE);
                Constant.isSelectDeliveryStatus = -1;
                Constant.isSelectPaymentStatus = -1;
                binding.tvBrand.setText(R.string.brand);
                binding.tvSellerNameProduct.setText(R.string.sellerName);
                binding.tvCategory.setText(R.string.category);
                binding.tvSubcategory.setText(R.string.subcategory);
                binding.tvBrand.setTextColor(Color.parseColor("#F39404"));
                binding.tvSellerNameProduct.setTextColor(Color.parseColor("#F39404"));
                binding.tvCategory.setTextColor(Color.parseColor("#F39404"));
                binding.tvSubcategory.setTextColor(Color.parseColor("#F39404"));
                filterApiCallSellerName();
                filterApiCallDeliveryStatus();
                filterApiCallPaymentStatus();
                productBrandApiCall();
                productSellerNameApiCall();
                productCategoryApiCall();
                productSubCategoryApiCall();
                binding.clTitle.tvHeader.setText(getString(R.string.pastProducts));
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                binding.rvOrderListData.setVisibility(View.VISIBLE);
                binding.tvNoRecord.setVisibility(View.GONE);
                binding.cvSortByCard.setCardBackgroundColor(getColor(R.color.white));
                binding.cvPastProducts.setCardBackgroundColor(getColor(R.color.button_bg));
                binding.tvPastProducts.setTextColor(getColor(R.color.white));
                binding.cvPostOrders.setCardBackgroundColor(getColor(R.color.white));
                binding.tvPostOrder.setTextColor(getColor(R.color.black));
                productApiCall();
            }
        });
        binding.cvSortByCard.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                if (orderTitle.equals("order")) {
                    if (binding.cvSortByList.getVisibility() == View.VISIBLE) {
                        layoutViewGone();
                        binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                        binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                    } else {
                        layoutViewGone();
                        binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.line_bg1));
                        binding.cvSortByList.setVisibility(View.VISIBLE);
                        binding.ivOrderArrowUp.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (binding.cvSortByListProduct.getVisibility() == View.VISIBLE) {
                        layoutViewGone();
                        binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                        binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                    } else {
                        layoutViewGone();
                        binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.line_bg1));
                        binding.cvSortByListProduct.setVisibility(View.VISIBLE);
                        binding.ivOrderArrowUp.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        binding.clFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderTitle.equals("order")) {
                    if (binding.clFilterList.getVisibility() == View.VISIBLE) {
                        layoutViewGone();
                        binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                    } else {
                        layoutViewGone();
                        filterLayoutGone();
                        binding.ivDeliveryStatusDownIcon.setVisibility(View.VISIBLE);
                        binding.ivSellerNameDownIcon.setVisibility(View.VISIBLE);
                        binding.ivPaymentStatusDownIcon.setVisibility(View.VISIBLE);
                        binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                        binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                        binding.clFilterList.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (binding.clFilterListProducts.getVisibility() == View.VISIBLE) {
                        layoutViewGone();
                        binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                    } else {
                        layoutViewGone();
                        binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                        binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                        binding.clFilterListProducts.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        binding.tvNewOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 1;
                binding.tvOldNew.setTextColor(getResources().getColor(R.color.button_bg));
                binding.tvNewOld.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setText(binding.tvNewOld.getText().toString());
                orderListArray.clear();
                item_sort = "desc";
                layoutViewGone();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                apiCall();
            }
        });
        binding.tvOldNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 1;
                orderListArray.clear();
                binding.tvOldNew.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setTextColor(getResources().getColor(R.color.black));
                binding.tvNewOld.setTextColor(getResources().getColor(R.color.button_bg));
                binding.tvSortby.setText(binding.tvOldNew.getText().toString());
                orderListArray.clear();
                item_sort = "asc";
                layoutViewGone();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                apiCall();
            }
        });

        binding.cvDeliveryStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cvDeliverStatusList.getVisibility() == View.VISIBLE) {
                    filterLayoutGone();
                    binding.ivDeliveryStatusDownIcon.setVisibility(View.VISIBLE);
                } else {
                    filterLayoutGone();
                    binding.ivDeliveryStatusUpIcon.setVisibility(View.VISIBLE);
                    binding.cvDeliverStatusList.setVisibility(View.VISIBLE);
                }
                binding.ivPaymentStatusDownIcon.setVisibility(View.VISIBLE);
                binding.ivSellerNameDownIcon.setVisibility(View.VISIBLE);

            }
        });
        binding.cvSellerNameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cvSellerNameCardList.getVisibility() == View.VISIBLE) {
                    filterLayoutGone();
                    binding.ivSellerNameDownIcon.setVisibility(View.VISIBLE);
                } else {
                    filterLayoutGone();
                    binding.ivSellerNameUpIcon.setVisibility(View.VISIBLE);
                    binding.cvSellerNameCardList.setVisibility(View.VISIBLE);
                }
                binding.ivDeliveryStatusDownIcon.setVisibility(View.VISIBLE);
                binding.ivPaymentStatusDownIcon.setVisibility(View.VISIBLE);
            }
        });
        binding.cvPaymentStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cvPaymentStatusList.getVisibility() == View.VISIBLE) {
                    filterLayoutGone();
                    binding.ivPaymentStatusDownIcon.setVisibility(View.VISIBLE);
                } else {
                    filterLayoutGone();
                    binding.ivPaymentStatusUpIcon.setVisibility(View.VISIBLE);
                    binding.cvPaymentStatusList.setVisibility(View.VISIBLE);
                }
                binding.ivDeliveryStatusDownIcon.setVisibility(View.VISIBLE);
                binding.ivSellerNameDownIcon.setVisibility(View.VISIBLE);
            }
        });
        binding.cvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderListArray.clear();
                page = 1;
                ArrayList<String> orderFilterArray = new ArrayList<>();
                if (!Constant.sellerName.equals("")) {
                    orderFilterArray.add(Constant.sellerName);
                }
                if (!Constant.deliveryStatus.equals("")) {
                    orderFilterArray.add(Constant.deliveryStatus);
                } else {
                    binding.tvDeliveryStatus.setText(R.string.deliveryStatus);
                    binding.tvDeliveryStatus.setTextColor(Color.parseColor("#F39404"));
                    Constant.isSelectDeliveryStatus = -1;
                }
                if (!Constant.paymentStatus.equals("")) {
                    orderFilterArray.add(Constant.paymentStatus);
                } else {
                    binding.tvPaymentStatus.setText(R.string.paymentStatus);
                    binding.tvPaymentStatus.setTextColor(Color.parseColor("#F39404"));
                    Constant.isSelectPaymentStatus = -1;
                }
                orderFilter = String.valueOf(orderFilterArray);
                Log.e("TAG", "onClick:allFilter >>>>>>>>>>>>>>>" + orderFilterArray);
                filters = 1;
                layoutViewGone();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                apiCall();
            }
        });
        binding.rvOrderListData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                layoutViewGone();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                return false;
            }
        });
        binding.cmBottom.clHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(1);
            }
        });
        binding.cmBottom.clAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackClick(2);
            }
        });
        binding.clTitle.ivBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //products
        binding.tvOldNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productSortCheck = "date";
                sortDate = "asc";
                sortByFun();
                binding.tvOldNewProduct.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setText(binding.tvOldNewProduct.getText().toString());
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                layoutViewGone();
                pastProductsArrayList.clear();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                productApiCall();
            }
        });
        binding.tvNewOldProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productSortCheck = "date";
                sortDate = "desc";
                sortByFun();
                binding.tvNewOldProduct.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setText(binding.tvNewOldProduct.getText().toString());
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                layoutViewGone();
                pastProductsArrayList.clear();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                productApiCall();
            }
        });
        binding.tvHighLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productSortCheck = "qty";
                sortQuantity = "desc";
                sortByFun();
                binding.tvHighLow.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setText(binding.tvHighLow.getText().toString());
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                layoutViewGone();
                pastProductsArrayList.clear();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                productApiCall();
            }
        });
        binding.tvLowHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productSortCheck = "qty";
                sortQuantity = "asc";
                sortByFun();
                binding.tvLowHigh.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setText(binding.tvLowHigh.getText().toString());
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                layoutViewGone();
                pastProductsArrayList.clear();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                productApiCall();
            }
        });
        binding.tvAtoZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productSortCheck = "name";
                sortName = "asc";
                sortByFun();
                binding.tvAtoZ.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setText(binding.tvAtoZ.getText().toString());
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                layoutViewGone();
                pastProductsArrayList.clear();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                productApiCall();
            }
        });
        binding.tvZtoA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                productSortCheck = "name";
                sortName = "desc";
                sortByFun();
                binding.tvZtoA.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setTextColor(getResources().getColor(R.color.black));
                binding.tvSortby.setText(binding.tvZtoA.getText().toString());
                binding.cvSortByCard.setCardBackgroundColor(getResources().getColor(R.color.white));
                layoutViewGone();
                pastProductsArrayList.clear();
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                productApiCall();
            }
        });

        binding.cvBrand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cvBrandList.getVisibility() == View.VISIBLE) {
                    productFilterGone();
                    binding.ivBrandDownIcon.setVisibility(View.VISIBLE);
                    binding.cvBrandList.setVisibility(View.GONE);
                } else {
                    productFilterGone();
                    binding.ivBrandUpIcon.setVisibility(View.VISIBLE);
                    binding.cvBrandList.setVisibility(View.VISIBLE);
                }
                binding.ivSellerNameProductDownIcon.setVisibility(View.VISIBLE);
                binding.ivCategoryDownIcon.setVisibility(View.VISIBLE);
                binding.ivSubcategoryDownIcon.setVisibility(View.VISIBLE);
            }
        });
        binding.cvSellerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cvSellerNameList.getVisibility() == View.VISIBLE) {
                    productFilterGone();
                    binding.ivSellerNameProductDownIcon.setVisibility(View.VISIBLE);
                    binding.cvSellerNameList.setVisibility(View.GONE);
                } else {
                    productFilterGone();
                    binding.ivSellerNameProductDownIcon.setVisibility(View.VISIBLE);
                    binding.cvSellerNameList.setVisibility(View.VISIBLE);
                }
                binding.ivBrandDownIcon.setVisibility(View.VISIBLE);
                binding.ivCategoryDownIcon.setVisibility(View.VISIBLE);
                binding.ivSubcategoryDownIcon.setVisibility(View.VISIBLE);
            }
        });
        binding.cvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cvCategoryList.getVisibility() == View.VISIBLE) {
                    productFilterGone();
                    binding.ivCategoryDownIcon.setVisibility(View.VISIBLE);
                    binding.cvCategoryList.setVisibility(View.GONE);
                } else {
                    productFilterGone();
                    binding.ivCategoryUpIcon.setVisibility(View.VISIBLE);
                    binding.cvCategoryList.setVisibility(View.VISIBLE);
                }
                binding.ivBrandDownIcon.setVisibility(View.VISIBLE);
                binding.ivSellerNameProductDownIcon.setVisibility(View.VISIBLE);
                binding.ivSubcategoryDownIcon.setVisibility(View.VISIBLE);
            }
        });
        binding.cvSubcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cvSubcategoryList.getVisibility() == View.VISIBLE) {
                    productFilterGone();
                    binding.ivSubcategoryDownIcon.setVisibility(View.VISIBLE);
                    binding.cvSubcategoryList.setVisibility(View.GONE);
                } else {
                    productFilterGone();
                    binding.ivSubcategoryUpIcon.setVisibility(View.VISIBLE);
                    binding.cvSubcategoryList.setVisibility(View.VISIBLE);
                }
                binding.ivBrandDownIcon.setVisibility(View.VISIBLE);
                binding.ivSellerNameProductDownIcon.setVisibility(View.VISIBLE);
                binding.ivCategoryDownIcon.setVisibility(View.VISIBLE);
            }
        });
        binding.cvApplyProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pastProductsArrayList.clear();
                page = 1;
                ArrayList<String> productFilterArray = new ArrayList<>();
                if (!Constant.productBrand.equals("")) {
                    productFilterArray.add(Constant.productBrand);
                }
                if (!Constant.productCategory.equals("")) {
                    productFilterArray.add(Constant.productCategory);
                }
                if (!Constant.productSubCategory.equals("")) {
                    productFilterArray.add(Constant.productSubCategory);
                }
                if (!Constant.productSellerName.equals("")) {
                    productFilterArray.add(Constant.productSellerName);
                }
                orderFilter = String.valueOf(productFilterArray);
                Log.e("TAG", "onClick:allFilter >>>>>>>>>>>>>>>" + productFilterArray);
                layoutViewGone();
                filters = 1;
                binding.ivOrderArrowDown.setVisibility(View.VISIBLE);
                productApiCall();
            }
        });
    }

    private void productBrandApiCall() {
        productBrandArrayList.clear();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.productBrand)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: brand >>>>>" + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject resultObject = response.getJSONObject(ApiStringModel.data);
                                JSONArray resultJsonArray = resultObject.getJSONArray(ApiStringModel.result);
                                for (int i = 0; i < resultJsonArray.length(); i++) {
                                    JSONObject object = resultJsonArray.getJSONObject(i);
                                    productBrandArrayList.add(new ProductBranModel(object.getString(ApiStringModel.id), object.getString(ApiStringModel.name)));
                                    Log.e("TAG", "onResponse: brand >>>>>>" + object.getString("id"));
                                }
                                LinearLayoutManager manager = new LinearLayoutManager(OrdersListActivity.this, RecyclerView.VERTICAL, false);
                                ProductBrandAdapter productBrandAdapter = new ProductBrandAdapter(OrdersListActivity.this, productBrandArrayList, binding);
                                binding.rvBrandList.setLayoutManager(manager);
                                binding.rvBrandList.setAdapter(productBrandAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG", "JSONException: >>>>>>" + e.getMessage());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("TAG", "onError: >>>>>" + error.getMessage());
                    }
                });
    }

    private void productSellerNameApiCall() {
        productSellerArrayList.clear();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.sellers)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: seller >>>>>" + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject resultObject = response.getJSONObject(ApiStringModel.data);
                                JSONArray resultJsonArray = resultObject.getJSONArray(ApiStringModel.result);
                                for (int i = 0; i < resultJsonArray.length(); i++) {
                                    JSONObject object = resultJsonArray.getJSONObject(i);
                                    productSellerArrayList.add(new OrderSellerNameModel(object.getString(ApiStringModel.id), object.getString(ApiStringModel.name)));
                                    Log.e("TAG", "onResponse: seller >>>>>>" + object);
                                }
                                LinearLayoutManager manager = new LinearLayoutManager(OrdersListActivity.this, RecyclerView.VERTICAL, false);
                                ProductSellerNameAdapte productSellerNameAdapte = new ProductSellerNameAdapte(OrdersListActivity.this, productSellerArrayList, binding);
                                binding.rvSellerNameProductList.setLayoutManager(manager);
                                binding.rvSellerNameProductList.setAdapter(productSellerNameAdapte);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG", "JSONException: >>>>>>" + e.getMessage());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("TAG", "onError: >>>>>" + error.getMessage());
                    }
                });

    }

    private void productCategoryApiCall() {
        productCategoryArrayList.clear();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.productCategory)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: brand >>>>>" + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject resultObject = response.getJSONObject(ApiStringModel.data);
                                JSONArray resultJsonArray = resultObject.getJSONArray(ApiStringModel.result);
                                for (int i = 0; i < resultJsonArray.length(); i++) {
                                    JSONObject object = resultJsonArray.getJSONObject(i);
                                    productCategoryArrayList.add(new ProductBranModel(object.getString(ApiStringModel.id), object.getString(ApiStringModel.name)));
                                    Log.e("TAG", "onResponse: brand >>>>>>" + object.getString("id"));
                                }
                                LinearLayoutManager manager = new LinearLayoutManager(OrdersListActivity.this, RecyclerView.VERTICAL, false);
                                ProductCategoryAdapter productCategoryAdapter = new ProductCategoryAdapter(OrdersListActivity.this, productCategoryArrayList, binding);
                                binding.rvCategoryList.setLayoutManager(manager);
                                binding.rvCategoryList.setAdapter(productCategoryAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG", "JSONException: >>>>>>" + e.getMessage());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("TAG", "onError: >>>>>" + error.getMessage());
                    }
                });
    }

    private void productSubCategoryApiCall() {
        productSubCategoryArrayList.clear();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.productSubCategory)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: brand >>>>>" + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject resultObject = response.getJSONObject(ApiStringModel.data);
                                JSONArray resultJsonArray = resultObject.getJSONArray(ApiStringModel.result);
                                for (int i = 0; i < resultJsonArray.length(); i++) {
                                    JSONObject object = resultJsonArray.getJSONObject(i);
                                    productSubCategoryArrayList.add(new ProductBranModel(object.getString(ApiStringModel.id), object.getString(ApiStringModel.name)));
                                    Log.e("TAG", "onResponse: brand >>>>>>" + object.getString("id"));
                                }
                                LinearLayoutManager manager = new LinearLayoutManager(OrdersListActivity.this, RecyclerView.VERTICAL, false);
                                ProductSubCategoryAdapter productSubCategoryAdapter = new ProductSubCategoryAdapter(OrdersListActivity.this, productSubCategoryArrayList, binding);
                                binding.rvSubcategoryList.setLayoutManager(manager);
                                binding.rvSubcategoryList.setAdapter(productSubCategoryAdapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG", "JSONException: >>>>>>" + e.getMessage());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("TAG", "onError: >>>>>" + error.getMessage());
                    }
                });
    }

    private void productFilterGone() {
        binding.cvBrandList.setVisibility(View.GONE);
        binding.cvSellerNameList.setVisibility(View.GONE);
        binding.cvCategoryList.setVisibility(View.GONE);
        binding.cvSubcategoryList.setVisibility(View.GONE);
        binding.ivBrandDownIcon.setVisibility(View.GONE);
        binding.ivBrandUpIcon.setVisibility(View.GONE);
        binding.ivSellerNameProductDownIcon.setVisibility(View.GONE);
        binding.ivSellerNameProductUpIcon.setVisibility(View.GONE);
        binding.ivCategoryDownIcon.setVisibility(View.GONE);
        binding.ivCategoryUpIcon.setVisibility(View.GONE);
        binding.ivSubcategoryDownIcon.setVisibility(View.GONE);
        binding.ivSubcategoryUpIcon.setVisibility(View.GONE);
    }

    private void sortByFun() {
        binding.tvSortby.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvSortby.setText(getString(R.string.sort));
        binding.tvNewOldProduct.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvHighLow.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvLowHigh.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvAtoZ.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvZtoA.setTextColor(getResources().getColor(R.color.button_bg));
        binding.tvOldNewProduct.setTextColor(getResources().getColor(R.color.button_bg));
    }

    private void productApiCall() {
        Log.e("TAG", " call product Api");
        progressDialog.show();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        ANRequest.GetRequestBuilder androidNetwork = AndroidNetworking.get(ApiEndPoints.products);
        androidNetwork.addQueryParameter(ApiStringModel.customerId, customerModel.getCustomerListModel().getId());
        androidNetwork.addQueryParameter(ApiStringModel.page, "1");
        androidNetwork.addQueryParameter(ApiStringModel.limit, "10");
        switch (productSortCheck) {
            case "name":
                androidNetwork.addQueryParameter(ApiStringModel.sortName, sortName);
                break;
            case "date":
                androidNetwork.addQueryParameter(ApiStringModel.sortDate, sortDate);
                break;
            case "qty":
                androidNetwork.addQueryParameter(ApiStringModel.sortQuantity, sortQuantity);
                break;
        }
        if (filters == 1) {
            androidNetwork.addQueryParameter(ApiStringModel.filter, orderFilter);
        }
        androidNetwork.setTag("test");
        androidNetwork.addHeaders("X-Openerp-Session-Id", separated[1]);
        androidNetwork.setPriority(Priority.LOW).build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: product >>>>>>> " + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject dataObject = response.getJSONObject(ApiStringModel.data);
                                JSONArray resultArray = dataObject.getJSONArray(ApiStringModel.result);
                                Log.e("TAG", "onResponse: result >>>>>> " + resultArray.length());

                                binding.rvOrderListData.setVisibility(View.VISIBLE);
                                binding.tvNoRecord.setVisibility(View.GONE);
                                for (int i = 0; i < resultArray.length(); i++) {
                                    JSONObject object = resultArray.getJSONObject(i);
                                    pastProductsArrayList.add(new ProductsListModel(object.getString(ApiStringModel.productName),
                                            object.getString(ApiStringModel.productBrand),
                                            object.getString(ApiStringModel.productCategory),
                                            object.getString(ApiStringModel.productCustomerPrice),
                                            object.getString(ApiStringModel.stock),
                                            object.getString(ApiStringModel.productImage),
                                            object.getString(ApiStringModel.lastPurchasedQty),
                                            object.getString(ApiStringModel.totalPurchasedProductQty),
                                            object.getString(ApiStringModel.defaultCode),
                                            object.getString(ApiStringModel.salesperson),
                                            object.getString(ApiStringModel.lastPurchasedDate),
                                            object.getString(ApiStringModel.lastPurchasedPrice)));
                                    Log.e("TAG", "onResponse: product data >> name " + pastProductsArrayList.get(i).getProductBrand());
                                    Log.e("TAG", "onResponse: product data >> date " + pastProductsArrayList.get(i).getLastPurchasedDate());
                                    Log.e("TAG", "onResponse: product data >> qua " + pastProductsArrayList.get(i).getTotalPurchasedProductQty());
                                }

                                Log.i("-->", "product Array :" + new Gson().toJson(pastProductsArrayList));

                                if (orderTitle.equals("product")) {
                                    Log.e("TAG", "onResponse: products");

                                    if (callProduct) {
                                        callProduct = false;
                                        customerProductsAdapter = new CustomerProductsAdapter(OrdersListActivity.this, pastProductsArrayList);
                                        binding.rvOrderListData.setAdapter(customerProductsAdapter);
                                        Log.e("TAG past product", "if");

                                    } else {
                                        customerProductsAdapter.notifyDataSetChanged();
                                        Log.e("TAG past product", "else");
                                    }
                                }

                            }

                            if (pastProductsArrayList.size() != 0) {
                                binding.rvOrderListData.setVisibility(View.VISIBLE);
                                binding.tvNoRecord.setVisibility(View.GONE);
                            } else {
                                binding.rvOrderListData.setVisibility(View.GONE);
                                binding.tvNoRecord.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("TAG", "onError: product " + error);
                        progressDialog.dismiss();
                    }
                });
    }

    private void filterApiCallDeliveryStatus() {
        deliveryStatusArrayList.clear();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.deliveryStatus)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: delivery >>>>>" + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject resultObject = response.getJSONObject(ApiStringModel.data);
                                JSONArray resultJsonArray = resultObject.getJSONArray(ApiStringModel.result);
                                for (int i = -1; i < resultJsonArray.length(); i++) {
                                    OrderDeliveryStatusModel orderDeliveryStatusModel = new OrderDeliveryStatusModel();
                                    if (i == -1) {
                                        orderDeliveryStatusModel.setDeliveryName(getString(R.string.pleaseSelect));
                                        orderDeliveryStatusModel.setDeliveryState("");
                                        deliveryStatusArrayList.add(orderDeliveryStatusModel);
                                    } else {
                                        JSONObject object = resultJsonArray.getJSONObject(i);
                                        deliveryStatusArrayList.add(new OrderDeliveryStatusModel(object.getString(ApiStringModel.state), object.getString(ApiStringModel.name)));
                                        Log.e("TAG", "onResponse: delivery >>>>>>" + object.getString("state"));
                                    }
                                }
                                LinearLayoutManager manager = new LinearLayoutManager(OrdersListActivity.this, RecyclerView.VERTICAL, false);
                                OrderDeliveryStatusAdapter orderDeliveryStatusAdapte = new OrderDeliveryStatusAdapter(OrdersListActivity.this, deliveryStatusArrayList, binding);
                                binding.rvDeliverStatus.setLayoutManager(manager);
                                binding.rvDeliverStatus.setAdapter(orderDeliveryStatusAdapte);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG", "JSONException: >>>>>>" + e.getMessage());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("TAG", "onError: >>>>>" + error.getMessage());
                    }
                });
    }

    private void filterApiCallSellerName() {
        sellerNameArrayList.clear();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.sellers)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: seller >>>>>" + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject resultObject = response.getJSONObject(ApiStringModel.data);
                                JSONArray resultJsonArray = resultObject.getJSONArray(ApiStringModel.result);
                                for (int i = 0; i < resultJsonArray.length(); i++) {
                                    JSONObject object = resultJsonArray.getJSONObject(i);
                                    sellerNameArrayList.add(new OrderSellerNameModel(object.getString(ApiStringModel.id), object.getString(ApiStringModel.name)));
                                    Log.e("TAG", "onResponse: seller >>>>>>" + object);
                                }
                                LinearLayoutManager manager = new LinearLayoutManager(OrdersListActivity.this, RecyclerView.VERTICAL, false);
                                OrderSellerNameAdapter orderSellerNameAdapte = new OrderSellerNameAdapter(OrdersListActivity.this, sellerNameArrayList, binding);
                                binding.rvSellerName.setLayoutManager(manager);
                                binding.rvSellerName.setAdapter(orderSellerNameAdapte);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG", "JSONException: >>>>>>" + e.getMessage());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("TAG", "onError: >>>>>" + error.getMessage());
                    }
                });

    }

    private void filterApiCallPaymentStatus() {
        paymentStatusArrayList.clear();
        String currentString = Constant.cookie;
        String[] separated = currentString.split("=");
        AndroidNetworking.get(ApiEndPoints.paymentStatus)
                .setTag("test")
                .addHeaders("X-Openerp-Session-Id", separated[1])
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("TAG", "onResponse: payment >>>>>" + response);
                        try {
                            if (response.getString(ApiStringModel.status).equals("200") || response.getString(ApiStringModel.message).equals("Success")) {
                                JSONObject resultObject = response.getJSONObject(ApiStringModel.data);
                                JSONArray resultJsonArray = resultObject.getJSONArray(ApiStringModel.result);
                                for (int i = -1; i < resultJsonArray.length(); i++) {
                                    OrderPaymentStatusModel orderPaymentStatusModel = new OrderPaymentStatusModel();
                                    if (i == -1) {
                                        orderPaymentStatusModel.setPaymentStateName(getString(R.string.pleaseSelect));
                                        orderPaymentStatusModel.setPaymentState("");
                                        paymentStatusArrayList.add(orderPaymentStatusModel);
                                    } else {
                                        JSONObject object = resultJsonArray.getJSONObject(i);
                                        paymentStatusArrayList.add(new OrderPaymentStatusModel(object.getString(ApiStringModel.paymentState), object.getString(ApiStringModel.name)));
                                        Log.e("TAG", "onResponse: payment >>>>>>" + object);
                                    }
                                }
                                LinearLayoutManager manager = new LinearLayoutManager(OrdersListActivity.this, RecyclerView.VERTICAL, false);
                                OrderPaymentStatusAdapter orderPaymentStatusAdapte = new OrderPaymentStatusAdapter(OrdersListActivity.this, paymentStatusArrayList, binding);
                                binding.rvPaymentStatus.setLayoutManager(manager);
                                binding.rvPaymentStatus.setAdapter(orderPaymentStatusAdapte);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("TAG", "JSONException: >>>>>>" + e.getMessage());
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        progressDialog.dismiss();
                        Log.e("TAG", "onError: >>>>>" + error.getMessage());
                    }
                });

    }

    private void layoutViewGone() {
        binding.cvSortByList.setVisibility(View.GONE);
        binding.clFilterList.setVisibility(View.GONE);
        binding.ivOrderArrowUp.setVisibility(View.GONE);
        binding.ivOrderArrowDown.setVisibility(View.INVISIBLE);

        //products
        binding.cvSortByListProduct.setVisibility(View.GONE);
        binding.clFilterListProducts.setVisibility(View.GONE);

    }

    private void filterLayoutGone() {
        binding.cvDeliverStatusList.setVisibility(View.GONE);
        binding.cvSellerNameCardList.setVisibility(View.GONE);
        binding.cvPaymentStatusList.setVisibility(View.GONE);
        binding.ivDeliveryStatusDownIcon.setVisibility(View.GONE);
        binding.ivDeliveryStatusUpIcon.setVisibility(View.GONE);
        binding.ivSellerNameDownIcon.setVisibility(View.GONE);
        binding.ivSellerNameUpIcon.setVisibility(View.GONE);
        binding.ivPaymentStatusUpIcon.setVisibility(View.GONE);
        binding.ivPaymentStatusDownIcon.setVisibility(View.GONE);
        binding.clFilterList.setVisibility(View.VISIBLE);
    }

    private void onBackClick(int checkClick) {
        if (checkClick == 0) {
            Constant.isSelectDeliveryStatus = -1;
            Constant.isSelectPaymentStatus = -1;
            if (orderTitle.equals("product")) {
                Intent i = new Intent(OrdersListActivity.this, CustomersListActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } else {
                finish();
            }
        } else if (checkClick == 1) {
            Common.openActivity(getApplicationContext(), DashboardActivity.class);
        } else if (checkClick == 2) {
            Intent intent = new Intent(getApplicationContext(), AccountDashboardActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        onBackClick(0);

    }
}