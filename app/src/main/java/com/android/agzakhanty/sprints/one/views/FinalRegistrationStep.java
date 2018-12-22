package com.android.agzakhanty.sprints.one.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FinalRegistrationStep extends AppCompatActivity {

    @BindView(R.id.profile_image)
    CircleImageView profileIV;

    @BindView(R.id.postRegisterUserName)
    TextView customerUserNameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_registration_step);
        CommonTasks.setUpTranslucentStatusBar(this);
        ButterKnife.bind(this);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());

        customerUserNameTV.setText(customer.getName());
        if (customer.getProfilePhotoImgUrl() != null && !customer.getProfilePhotoImgUrl().isEmpty()) {
            Glide
                    .with(FinalRegistrationStep.this)
                    .load(Constants.BASE_URL + customer.getProfilePhotoImgUrl())
                    .centerCrop()
                    .into(profileIV);
        } else {
            Glide
                    .with(FinalRegistrationStep.this)
                    .load(Constants.NO_IMG_FOUND_URL)
                    .centerCrop()
                    .into(profileIV);
        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.NextButton)
    public void onNextClicked() {
        Intent i = new Intent(FinalRegistrationStep.this, Dashboard.class);
        startActivity(i);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }
}
