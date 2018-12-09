package com.android.agzakhanty.sprints.two.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.two.models.CancelReason;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.android.agzakhanty.sprints.two.views.dialogs.CancelReasonsDialog;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Aly on 27/04/2018.
 */

public class CancelReasonsAdapter extends ArrayAdapter<CancelReason> {

    ArrayList<CancelReason> reasons;
    Context context;
    PrefManager prefManager;
    CancelReasonsDialog dialog;

    // View lookup cache
    private static class ViewHolder {
        Button reasonRB;
    }

    public CancelReasonsAdapter(ArrayList<CancelReason> models, Context context, CancelReasonsDialog dialog) {
        super(context, R.layout.cancel_reasons_list_item, models);
        this.context = context;
        this.reasons = models;
        this.dialog = dialog;
        prefManager = PrefManager.getInstance(context);
    }

    @Override
    public int getCount() {
        return reasons.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final CancelReason reason = reasons.get(i);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.cancel_reasons_list_item, viewGroup, false);
            viewHolder.reasonRB = (Button) view.findViewById(R.id.cancelReasonRB);
            result = view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }


        if (prefManager.readInt(Constants.SP_LANGUAGE_KEY) == 0)
            viewHolder.reasonRB.setText(reason.getDescAr());
        else if (prefManager.readInt(Constants.SP_LANGUAGE_KEY) == 1)
            viewHolder.reasonRB.setText(reason.getDescEn());

        viewHolder.reasonRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismissCancelReasonDialog(reason);
            }
        });




        return view;
    }

}
