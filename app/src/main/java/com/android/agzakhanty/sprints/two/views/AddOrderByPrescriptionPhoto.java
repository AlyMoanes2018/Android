package com.android.agzakhanty.sprints.two.views;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderDetails;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderResponseModel;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddOrderByPrescriptionPhoto extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.favPharmData)
    TextView favPharmDataTV;
    @BindView(R.id.callButton)
    ImageView callFavPharmacy;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.pharmacyLogo)
    CircleImageView pharmacyLogo;
    @BindView(R.id.twentyFourTV)
    TextView twentyFourTV;
    @BindView(R.id.distanceTV)
    TextView distanceTV;
    @BindView(R.id.deliveryButton)
    ImageView deliveryButton;
    @BindView(R.id.phoneButton)
    ImageView phoneButton;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.imgLayout)
    RelativeLayout imgLayout;
    @BindView(R.id.pharmacyPickup)
    RadioButton pharmacyPickup;
    @BindView(R.id.homeDelivery)
    RadioButton homeDelivery;
    @BindView(R.id.commentET)
    EditText commentET;
    ProgressDialog dialog;
    Customer customer;
    String favouritePharmacyID;
    String imgByteArrStr;
    PharmacyDistance model;


    @OnClick(R.id.imgLayout)
    public void onImgLayoutClicked() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    122);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 123);
        }
    }

    @OnClick(R.id.callButton)
    public void callPCY() {
        Boolean isPermissionGranted = PrefManager.getInstance(this).read(Constants.SP_CALL_PERMISSION_GRANTED).equals("true");
        Log.d("TEST_CALL", isPermissionGranted + "");
        if (model.getPharmacy().getMobile() != null && !model.getPharmacy().getMobile().isEmpty() && isPermissionGranted) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + model.getPharmacy().getMobile()));
            startActivity(intent);
        }else {
            getPhoneCallPermission();
        }
    }

    private void getPhoneCallPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + model.getPharmacy().getMobile()));
            startActivity(intent);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    220);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order_by_prescription_photo);
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(toolbar);
        dialog = DialogCreator.getInstance(this);
        favouritePharmacyID = "";
        imgByteArrStr = "";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_dehaze_black_24dp));
        CommonTasks.addLeftMenu(this, toolbar);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        if (customer.getFavPcy() != null && !customer.getFavPcy().isEmpty()) {
            callFavPharmacy.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            favPharmDataTV.setVisibility(View.GONE);
            goToFavPCYWS(customer.getFavPcy());
        }
    }

    private void goToFavPCYWS(String id) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PharmacyDistance> call = apiService.getCustomerFavouritePharmacy(id, customer.getId(), customer.getLatitude(), customer.getLongitude());
        call.enqueue(new Callback<PharmacyDistance>() {
            @Override
            public void onResponse(Call<PharmacyDistance> call, Response<PharmacyDistance> response) {
                if (response.body() != null) {
                     model = response.body();
                    Log.d("TEST_DIST", model.getDistanceResult() + "  E");
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        favouritePharmacyID = model.getPharmacy().getId();
                        model.getPharmacy().setRate(model.getRate());
                        model.getPharmacy().setDistance(model.getDistanceResult());
                        favPharmDataTV.setText(model.getPharmacy().getName() + ":\n\n" +
                                model.getPharmacy().getAddress());
                        callFavPharmacy.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        favPharmDataTV.setVisibility(View.VISIBLE);
                        if (model.getPharmacy().getLogoURL() != null && !model.getPharmacy().getLogoURL().isEmpty()) {
                            Glide
                                    .with(AddOrderByPrescriptionPhoto.this)
                                    .load(Constants.BASE_URL + model.getPharmacy().getLogoURL())
                                    .centerCrop()
                                    .into(pharmacyLogo);
                        } else {
                            Glide
                                    .with(AddOrderByPrescriptionPhoto.this)
                                    .load(Constants.NO_IMG_FOUND_URL)
                                    .centerCrop()
                                    .into(pharmacyLogo);
                        }
                        if (model.getPharmacy().getAllDay() != null && !model.getPharmacy().getAllDay().isEmpty()) {
                            twentyFourTV.setVisibility(View.VISIBLE);
                        }
                        if (model.getPharmacy().getDistance() != null && !model.getPharmacy().getDistance().isEmpty()) {

                            distanceTV.setText(model.getPharmacy().getDistance());
                            distanceTV.setVisibility(View.VISIBLE);
                        }
                        if (model.getPharmacy().getDelivery() != null && !model.getPharmacy().getDelivery().isEmpty()) {
                            deliveryButton.setVisibility(View.VISIBLE);
                        }
                        if (model.getPharmacy().getChat() != null && !model.getPharmacy().getChat().isEmpty()) {
                            phoneButton.setVisibility(View.VISIBLE);
                        }
                        if (model.getPharmacy().getRate() != null && !model.getPharmacy().getRate().isEmpty()) {
                            ratingBar.setRating(Float.parseFloat(model.getPharmacy().getRate()));
                            ratingBar.setVisibility(View.VISIBLE);
                        }

                    } else {
                        favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                        callFavPharmacy.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddOrderByPrescriptionPhoto.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<PharmacyDistance> call, Throwable t) {
                favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                callFavPharmacy.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddOrderByPrescriptionPhoto.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 122) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 123);
            } else {
                Toast.makeText(this, getResources().getString(R.string.camera), Toast.LENGTH_LONG).show();

            }

        } else if (requestCode == 220){
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PrefManager.getInstance(this).write(Constants.SP_CALL_PERMISSION_GRANTED, true + "");
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + model.getPharmacy().getMobile()));
                    startActivity(intent);
                } else {
                    PrefManager.getInstance(this).write(Constants.SP_CALL_PERMISSION_GRANTED, false + "");
                }

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgLayout.setBackground(new BitmapDrawable(photo));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            imgByteArrStr = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.order)
    public void confirmOrder() {
        //create the order object
        SaveOrderResponseModel order = new SaveOrderResponseModel();
        SaveOrderDetails orderDetails = new SaveOrderDetails();
        orderDetails.setCstId(customer.getId());
        orderDetails.setPcyId(favouritePharmacyID);
        if (pharmacyPickup.isChecked())
            orderDetails.setDeliveryType("D");
        else if (homeDelivery.isChecked())
            orderDetails.setDeliveryType("H");
        orderDetails.setLattitude(customer.getLatitude());
        orderDetails.setLongitude(customer.getLongitude());
        orderDetails.setTotal("0");
        orderDetails.setComment(commentET.getText().toString());
        order.setRxImage(imgByteArrStr);
        ArrayList<SaveOrderDetails> arr = new ArrayList<>();
        arr.add(orderDetails);
        order.setOrderDetails(arr);
        order.setFileName();
        Log.d("TEST_ORDER_SENT", new Gson().toJson(order));

        if (!imgByteArrStr.isEmpty()) {
            //send customer order
            dialog.setMessage(getResources().getString(R.string.savingOrder));
            dialog.show();
            goToSaveOrderWS(order);
        } else
            Toast.makeText(AddOrderByPrescriptionPhoto.this, getResources().getString(R.string.noPhoto), Toast.LENGTH_LONG).show();
    }

    private void goToSaveOrderWS(SaveOrderResponseModel order) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Boolean> call = apiService.saveCustomerOrder(order);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body()) {
                        Toast.makeText(AddOrderByPrescriptionPhoto.this, getResources().getString(R.string.saveOrderSuccess), Toast.LENGTH_LONG).show();
                        //back to my orders after saving order
                        Intent intent = new Intent(AddOrderByPrescriptionPhoto.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                        finish();
                    } else
                        Toast.makeText(AddOrderByPrescriptionPhoto.this, getResources().getString(R.string.saveOrderFailure), Toast.LENGTH_LONG).show();
                } else {
                    Log.d("TEST_NULL", response.code() + "");
                    Toast.makeText(AddOrderByPrescriptionPhoto.this, getResources().getString(R.string.saveOrderFailure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(AddOrderByPrescriptionPhoto.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.cancel)
    public void cancelOrder() {
        //confirm customer order
        Intent intent = new Intent(AddOrderByPrescriptionPhoto.this, NewOrder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddOrderByPrescriptionPhoto.this, NewOrder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
