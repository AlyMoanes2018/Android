package com.android.agzakhanty.sprints.two.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.one.views.AddPharmacy;
import com.android.agzakhanty.sprints.one.views.CustomerLocationSelector;
import com.android.agzakhanty.sprints.one.views.FavouritePharmacy;
import com.android.agzakhanty.sprints.one.views.ProfilePhotoSetter;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderDetails;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderResponseModel;
import com.android.agzakhanty.sprints.two.adapters.SelectedItemsAdapter;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.models.OrderDetails;
import com.android.agzakhanty.sprints.two.models.api_responses.DetailedOrderResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.PharmacyResponseModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewOrder extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.favPharmData)
    TextView favPharmDataTV;
    @BindView(R.id.editButton)
    Button editFavPharmacyButton;
    @BindView(R.id.callButton)
    ImageView callFavPharmacy;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    ProgressDialog dialog;
    Customer customer;
    String favouritePharmacyID;
    DetailedOrderResponseModel toBeRepeated;
    PharmacyDistance model;


    @OnClick(R.id.callButton)
    public void callPCY() {
        Boolean isPermissionGranted = PrefManager.getInstance(this).read(Constants.SP_CALL_PERMISSION_GRANTED).equals("true");
        Log.d("TEST_CALL", isPermissionGranted + "");
        if (model.getPharmacy().getMobile() != null && !model.getPharmacy().getMobile().isEmpty() && isPermissionGranted) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + model.getPharmacy().getMobile()));
            startActivity(intent);
        } else {
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
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 220: {
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
                break;
            }
            default: {
                PrefManager.getInstance(this).write(Constants.SP_CALL_PERMISSION_GRANTED, false + "");
                break;
            }
        }
    }


    @OnClick(R.id.items)
    public void onOrderItemsClicked() {
        Intent intent = new Intent(NewOrder.this, AddOrderByItemsSelection.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }

    @OnClick(R.id.photo)
    public void onPhotoOrderClicked() {
        Intent intent = new Intent(NewOrder.this, AddOrderByPrescriptionPhoto.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }

    @OnClick(R.id.repeat)
    public void onRepeatOrderClicked() {
        Intent intent = new Intent(NewOrder.this, MyOrders.class);
        intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "newOrder");
        startActivityForResult(intent, 124);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
    }

    @OnClick(R.id.ads)
    public void onAdsClicked() {
        Intent intent = new Intent(NewOrder.this, Ads.class);
        startActivityForResult(intent, 124);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        CommonTasks.setUpTranslucentStatusBar(this);
        CommonTasks.addLeftMenu(this, toolbar);
        dialog = DialogCreator.getInstance(this);
        favouritePharmacyID = "";
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        if (customer.getFavPcy() != null && !customer.getFavPcy().isEmpty()) {
            callFavPharmacy.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            favPharmDataTV.setVisibility(View.GONE);
            editFavPharmacyButton.setVisibility(View.GONE);
            goToWS(customer.getFavPcy());
        } else {
            favPharmDataTV.setText(getResources().getString(R.string.noFavP));
            editFavPharmacyButton.setText(getResources().getString(R.string.addP));
            editFavPharmacyButton.setVisibility(View.VISIBLE);
            callFavPharmacy.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private void goToWS(String id) {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PharmacyDistance> call = apiService.getCustomerFavouritePharmacy(id, customer.getId(), null, null);
        call.enqueue(new Callback<PharmacyDistance>() {
            @Override
            public void onResponse(Call<PharmacyDistance> call, Response<PharmacyDistance> response) {
                if (response.body() != null) {
                    model = response.body();
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        favPharmDataTV.setText(getResources().getString(R.string.yourFavPcy) + "\n" + model.getPharmacy().getName() + "\n" +
                                model.getPharmacy().getAddress());
                        favouritePharmacyID = response.body().getPharmacy().getId();
                        editFavPharmacyButton.setText(getResources().getString(R.string.edit));
                        //callFavPharmacy.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        favPharmDataTV.setVisibility(View.VISIBLE);
                        editFavPharmacyButton.setVisibility(View.VISIBLE);
                    } else {
                        favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                        editFavPharmacyButton.setText(getResources().getString(R.string.addP));
                        editFavPharmacyButton.setVisibility(View.VISIBLE);
                        callFavPharmacy.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(NewOrder.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<PharmacyDistance> call, Throwable t) {
                favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                editFavPharmacyButton.setText(getResources().getString(R.string.addP));
                editFavPharmacyButton.setVisibility(View.VISIBLE);
                callFavPharmacy.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NewOrder.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.editButton)
    public void nextClicked() {
        View view = getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioSelect);
        final RadioButton data = (RadioButton) view.findViewById(R.id.radioData);
        final RadioButton surrounding = (RadioButton) view.findViewById(R.id.radioSurr);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (data.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(NewOrder.this, SearchPharmacyByName.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_NEW_ORDER);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "NO");
                    startActivity(intent);
                    finish();
                } else if (surrounding.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(NewOrder.this, AddPharmacy.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_NEW_ORDER);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "NO");
                    startActivity(intent);
                    finish();
                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 124 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            String orderId = "";
            if (extras != null) {
                Log.d("TEST_RESULT", extras.getString("order") + "  E");
                //submit the selected order here
                orderId = extras.getString("order");

                //getting order details then sending order
                dialog.setMessage(getResources().getString(R.string.loadingOrder));
                dialog.show();
                goToGetOrderDetailsWS(orderId, extras.getString("orderPCYID"));
            } else Log.d("TEST_RESULT", "NULL");
        } else {
            Toast.makeText(NewOrder.this, getResources().getString(R.string.noOrdersForResult), Toast.LENGTH_LONG).show();
        }
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
                        Toast.makeText(NewOrder.this, getResources().getString(R.string.saveOrderSuccess), Toast.LENGTH_LONG).show();
                        //go to my orders after saving order
                        Intent intent = new Intent(NewOrder.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                        finish();
                    } else
                        Toast.makeText(NewOrder.this, getResources().getString(R.string.saveOrderFailure), Toast.LENGTH_LONG).show();
                } else {
                    Log.d("TEST_NULL", response.code() + "");
                    Toast.makeText(NewOrder.this, getResources().getString(R.string.saveOrderFailure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(NewOrder.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("dash"))
                intent = new Intent(NewOrder.this, Dashboard.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("myOrders"))
                intent = new Intent(NewOrder.this, MyOrders.class);
            else
                intent = new Intent(NewOrder.this, Dashboard.class);
        } else intent = new Intent(NewOrder.this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }

    private void goToGetOrderDetailsWS(String orderID, final String pcyId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DetailedOrderResponseModel> call = apiService.getSingleOrderDetails(customer.getId(), orderID);
        Log.d("TEST_414", customer.getId() + " " + orderID);
        call.enqueue(new Callback<DetailedOrderResponseModel>() {
            @Override
            public void onResponse(Call<DetailedOrderResponseModel> call, Response<DetailedOrderResponseModel> response) {

                if (response.body() != null) {
                    toBeRepeated = response.body();
                    dialog.dismiss();
                    Intent intent = new Intent(NewOrder.this, AddOrderByItemsSelection.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("adItems", new Gson().toJson(toBeRepeated.getListItem()));
                    intent.putExtra("adPrice", toBeRepeated.getTotal());
                    intent.putExtra("adPcy", pcyId);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();

                } else {
                    dialog.dismiss();
                    Toast.makeText(NewOrder.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DetailedOrderResponseModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(NewOrder.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }
}
