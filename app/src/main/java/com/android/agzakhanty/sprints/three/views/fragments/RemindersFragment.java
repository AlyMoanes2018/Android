package com.android.agzakhanty.sprints.three.views.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.three.adapters.RemindersAdapter;
import com.android.agzakhanty.sprints.three.models.Reminder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RemindersFragment extends Fragment {

    @BindView(R.id.remindersList)
    ListView remindersList;
    @BindView(R.id.noReminders)
    TextView noReminders;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    public RemindersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    public void loadActiveReminders(String query) {
        progressBar.setVisibility(View.GONE);
        String custJSON = PrefManager.getInstance(getContext()).read(Constants.REMINDER_ALL_CUSTOMER_REMINDER);
        final List<Reminder> copy = new Gson().fromJson(custJSON, new TypeToken<ArrayList<Reminder>>() {
        }.getType());
        ArrayList<Reminder> activeReminders = new ArrayList<>();
        ArrayList<Reminder> searchResultsIfAny = new ArrayList<>();
        if (copy == null)
            updateUIForNoRemindersFound();
        else {
            for (int i = 0; i < copy.size(); i++) {
                //Log.d("TEST_PAGE", copy.get(i).getName() + " " + copy.get(i).getActive());
                if (copy.get(i).getStatus() == null ||
                        copy.get(i).getStatus().equalsIgnoreCase("y")) {
                    //Log.d("TEST_PAGE", copy.get(i).getName() + " removed");
                    activeReminders.add(copy.get(i));
                }
            }

            if (!query.isEmpty()) {

                // date, search by order date
                for (int i = 0; i < copy.size(); i++) {
                    if (copy.get(i).getDate() != null &&
                            copy.get(i).getDate().contains(String.valueOf(query)))
                        searchResultsIfAny.add(copy.get(i));
                }


                RemindersAdapter adapter = new RemindersAdapter(searchResultsIfAny, getContext());
                Log.d("TEST_PAGE4", copy.size() + "");
                if (searchResultsIfAny.size() > 0) {
                    remindersList.setAdapter(adapter);
                    noReminders.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    remindersList.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    noReminders.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    remindersList.setVisibility(View.GONE);
                }
            } else {
                RemindersAdapter adapter = new RemindersAdapter(activeReminders, getContext());
                Log.d("TEST_PAGE4", copy.size() + "");
                if (copy.size() > 0) {
                    remindersList.setAdapter(adapter);
                    noReminders.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    remindersList.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    noReminders.setVisibility(View.VISIBLE);
                    remindersList.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }

    public void loadCompletedReminders(String query) {
        progressBar.setVisibility(View.GONE);
        String custJSON = PrefManager.getInstance(getContext()).read(Constants.REMINDER_ALL_CUSTOMER_REMINDER);
        final ArrayList<Reminder> copy = new Gson().fromJson(custJSON, new TypeToken<ArrayList<Reminder>>() {
        }.getType());
        ArrayList<Reminder> completedReminders = new ArrayList<>();
        ArrayList<Reminder> searchResultsIfAny = new ArrayList<>();
        if (copy == null)
            updateUIForNoRemindersFound();
        else {
            for (int i = 0; i < copy.size(); i++) {
                //Log.d("TEST_PAGE2", copy.get(i).getName() + " " + copy.get(i).getActive());
                if (copy.get(i).getStatus() == null ||
                        copy.get(i).getStatus().equalsIgnoreCase("n")) {
                    //Log.d("TEST_PAGE2", copy.get(i).getName() + " removed");
                    completedReminders.add(copy.get(i));
                }
            }
            if (!query.isEmpty()) {

                try {

                    // number, search by order num
                    for (int i = 0; i < copy.size(); i++) {
                        if (copy.get(i).getDate() != null &&
                                copy.get(i).getDate().contains(query))
                            searchResultsIfAny.add(copy.get(i));
                    }
                } catch (Exception ex) {
                    //text, search by pcy name
                    for (int i = 0; i < copy.size(); i++) {
                        if (copy.get(i).getName() != null &&
                                copy.get(i).getName().contains(query))
                            searchResultsIfAny.add(copy.get(i));
                    }
                }


                RemindersAdapter adapter = new RemindersAdapter(searchResultsIfAny, getContext());
                Log.d("TEST_PAGE4", copy.size() + "");
                if (searchResultsIfAny.size() > 0) {
                    remindersList.setAdapter(adapter);
                    noReminders.setVisibility(View.GONE);
                    remindersList.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } else {
                    noReminders.setVisibility(View.VISIBLE);
                    remindersList.setVisibility(View.GONE);
                }
            } else {
                RemindersAdapter adapter = new RemindersAdapter(completedReminders, getContext());
                Log.d("TEST_PAGE4", copy.size() + "");
                if (copy.size() > 0) {
                    remindersList.setAdapter(adapter);
                    noReminders.setVisibility(View.GONE);
                    remindersList.setVisibility(View.VISIBLE);
                } else {
                    noReminders.setVisibility(View.VISIBLE);
                    remindersList.setVisibility(View.GONE);
                }
            }
        }
    }

    public void updateUIForNoRemindersFound() {
        noReminders.setVisibility(View.VISIBLE);
        remindersList.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }
}
