package com.android.agzakhanty.sprints.two.views.fragments;


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
import com.android.agzakhanty.sprints.two.adapters.AdsAdapter;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.android.agzakhanty.sprints.two.views.AdDetails;
import com.android.agzakhanty.sprints.two.views.AddAdvertismentRate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdsFragment extends Fragment {

    @BindView(R.id.pharmaciesList)
    ListView adsList;
    @BindView(R.id.noNearby)
    TextView noNearby;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;


    public AdsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ads_list, container, false);
        ButterKnife.bind(this, view);

        return view;
    }


    public void loadActiveAds(String query) {
        progressBar.setVisibility(View.GONE);
        String custJSON = PrefManager.getInstance(getContext()).read(Constants.CIRCLE_ALL_ADS_LIST);
        final ArrayList<AdResponseModel> copy = new Gson().fromJson(custJSON, new TypeToken<ArrayList<AdResponseModel>>() {
        }.getType());
        ArrayList<AdResponseModel> activeAds = new ArrayList<>();
        ArrayList<AdResponseModel> searchResultsIfAny = new ArrayList<>();
        for (int i = 0; i < copy.size(); i++) {
//            Log.d("TEST_ADS", copy.get(i).getIsActive());
            //Log.d("TEST_PAGE", copy.get(i).getName() + " " + copy.get(i).getActive());
            if (copy.get(i).getIsActive() != null &&
                    copy.get(i).getIsActive().equalsIgnoreCase("y")) {
                //Log.d("TEST_PAGE", copy.get(i).getName() + " removed");
                activeAds.add(copy.get(i));
            }
        }
        if (!query.isEmpty()) {

            for (int i = 0; i < activeAds.size(); i++) {
                if (activeAds.get(i).getPcyName().contains(query))
                    searchResultsIfAny.add(activeAds.get(i));
            }

            AdsAdapter adapter = new AdsAdapter(searchResultsIfAny, getContext());
            Log.d("TEST_PAGE4", copy.size() + "");
            if (searchResultsIfAny.size() > 0) {
                adsList.setAdapter(adapter);
                noNearby.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                adsList.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {
                noNearby.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adsList.setVisibility(View.GONE);
            }
        } else {
            AdsAdapter adapter = new AdsAdapter(activeAds, getContext());
            Log.d("TEST_PAGE4", copy.size() + "");
            if (activeAds.size() > 0) {
                adsList.setAdapter(adapter);
                noNearby.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                adsList.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {
                noNearby.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adsList.setVisibility(View.GONE);
            }

        }
    }

    public void loadInactiveAds(String query) {
        progressBar.setVisibility(View.GONE);
        String custJSON = PrefManager.getInstance(getContext()).read(Constants.CIRCLE_ALL_ADS_LIST);
        final ArrayList<AdResponseModel> copy = new Gson().fromJson(custJSON, new TypeToken<ArrayList<AdResponseModel>>() {
        }.getType());
        ArrayList<AdResponseModel> inActiveAds = new ArrayList<>();
        ArrayList<AdResponseModel> searchResultsIfAny = new ArrayList<>();
        for (int i = 0; i < copy.size(); i++) {
//            Log.d("TEST_ADS2", copy.get(i).getIsActive());
            //Log.d("TEST_PAGE", copy.get(i).getName() + " " + copy.get(i).getActive());
            if (copy.get(i).getIsActive() != null
                    && (
                    copy.get(i).getIsActive().equalsIgnoreCase("p") ||
                            copy.get(i).getIsActive().equalsIgnoreCase("n")
            )) {
                //Log.d("TEST_PAGE", copy.get(i).getName() + " removed");
                inActiveAds.add(copy.get(i));
            }
        }

        if (!query.isEmpty()) {

            for (int i = 0; i < inActiveAds.size(); i++) {
                if (inActiveAds.get(i).getPcyName().contains(query))
                    searchResultsIfAny.add(inActiveAds.get(i));
            }

            AdsAdapter adapter = new AdsAdapter(searchResultsIfAny, getContext());
            Log.d("TEST_PAGE4", copy.size() + "");
            if (searchResultsIfAny.size() > 0) {
                adsList.setAdapter(adapter);
                noNearby.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                adsList.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {
                noNearby.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adsList.setVisibility(View.GONE);
            }
        } else {

            AdsAdapter adapter = new AdsAdapter(inActiveAds, getContext());
            Log.d("TEST_PAGE4", copy.size() + "");
            if (inActiveAds.size() > 0) {
                adsList.setAdapter(adapter);
                noNearby.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                adsList.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {
                noNearby.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                adsList.setVisibility(View.GONE);
            }
        }

    }

}
