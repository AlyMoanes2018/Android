package com.android.agzakhanty.sprints.two.views.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.two.adapters.AdsAdapter;
import com.android.agzakhanty.sprints.two.adapters.CircleFullPharmaciesAdapter;
import com.android.agzakhanty.sprints.two.adapters.OrdersAdapter;
import com.android.agzakhanty.sprints.two.models.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    @BindView(R.id.ordersList)
    ListView ordersList;
    @BindView(R.id.noOrders)
    TextView noOrders;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    public void loadActiveOrders(String query) {
        progressBar.setVisibility(View.GONE);
        String custJSON = PrefManager.getInstance(getContext()).read(Constants.ORDER_ALL_CUSTOMER_ORDER);
        final ArrayList<Order> copy = new Gson().fromJson(custJSON, new TypeToken<ArrayList<Order>>() {
        }.getType());
        ArrayList<Order> activeOrders = new ArrayList<>();
        ArrayList<Order> searchResultsIfAny = new ArrayList<>();
        if (copy == null)
            updateUIForNoOrdersFound();
        else {
            for (int i = 0; i < copy.size(); i++) {
                //Log.d("TEST_PAGE", copy.get(i).getName() + " " + copy.get(i).getActive());
                if (copy.get(i).getStatus() == null ||
                        copy.get(i).getStatus().equalsIgnoreCase("y")) {
                    //Log.d("TEST_PAGE", copy.get(i).getName() + " removed");
                    activeOrders.add(copy.get(i));
                }
            }

            if (!query.isEmpty()) {

                try {
                    int orderNum = Integer.parseInt(query);
                    // number, search by order num
                    for (int i = 0; i < activeOrders.size(); i++) {
                        if (activeOrders.get(i).getCstOrderNo() != null &&
                                activeOrders.get(i).getCstOrderNo().contains(String.valueOf(orderNum)))
                            searchResultsIfAny.add(activeOrders.get(i));
                    }
                } catch (Exception ex) {
                    //text, search by pcy name
                    for (int i = 0; i < activeOrders.size(); i++) {
                        if (activeOrders.get(i).getPcyName() != null &&
                                activeOrders.get(i).getPcyName().contains(query))
                            searchResultsIfAny.add(activeOrders.get(i));
                    }
                }


                OrdersAdapter adapter = new OrdersAdapter(searchResultsIfAny, getContext());
                Log.d("TEST_PAGE4", copy.size() + "");
                if (searchResultsIfAny.size() > 0) {
                    ordersList.setAdapter(adapter);
                    noOrders.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    ordersList.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    noOrders.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    ordersList.setVisibility(View.GONE);
                }
            } else {
                OrdersAdapter adapter = new OrdersAdapter(activeOrders, getContext());
                Log.d("TEST_PAGE4", copy.size() + "");
                if (activeOrders.size() > 0) {
                    ordersList.setAdapter(adapter);
                    noOrders.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    ordersList.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    noOrders.setVisibility(View.VISIBLE);
                    ordersList.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }

    public void loadInactiveOrders(String query) {
        progressBar.setVisibility(View.GONE);
        String custJSON = PrefManager.getInstance(getContext()).read(Constants.ORDER_ALL_CUSTOMER_ORDER);
        final ArrayList<Order> copy = new Gson().fromJson(custJSON, new TypeToken<ArrayList<Order>>() {
        }.getType());
        ArrayList<Order> InActiveOrders = new ArrayList<>();
        ArrayList<Order> searchResultsIfAny = new ArrayList<>();
        if (copy == null)
            updateUIForNoOrdersFound();
        else {
            for (int i = 0; i < copy.size(); i++) {
                //Log.d("TEST_PAGE2", copy.get(i).getName() + " " + copy.get(i).getActive());
                if (copy.get(i).getStatus() == null ||
                        copy.get(i).getStatus().equalsIgnoreCase("p") ||
                        copy.get(i).getStatus().equalsIgnoreCase("n")) {
                    Log.d("TEST_PAGE2", copy.get(i).getCstOrderNo() + " removed");
                    InActiveOrders.add(copy.get(i));
                }
            }
            if (!query.isEmpty()) {

                try {
                    int orderNum = Integer.parseInt(query);
                    // number, search by order num
                    for (int i = 0; i < InActiveOrders.size(); i++) {
                        if (InActiveOrders.get(i).getCstOrderNo() != null &&
                                InActiveOrders.get(i).getCstOrderNo().contains(String.valueOf(orderNum)))
                            searchResultsIfAny.add(InActiveOrders.get(i));
                    }
                } catch (Exception ex) {
                    //text, search by pcy name
                    for (int i = 0; i < InActiveOrders.size(); i++) {
                        if (InActiveOrders.get(i).getPcyName() != null &&
                                InActiveOrders.get(i).getPcyName().contains(query))
                            searchResultsIfAny.add(InActiveOrders.get(i));
                    }
                }


                OrdersAdapter adapter = new OrdersAdapter(searchResultsIfAny, getContext());
                Log.d("TEST_PAGE4", copy.size() + "");
                if (searchResultsIfAny.size() > 0) {
                    ordersList.setAdapter(adapter);
                    noOrders.setVisibility(View.GONE);
                    ordersList.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    noOrders.setVisibility(View.VISIBLE);
                    ordersList.setVisibility(View.GONE);
                }
            } else {
                OrdersAdapter adapter = new OrdersAdapter(InActiveOrders, getContext());
                Log.d("TEST_PAGE4", copy.size() + "");
                if (InActiveOrders.size() > 0) {
                    ordersList.setAdapter(adapter);
                    noOrders.setVisibility(View.GONE);
                    ordersList.setVisibility(View.VISIBLE);
                } else {
                    noOrders.setVisibility(View.VISIBLE);
                    ordersList.setVisibility(View.GONE);
                }
            }
        }
    }

    public void updateUIForNoOrdersFound() {
        noOrders.setVisibility(View.VISIBLE);
        ordersList.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
}
