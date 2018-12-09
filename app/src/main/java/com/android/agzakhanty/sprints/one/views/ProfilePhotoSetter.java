package com.android.agzakhanty.sprints.one.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfilePhotoSetter extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar appBar;
    @BindView(R.id.profile_image)
    CircleImageView imageView;
    ProgressDialog dialog;
    String imgByteArrStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_register);
        ButterKnife.bind(this);
        setSupportActionBar(appBar);
        CommonTasks.setUpTranslucentStatusBar(this);
        dialog = DialogCreator.getInstance(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.skip)
    public void skip() {
        Intent i = new Intent(this, CustomerLocationSelector.class);
        startActivity(i);
    }

    @OnClick(R.id.PreRegisterNextButton)
    public void goToRegisterView() {
        if (!imgByteArrStr.isEmpty()) {
            dialog.setMessage(getResources().getString(R.string.registerMsg));
            dialog.show();
            goToUpdateWS(imgByteArrStr);
        } else
            Toast.makeText(this, getResources().getString(R.string.noImg), Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.selectImgButton)
    public void selectImage() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.selectPic)), 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                imgByteArrStr = Base64.encodeToString(byteArray, Base64.DEFAULT);
                Log.d("TEST_BYTE", imgByteArrStr + "");
                /*byte[] bytes = Base64.decode(imgByteArrStr, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);*/

                // Log.d(TAG, String.valueOf(bitmap));
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void goToUpdateWS(String imgArr) {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        customer.setProfile_Photo(imgArr);
        customer.setFileName();
        customer.setRegId(PrefManager.getInstance(ProfilePhotoSetter.this).read(Constants.REGISTRATION_ID));
        Log.d("TEST_UPDATE", new Gson().toJson(customer));
        Log.d("TEST_UPDATE", customer.getPwd() + " & " + customer.getName() + " & " + customer.getE_Mail() + " & " + customer.getDateOfBirth() + " & " + customer.getMobile() + " & " + customer.getGender() + " & " + customer.getId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.updateCustomerInfo(customer.getId(), customer);
        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        PrefManager.getInstance(ProfilePhotoSetter.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(response.body().getCstmr()));
                        Log.d("TEST_NULL", new Gson().toJson(response.body().getCstmr()));
                        Log.d("TEST_REG", response.body().getCstmr().getRegId() + " E");
                        Intent intent = new Intent(ProfilePhotoSetter.this, CustomerLocationSelector.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ProfilePhotoSetter.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
                dialog.dismiss();

            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(ProfilePhotoSetter.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }

}

