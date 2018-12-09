package com.android.agzakhanty.sprints.three.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.LocalDBHelper;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.three.models.Reminder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aly on 27/04/2018.
 */

public class RemindersAdapter extends ArrayAdapter<Reminder> {

    List<Reminder> reminders;
    Context context;
    PrefManager prefManager;
    LocalDBHelper db;


    // View lookup cache
    private static class ViewHolder {
        TextView pharmacyName, date, statusTV;
        RelativeLayout mainLayout;
    }

    public RemindersAdapter(List<Reminder> rdrs, Context context) {
        super(context, R.layout.reminder_list_item, rdrs);
        this.context = context;
        this.reminders = rdrs;
        prefManager = PrefManager.getInstance(this.context);
        db = new LocalDBHelper(context);
    }

    @Override
    public int getCount() {
        return reminders.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final Reminder reminder = reminders.get(i);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.reminder_list_item, viewGroup, false);
            viewHolder.pharmacyName = (TextView) view.findViewById(R.id.nameTV);
            viewHolder.date = (TextView) view.findViewById(R.id.dateTV);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
            viewHolder.statusTV = (TextView) view.findViewById(R.id.statusTV);
            result = view;


            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }


        viewHolder.pharmacyName.setText(reminder.getName());
        viewHolder.date.setText(context.getResources().getString(R.string.orderDate) + " " +
                reminder.getDate().split("T")[0]);


        if (reminder.getStatus().equalsIgnoreCase("y"))
            viewHolder.statusTV.setVisibility(View.VISIBLE);
        else if (reminder.getStatus().equalsIgnoreCase("n"))
            viewHolder.statusTV.setVisibility(View.GONE);


        return result;
    }




}
