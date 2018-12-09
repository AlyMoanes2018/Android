package com.android.agzakhanty.sprints.three.adapters;

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

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.three.models.Consultation;
import com.android.agzakhanty.sprints.three.models.Measurement;
import com.android.agzakhanty.sprints.three.models.api_responses.MeasureDetailsResponseModel;
import com.android.agzakhanty.sprints.three.views.MyMeasurements;
import com.android.agzakhanty.sprints.three.views.NewMeasurementDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Aly on 27/04/2018.
 */

public class MeasurementsAdapter extends ArrayAdapter<MeasureDetailsResponseModel> {

    ArrayList<MeasureDetailsResponseModel> measurements;
    Context context;
    PrefManager prefManager;


    // View lookup cache
    private static class ViewHolder {
        TextView measureNameTV, measureCountTV;
        RelativeLayout mainLayout;
    }

    public MeasurementsAdapter(ArrayList<MeasureDetailsResponseModel> conslts, Context context) {
        super(context, R.layout.measurement_list_item, conslts);
        this.context = context;
        this.measurements = conslts;
        prefManager = PrefManager.getInstance(this.context);
    }

    @Override
    public int getCount() {
        return measurements.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        // Get the data item for this position
        final MeasureDetailsResponseModel measurement = measurements.get(i);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.measurement_list_item, viewGroup, false);
            viewHolder.measureNameTV = (TextView) view.findViewById(R.id.measureNameTV);
            viewHolder.measureCountTV = (TextView) view.findViewById(R.id.measureCountTV);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.measureLayout);
            result = view;
            view.setTag(viewHolder);
            viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, NewMeasurementDetails.class);
                    intent.putExtra("measure", new Gson().toJson(measurement));
                    intent.putExtra("isView", "y");
                    intent.putExtra("id", measurement.getMsrId());
                    if (PrefManager.getInstance(context).readInt(Constants.APP_LANGUAGE) == 0)
                        intent.putExtra("title", measurement.getMsrName());
                    else
                        intent.putExtra("title", measurement.getMsrNameEn());
                    Log.d("TEST_CHART_ADAPTER", measurement.getMsrId());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    ((Activity) context).overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
                    ((Activity) context).finish();
                }
            });
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }

        if (PrefManager.getInstance(context).readInt(Constants.SP_LANGUAGE_KEY) == 0)
            viewHolder.measureNameTV.setText(measurement.getMsrName());
        else if (PrefManager.getInstance(context).readInt(Constants.SP_LANGUAGE_KEY) == 1)
            viewHolder.measureNameTV.setText(measurement.getMsrNameEn());

        viewHolder.measureCountTV.setText(measurement.getNoMeasures() + " " +
                context.getResources().getString(R.string.measurements));

        return view;
    }
}
