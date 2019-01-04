package com.android.agzakhanty.sprints.two.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.views.Circles;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.two.views.NewOrder;
import com.android.agzakhanty.sprints.two.views.SearchCirclePharmacyByName;
import com.android.agzakhanty.sprints.two.views.ViewOrderDetails;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Aly on 27/04/2018.
 */

public class OrdersAdapter extends ArrayAdapter<Order> {

    ArrayList<Order> orders;
    Context context;
    PrefManager prefManager;


    // View lookup cache
    private static class ViewHolder {
        TextView pharmacyName, date, status, totalNumOfProducts, orderNum, products;
        CircleImageView pharmacyLogo;
        RelativeLayout mainLayout;
    }

    public OrdersAdapter(ArrayList<Order> ordrs, Context context) {
        super(context, R.layout.order_list_item, ordrs);
        this.context = context;
        this.orders = ordrs;
        prefManager = PrefManager.getInstance(this.context);
    }

    @Override
    public int getCount() {
        return orders.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        Order order = orders.get(i);
        if (order != null)
            Log.d("TEST_NULL", "NOT NULL");
        else Log.d("TEST_NULL", "NULL");
        String orderFinalStatus = "";
        if (order.getStatusId().equals("1") || order.getStatusId().equals("2") || order.getStatusId().equals("3") || order.getStatusId().equals("4"))
            orderFinalStatus = context.getResources().getString(R.string.preparingOrder);
        else if (order.getStatusId().equals("5"))
            orderFinalStatus = context.getResources().getString(R.string.deliveringOrder);
        else if (order.getStatusId().equals("6"))
            orderFinalStatus = context.getResources().getString(R.string.deliveredOrder);
        else if (order.getStatusId().equals("7"))
            orderFinalStatus = context.getResources().getString(R.string.deletedOrder);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.order_list_item, viewGroup, false);
            viewHolder.pharmacyName = (TextView) view.findViewById(R.id.nameTV);
            viewHolder.date = (TextView) view.findViewById(R.id.dateTV);
            viewHolder.status = (TextView) view.findViewById(R.id.statusTV);
            viewHolder.totalNumOfProducts = (TextView) view.findViewById(R.id.totalTV);
            viewHolder.orderNum = (TextView) view.findViewById(R.id.orderNumTV);
            viewHolder.products = (TextView) view.findViewById(R.id.productsTV);
            viewHolder.pharmacyLogo = (CircleImageView) view.findViewById(R.id.logo);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
            result = view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }

        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Activity) context).getCallingActivity() != null) {
                    Intent intent = new Intent();
                    if (orders.get(i).getStatusId().equalsIgnoreCase("6")) {
                        // select an order
                        intent.putExtra("order", orders.get(i).getOrderId());
                        intent.putExtra("orderPCYID", orders.get(i).getPcyId());
                        ((Activity) context).setResult(RESULT_OK, intent);
                        //close this Activity...
                        ((Activity) context).finish();
                    } else
                        Toast.makeText(context, context.getResources().getString(R.string.noOrdersForResult), Toast.LENGTH_LONG).show();

                } else {
                    //view order details
                    Intent intent = new Intent(context, ViewOrderDetails.class);
                    intent.putExtra("order", orders.get(i).getOrderId());
                    ((Activity) context).startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    ((Activity) context).finish();
                }
            }
        });

        viewHolder.pharmacyName.setText(order.getPcyName());
        viewHolder.date.setText(context.getResources().getString(R.string.orderDate) + " " +
                order.getOrderDate().split("T")[0]);
        viewHolder.status.setText(orderFinalStatus);
        if (order.getNoofItems() != null)
            viewHolder.totalNumOfProducts.setText(context.getResources().getString(R.string.orderProd) +
                    " " + order.getNoofItems());
        else
            viewHolder.totalNumOfProducts.setText(context.getResources().getString(R.string.orderProd) +
                    " 0");
        viewHolder.orderNum.setText(context.getResources().getString(R.string.orderNum) +
                " " + order.getCstOrderNo());

        String products = "";
        for (int j = 0; j < order.getListItem().size(); j++) {
            if (prefManager.readInt(Constants.APP_LANGUAGE) == 0) {
                if (j == order.getListItem().size() - 1)
                    products += order.getListItem().get(j).getNameAr();
                else products += order.getListItem().get(j).getNameAr() + " - ";

            } else if (prefManager.readInt(Constants.APP_LANGUAGE) == 1) {
                if (j == order.getListItem().size() - 1)
                    products += order.getListItem().get(j).getNameEn();
                else products += order.getListItem().get(j).getNameEn() + " - ";
            }
        }

        viewHolder.products.setText(products);
        if (order.getPcyImage() != null && !order.getPcyImage().isEmpty()) {

            Glide
                    .with(context)
                    .load(Constants.BASE_URL + order.getPcyImage())
                    .centerCrop()
                    .into(viewHolder.pharmacyLogo);
        } else {
            Glide
                    .with(context)
                    .load(Constants.NO_IMG_FOUND_URL)
                    .centerCrop()
                    .into(viewHolder.pharmacyLogo);
        }

        return view;
    }

    /*private void goToUpdateWS(final ViewHolder viewHolder) {
        String custJSON = PrefManager.getInstance(context).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        customer.setFavPcy("15");
        Log.d("TEST_UPDATE", new Gson().toJson(customer));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.updateCustomerInfo(customer.getId(), customer);
        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                if (response.body() != null) {
                    if (response.body().getActiveOrNot().equalsIgnoreCase("true")) {
                        viewHolder.favorite.setBackground(context.getResources().getDrawable(R.drawable.ic_favorite_selected));
                        PrefManager.getInstance(context).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(response.body().getCstmr()));
                        Intent intent = new Intent(context, InterestsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");

            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(context, context.getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
            }
        });
    }*/


}
