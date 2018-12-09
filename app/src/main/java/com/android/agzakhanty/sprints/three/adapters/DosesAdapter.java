package com.android.agzakhanty.sprints.three.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Interest;
import com.android.agzakhanty.sprints.three.models.Dose;
import com.android.agzakhanty.sprints.three.models.Reminder;
import com.android.agzakhanty.sprints.three.views.NewReminder;
import com.android.agzakhanty.sprints.two.adapters.ItemsAdapter;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Aly on 04/08/2018.
 */

public class DosesAdapter extends ArrayAdapter<Dose> {

    ArrayList<Dose> doses;
    Context context;
    PrefManager prefManager;
    String repeatRate;
    int dosesNumber;
    HashMap<Integer, String> doseNumberInLetters;


    // View lookup cache
    private static class ViewHolder {
        TextView doseName;
        EditText time, monthDay;
        Spinner dayOfMonth;
    }

    public DosesAdapter(ArrayList<Dose> doses, Context context, String repeatRate, int dosesNumber) {
        super(context, R.layout.dose_list_item, doses);
        this.context = context;
        this.doses = doses;
        this.repeatRate = repeatRate;
        this.dosesNumber = dosesNumber;
        prefManager = PrefManager.getInstance(this.context);
        doseNumberInLetters = new HashMap<>();
        doseNumberInLetters.put(1, context.getResources().getString(R.string.first));
        doseNumberInLetters.put(2, context.getResources().getString(R.string.second));
        doseNumberInLetters.put(3, context.getResources().getString(R.string.third));
        doseNumberInLetters.put(4, context.getResources().getString(R.string.fourth));
        doseNumberInLetters.put(5, context.getResources().getString(R.string.fifth));
        doseNumberInLetters.put(6, context.getResources().getString(R.string.sixth));
    }

    @Override
    public int getCount() {
        return doses.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final Dose dose = doses.get(i);
        final int index = i;
        // Check if an existing view is being reused, otherwise inflate the view
        final DosesAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
        viewHolder = new DosesAdapter.ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.dose_list_item, viewGroup, false);
        viewHolder.doseName = (TextView) view.findViewById(R.id.doseName);
        viewHolder.time = (EditText) view.findViewById(R.id.time);
        viewHolder.monthDay = (EditText) view.findViewById(R.id.monthDay);
        viewHolder.dayOfMonth = (Spinner) view.findViewById(R.id.daySpinner);
        prefManager.write(Constants.REMINDER_SELECTED_INDEX, i + "");

        if (dose.getHours() != null && !dose.getHours().isEmpty() && dose.getMinutes() != null && !dose.getMinutes().isEmpty())
            viewHolder.time.setText(dose.getHours() + ":" + dose.getMinutes());
        else viewHolder.time.setText("00:00");

        if (dose.getDayWeekIndex() >= 0)
            viewHolder.dayOfMonth.setSelection(dose.getDayWeekIndex() - 1);
        else viewHolder.dayOfMonth.setSelection(0);

        if (dose.getDayOfMonth() != null && !dose.getDayOfMonth().isEmpty())
            viewHolder.monthDay.setText(dose.getDayOfMonth());
        else viewHolder.monthDay.setText("1");

        viewHolder.doseName.setText(doseNumberInLetters.get(i + 1));
        if (repeatRate.equalsIgnoreCase("d")) {
            viewHolder.dayOfMonth.setVisibility(View.GONE);
        } else if (repeatRate.equalsIgnoreCase("w")) {
            viewHolder.dayOfMonth.setVisibility(View.VISIBLE);

        } else if (repeatRate.equalsIgnoreCase("m")) {
            viewHolder.dayOfMonth.setVisibility(View.GONE);
            viewHolder.monthDay.setVisibility(View.VISIBLE);
        }

        viewHolder.monthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        prefManager.write(dayOfMonth, Constants.DAY_OF_MONTH);
                        dose.setDayOfMonth(dayOfMonth + "");
                        viewHolder.monthDay.setText(dayOfMonth + "");
                        ((NewReminder) context).syncDosesArray(doses);
                    }
                }, mYear, mMonth, mDay);
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
                //dialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("year", "id", "android")).setVisibility(View.GONE);
                //dialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("month", "id", "android")).setVisibility(View.GONE);
                dialog.show();
            }
        });

        viewHolder.time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                        context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        viewHolder.time.setText(String.format("%02d", i) + ":" + String.format("%02d", i1));
                        prefManager.write(i, Constants.HOURS_OF_DAY);
                        prefManager.write(i1, Constants.MINUTES_OF_HOURS);
                        doses.get(index).setHours(i + "");
                        doses.get(index).setMinutes(i1 + "");
                        ((NewReminder) context).syncDosesArray(doses);
                    }
                }, 0, 0, true);
                timePickerDialog.show();
            }
        });

        viewHolder.dayOfMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                prefManager.write(i, Constants.DAY_OF_WEEK);
                dose.setDayName(context.getResources().getStringArray(R.array.week_days)[i]);
                dose.setDayWeekIndex(i + 1);
                ((NewReminder) context).syncDosesArray(doses);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }


}
