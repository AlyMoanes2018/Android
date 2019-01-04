package com.android.agzakhanty.sprints.two.views;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderDetails;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderResponseModel;
import com.android.agzakhanty.sprints.two.adapters.SelectedItemsAdapter;
import com.android.agzakhanty.sprints.two.models.OrderDetails;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.android.agzakhanty.sprints.two.views.dialogs.AddItemsDialog;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddOrderByItemsSelection extends AppCompatActivity {

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
    @BindView(R.id.addItemTV)
    TextView addItemTV;
    @BindView(R.id.itemsList)
    ListView itemsList;
    @BindView(R.id.noItemsAdded)
    TextView noItemsAdded;
    @BindView(R.id.priceValTV)
    TextView priceValTV;
    @BindView(R.id.commentET)
    EditText commentET;
    @BindView(R.id.pharmacyPickup)
    RadioButton pharmacyPickup;
    @BindView(R.id.homeDelivery)
    RadioButton homeDelivery;
    PharmacyDistance model;
    Customer customer;
    String favouritePharmacyID;
    Float totalOrderPrice;
    ArrayList<ItemsResponseModel> selectedItems;
    ProgressDialog dialog;


    public float calculateTotalOrderPrice() {
        float totalPrice = 0f;

        for (int i = 0; i < selectedItems.size(); i++) {
            float q = Float.parseFloat(selectedItems.get(i).getQty());
            float p = Float.parseFloat(selectedItems.get(i).getPrice());
            Log.d("TEST_PRICE", q + " " + p);
            //Log.d("TEST_PRICE", totalPrice+"");
            totalPrice += (q * p);

        }

        return totalPrice;
    }

    @OnClick(R.id.addItemTV)
    public void onAddItemClicked() {
        Log.d("TEST_ITEMS_ACTIVITY2", selectedItems.size() + "");
        AddItemsDialog dialog = new AddItemsDialog(AddOrderByItemsSelection.this, selectedItems, true);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order_by_items_selection);
        ButterKnife.bind(this);
        selectedItems = new ArrayList<>();
        favouritePharmacyID = "";
        dialog = DialogCreator.getInstance(this);
        // Add a header to the ListView
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.selected_items_header, itemsList, false);
        header.setPadding(0, 0, 0, 0);
        itemsList.addHeaderView(header);
        CommonTasks.setUpTranslucentStatusBar(this);
        setSupportActionBar(toolbar);
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

        if (getIntent().getStringExtra("adItems") != null && !getIntent().getStringExtra("adItems").isEmpty()) {
            ArrayList<ItemsResponseModel> adItems = new Gson().fromJson(getIntent().getStringExtra("adItems"),
                    new TypeToken<ArrayList<ItemsResponseModel>>() {
                    }.getType());
            addItemTV.setVisibility(View.GONE);
            onDialogDoneButtonClicked(adItems);
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
                        favPharmDataTV.setText(getResources().getString(R.string.yourFavPcy) + "\n" + model.getPharmacy().getName() + "\n" +
                                model.getPharmacy().getAddress());
                        callFavPharmacy.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        favPharmDataTV.setVisibility(View.VISIBLE);
                        if (model.getPharmacy().getLogoURL() != null && !model.getPharmacy().getLogoURL().isEmpty()) {
                            Glide
                                    .with(AddOrderByItemsSelection.this)
                                    .load(Constants.BASE_URL + model.getPharmacy().getLogoURL())
                                    .centerCrop()
                                    .into(pharmacyLogo);
                        } else {
                            Glide
                                    .with(AddOrderByItemsSelection.this)
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
                        Toast.makeText(AddOrderByItemsSelection.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<PharmacyDistance> call, Throwable t) {
                favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                callFavPharmacy.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddOrderByItemsSelection.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onDialogDoneButtonClicked(ArrayList<ItemsResponseModel> selectedItemsFromDialog) {
        selectedItems = selectedItemsFromDialog;
        boolean isAd = false;
        if (getIntent().getStringExtra("adItems") != null && !getIntent().getStringExtra("adItems").isEmpty()) {
            totalOrderPrice = Float.parseFloat(getIntent().getStringExtra("adPrice"));
            isAd = true;
        } else totalOrderPrice = calculateTotalOrderPrice();
        priceValTV.setText(String.format("%.2f", totalOrderPrice) + " " + getResources().getString(R.string.egp));
        if (selectedItems.size() > 0) {

            SelectedItemsAdapter adapter = new SelectedItemsAdapter(selectedItems, this, false, "", isAd);
            itemsList.setAdapter(adapter);
            itemsList.setVisibility(View.VISIBLE);
            noItemsAdded.setVisibility(View.GONE);
        } else {
            itemsList.setVisibility(View.INVISIBLE);
            noItemsAdded.setVisibility(View.VISIBLE);
        }

        //Log.d("TEST_ITEMS_ACTIVITY", selectedItems.get(0).getNameAr());
    }

    public void updateTotalPrice() {
        totalOrderPrice = calculateTotalOrderPrice();
        priceValTV.setText(String.format("%.2f", totalOrderPrice) + " " + getResources().getString(R.string.egp));
    }

    @OnClick(R.id.order)
    public void confirmOrder() {
        //create the order object
        SaveOrderResponseModel order = new SaveOrderResponseModel();
        SaveOrderDetails orderDetails = new SaveOrderDetails();
        orderDetails.setCstId(customer.getId());
        if (getIntent().getStringExtra("adItems") != null && !getIntent().getStringExtra("adItems").isEmpty())
            orderDetails.setPcyId(getIntent().getStringExtra("adPcy"));
        else
            orderDetails.setPcyId(favouritePharmacyID);
        if (pharmacyPickup.isChecked())
            orderDetails.setDeliveryType("D");
        else if (homeDelivery.isChecked())
            orderDetails.setDeliveryType("H");
        orderDetails.setLattitude(customer.getLatitude());
        orderDetails.setLongitude(customer.getLongitude());
        orderDetails.setTotal(totalOrderPrice + "");
        orderDetails.setComment(commentET.getText().toString());
        orderDetails.setItemsList(selectedItems);
        order.setRxImage(null);
        if (getIntent().getStringExtra("adItems") != null && !getIntent().getStringExtra("adItems").isEmpty()) {
            orderDetails.setRefType("A");
            orderDetails.setRefId(getIntent().getStringExtra("adId"));
        }
        else {
            orderDetails.setRefType(null);
            orderDetails.setRefId(null);
        }
        ArrayList<SaveOrderDetails> arr = new ArrayList<>();
        arr.add(orderDetails);
        order.setOrderDetails(arr);
        order.setFileName();
        Log.d("TEST_ORDER_SENT", new Gson().toJson(order));

        if (selectedItems.size() > 0) {
            //send customer order
            dialog.setMessage(getResources().getString(R.string.savingOrder));
            dialog.show();
            goToSaveOrderWS(order);
        } else
            Toast.makeText(AddOrderByItemsSelection.this, getResources().getString(R.string.noItemsSelected), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(AddOrderByItemsSelection.this, getResources().getString(R.string.saveOrderSuccess), Toast.LENGTH_LONG).show();
                        //back to my orders after saving order
                        Intent intent = new Intent(AddOrderByItemsSelection.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                        finish();
                    } else {
                        Toast.makeText(AddOrderByItemsSelection.this, getResources().getString(R.string.saveOrderFailure), Toast.LENGTH_LONG).show();
                        Log.d("TEST_NULL", response.code() + "");
                    }
                } else {
                    Log.d("TEST_NULL", response.code() + "");
                    Toast.makeText(AddOrderByItemsSelection.this, getResources().getString(R.string.saveOrderFailure), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(AddOrderByItemsSelection.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.cancel)
    public void cancelOrder() {
        //confirm customer order
        Intent intent = new Intent(AddOrderByItemsSelection.this, NewOrder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();
    }

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

    //OnBack
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddOrderByItemsSelection.this, NewOrder.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
