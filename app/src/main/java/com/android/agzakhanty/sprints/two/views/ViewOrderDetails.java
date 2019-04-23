package com.android.agzakhanty.sprints.two.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
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
import com.android.agzakhanty.sprints.one.views.Login;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderResponseModel;
import com.android.agzakhanty.sprints.two.adapters.SelectedItemsAdapter;
import com.android.agzakhanty.sprints.two.models.CancelReason;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.models.api_responses.DetailedOrderResponseModel;
import com.android.agzakhanty.sprints.two.views.dialogs.CancelReasonsDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ViewOrderDetails extends AppCompatActivity {

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
    @BindView(R.id.itemsList)
    ListView itemsList;
    @BindView(R.id.commentET)
    EditText commentET;
    @BindView(R.id.priceValTV)
    TextView priceValTV;
    @BindView(R.id.pharmacyPickup)
    RadioButton pharmacyPickup;
    @BindView(R.id.homeDelivery)
    RadioButton homeDelivery;
    @BindView(R.id.actionsLayout)
    LinearLayout actionsLayout;
    @BindView(R.id.backLayout)
    LinearLayout backLayout;
    @BindView(R.id.order)
    Button orderBtn;
    Customer customer;
    String orderID;
    ProgressDialog dialog;
    PrefManager prefManager;
    //get the details of the order from the web service
    DetailedOrderResponseModel order;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_details);
        ButterKnife.bind(this);
        dialog = DialogCreator.getInstance(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        prefManager = PrefManager.getInstance(this);
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        pharmacyPickup.setEnabled(false);
        homeDelivery.setEnabled(false);
        orderID = getIntent().getStringExtra("order");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_dehaze_black_24dp));
        CommonTasks.addLeftMenu(this, toolbar);
        if (customer.getFavPcy() != null && !customer.getFavPcy().isEmpty()) {
            callFavPharmacy.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            favPharmDataTV.setVisibility(View.GONE);
            goToFavPCYWS(customer.getFavPcy());
        }
        dialog.setMessage(getResources().getString(R.string.loadingOrder));
        dialog.show();
        goToGetOrderDetailsWS();
    }

    private void goToGetOrderDetailsWS() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DetailedOrderResponseModel> call = apiService.getSingleOrderDetails(customer.getId(), orderID);

        call.enqueue(new Callback<DetailedOrderResponseModel>() {
            @Override
            public void onResponse(Call<DetailedOrderResponseModel> call, Response<DetailedOrderResponseModel> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    order = response.body();
                    Log.d("TEST_414", new Gson().toJson(order));
                    Log.d("TEST_414", customer.getId());
                    // Add a header to the ListView
                    LayoutInflater inflater = getLayoutInflater();
                    ViewGroup header = (ViewGroup) inflater.inflate(R.layout.selected_items_header, null);
                    header.setPadding(0, 0, 0, 0);
                    itemsList.addHeaderView(header);
                    SelectedItemsAdapter adapter = new SelectedItemsAdapter(order.getListItem(), ViewOrderDetails.this, true, order.getStatusId(), false);
                    itemsList.setAdapter(adapter);
                    if (order.getComment() != null) {
                        if (!order.getComment().isEmpty())
                            commentET.setText(order.getComment());
                    }
                    if (order.getTotal() != null) {
                        if (!order.getTotal().isEmpty())
                            priceValTV.setText(order.getTotal() + " " + getResources().getString(R.string.egp));
                    }
                    if (order.getDeliveryType().equalsIgnoreCase("d"))
                        pharmacyPickup.setChecked(true);
                    else if (order.getDeliveryType().equalsIgnoreCase("h"))
                        homeDelivery.setChecked(true);
                    if (order.getStatusId() != null) {
                        if (!order.getStatusId().equals("6") && !order.getStatusId().equals("7")) {
                            actionsLayout.setVisibility(View.VISIBLE);
                            backLayout.setVisibility(View.GONE);
                        } else if (order.getStatusId().equals("5")) {
                            actionsLayout.setVisibility(View.VISIBLE);
                            orderBtn.setVisibility(View.GONE);
                            backLayout.setVisibility(View.GONE);
                        } else {
                            actionsLayout.setVisibility(View.INVISIBLE);
                            backLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        actionsLayout.setVisibility(View.INVISIBLE);
                        backLayout.setVisibility(View.VISIBLE);
                    }
                } else Log.d("TEST_NULL14", response.code() + "");
            }

            @Override
            public void onFailure(Call<DetailedOrderResponseModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
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
                        model.getPharmacy().setRate(model.getRate());
                        model.getPharmacy().setDistance(model.getDistanceResult());
                        favPharmDataTV.setText(getResources().getString(R.string.yourFavPcy) + "\n" + model.getPharmacy().getName() + "\n" +
                                model.getPharmacy().getAddress());
                        callFavPharmacy.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        favPharmDataTV.setVisibility(View.VISIBLE);
                        if (model.getPharmacy().getLogoURL() != null && !model.getPharmacy().getLogoURL().isEmpty()) {
                            Glide
                                    .with(ViewOrderDetails.this)
                                    .load(Constants.BASE_URL + model.getPharmacy().getLogoURL())
                                    .centerCrop()
                                    .into(pharmacyLogo);
                        } else {
                            Glide
                                    .with(ViewOrderDetails.this)
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
                        Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<PharmacyDistance> call, Throwable t) {
                favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                callFavPharmacy.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void goToUpdateOrderWS(final String statusId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Boolean> call = apiService.updateCustomerOrder(orderID, customer.getId(), statusId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();
                if (response.body() != null && response.body()) {
                    if (statusId.equalsIgnoreCase("4"))
                        Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.updateOrderSuccess), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ViewOrderDetails.this, Dashboard.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();

                } else {
                    Log.d("TEST_NULL", response.code() + "");
                    Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.updateOrderFailure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.order)
    public void confirmOrder() {
        //update customer order
        dialog.setMessage(getResources().getString(R.string.updatingOrder));
        dialog.show();
        goToUpdateOrderWS("4");
    }

    @OnClick(R.id.cancel)
    public void cancelOrder() {
        //cancel customer order
        CancelReasonsDialog dialog = new CancelReasonsDialog(ViewOrderDetails.this, order.getOrderId());
        dialog.show();
        dialog.getWindow().setWindowAnimations(R.style.PauseDialogAnimation);
    }

    @OnClick(R.id.back)
    public void backToMyOrders() {
        //back to my orders without no action
        Intent intent = new Intent(ViewOrderDetails.this, MyOrders.class);
        intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "dash");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
        finish();
    }

    public void onCancelReasonSelected(final CancelReason selectedReason) {
        //Cancel order confirmation dialog
        String reasonDesc = prefManager.readInt(Constants.SP_LANGUAGE_KEY) == 0 ? selectedReason.getDescAr() : selectedReason.getDescEn();
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.cancelOrderDialogTitle) + " " + order.getOrderId())
                .setMessage(getResources().getString(R.string.cancelOrderDialogMsg) + " "
                        + reasonDesc + " " + getResources().getString(R.string.areYouSure))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //cancel order web service
                        dialog.setMessage(getResources().getString(R.string.cancelingOrder));
                        goToCancelOrderWS(selectedReason.getId());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    public void goToCancelOrderWS(String cancelReasonID) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Boolean> call = apiService.cancelCustomerOrder(orderID, cancelReasonID, customer.getId());
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body()) {
                        Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.cancelOrderSuccess), Toast.LENGTH_LONG).show();
                        goToUpdateOrderWS("7");
                    } else
                        Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.cancelOrderFailure), Toast.LENGTH_LONG).show();
                } else {
                    Log.d("TEST_NULL", response.code() + "");
                    Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.cancelOrderFailure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(ViewOrderDetails.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ViewOrderDetails.this, MyOrders.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
