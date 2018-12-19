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
import com.android.agzakhanty.sprints.three.views.MyConsultations;
import com.android.agzakhanty.sprints.three.views.NewConsultation;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.two.views.ViewOrderDetails;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Aly on 27/04/2018.
 */

public class ConsultationsAdapter extends ArrayAdapter<Consultation> {

    ArrayList<Consultation> consultations;
    Context context;
    PrefManager prefManager;


    // View lookup cache
    private static class ViewHolder {
        TextView pharmacyName, date, status, type, consultationNum, customerAddress;
        CircleImageView pharmacyLogo;
        RelativeLayout mainLayout;
    }

    public ConsultationsAdapter(ArrayList<Consultation> conslts, Context context) {
        super(context, R.layout.consultation_list_item, conslts);
        this.context = context;
        this.consultations = conslts;
        prefManager = PrefManager.getInstance(this.context);
    }

    @Override
    public int getCount() {
        return consultations.size();
    }


    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        // Get the data item for this position
        final Consultation consultation = consultations.get(i);
        // Check if an existing view is being reused, otherwise inflate the view
        final ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;
        if (view == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.consultation_list_item, viewGroup, false);
            viewHolder.pharmacyName = (TextView) view.findViewById(R.id.nameTV);
            viewHolder.date = (TextView) view.findViewById(R.id.dateTV);
            viewHolder.status = (TextView) view.findViewById(R.id.totalTV2);
            viewHolder.type = (TextView) view.findViewById(R.id.consultationTypeTV);
            viewHolder.consultationNum = (TextView) view.findViewById(R.id.consultationNumTV);
            viewHolder.customerAddress = (TextView) view.findViewById(R.id.addressTV);
            viewHolder.pharmacyLogo = (CircleImageView) view.findViewById(R.id.logo);
            viewHolder.mainLayout = (RelativeLayout) view.findViewById(R.id.mainLayout);
            result = view;

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            result = view;
        }

        viewHolder.pharmacyName.setText(consultation.getPcyName());
        viewHolder.date.setText(context.getResources().getString(R.string.orderDate) + " " +
                consultation.getDate().split("T")[0]);
        if (consultation.getStatus().equalsIgnoreCase("y"))
            viewHolder.status.setText(context.getResources().getString(R.string.respondedd));
        else
            viewHolder.status.setText(context.getResources().getString(R.string.notRespondedd));
        String custJSON = PrefManager.getInstance(context).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        //viewHolder.customerAddress.setText(customer.getA);
        if (consultation.getPcyImage() != null && !consultation.getPcyImage().isEmpty()) {
            byte[] decodedString = Base64.decode(consultation.getPcyImage(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            viewHolder.pharmacyLogo.setImageBitmap(bmp);
        }
        if (PrefManager.getInstance(context).readInt(Constants.SP_LANGUAGE_KEY) == 0)
            viewHolder.type.setText(consultation.getConsltionTypeName());
        else if (PrefManager.getInstance(context).readInt(Constants.SP_LANGUAGE_KEY) == 1)
            viewHolder.type.setText(consultation.getConsltionTypeNameEn());

        viewHolder.consultationNum.setText(context.getResources().getString(R.string.consNumber) + " " + consultation.getConsltionId());
        viewHolder.customerAddress.setText(context.getResources().getString(R.string.addressCon) + " " + consultation.getPcyAddress());
        viewHolder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewConsultation.class);
                intent.putExtra("isView", "true");
                intent.putExtra("cons", new Gson().toJson(consultation));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                ((Activity) context).overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
                ((Activity) context).finish();
            }
        });

        return view;
    }
}
