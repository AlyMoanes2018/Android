package com.android.agzakhanty.sprints.three.views;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DataValidator;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.one.views.AddPharmacy;
import com.android.agzakhanty.sprints.three.models.Consultation;
import com.android.agzakhanty.sprints.three.models.api_requests.SaveConsultationRequestModel;
import com.android.agzakhanty.sprints.three.models.api_responses.ConsultationTypesResponesModel;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.two.views.SearchPharmacyByName;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewConsultation extends AppCompatActivity {

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
    @BindView(R.id.spinner1)
    Spinner consultationTypeSpinner;
    @BindView(R.id.topicET)
    EditText topicET;
    @BindView(R.id.imgLayout)
    RelativeLayout imgLayout;
    @BindView(R.id.topicWrapper)
    TextInputLayout topicWrapper;
    @BindView(R.id.typeWrapper)
    TextInputLayout typeWrapper;
    @BindView(R.id.responseTV)
    TextView responseTV;
    @BindView(R.id.responseWrapper)
    TextInputLayout responseWrapper;
    @BindView(R.id.addPicTV)
    TextView addPicTV;
    @BindView(R.id.sendButton)
    Button sendButton;
    @BindView(R.id.responseET)
    EditText responseET;
    int spinnerSelectedIndex;
    String imgByteArrStr;
    ArrayList<String> typesNames;
    ArrayList<String> typesIds;
    ProgressDialog dialog;
    Customer customer;
    String favoritePcyId = "";
    PharmacyDistance model;
    Consultation consultation;


    @OnClick(R.id.imgLayout)
    public void addImage() {
        CommonTasks.showImagesSelectionDialog(this);
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


    //sendButton
    @OnClick(R.id.sendButton)
    public void sendConsultation() {

        if (spinnerSelectedIndex == -1)
            typeWrapper.setError(getResources().getString(R.string.chooseConsultType));

        if (DataValidator.isStringEmpty(topicET.getText().toString()))
            topicWrapper.setError(getResources().getString(R.string.chooseConsultTTopic));

        if (spinnerSelectedIndex > -1 && !DataValidator.isStringEmpty(topicET.getText().toString())) {
            dialog.setMessage(getResources().getString(R.string.seindingCons));
            dialog.show();
            goToSendRequestWS();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_consultation);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        CommonTasks.setUpTranslucentStatusBar(this);
        CommonTasks.addLeftMenu(this, toolbar);
        dialog = DialogCreator.getInstance(this);
        getConsultaionTypes();
        imgByteArrStr = "";
        typesIds = new ArrayList<>();
        typesNames = new ArrayList<>();
        spinnerSelectedIndex = -1;
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
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

        if (getIntent().getStringExtra("isView") != null && getIntent().getStringExtra("isView").equalsIgnoreCase("true")) {
            consultationTypeSpinner.setEnabled(false);
            topicET.setEnabled(false);
            addPicTV.setVisibility(View.GONE);
            imgLayout.setVisibility(View.GONE);
            responseTV.setVisibility(View.VISIBLE);
            responseWrapper.setVisibility(View.VISIBLE);
            sendButton.setVisibility(View.GONE);
            String consJSON = getIntent().getStringExtra("cons");
            consultation = new Gson().fromJson(consJSON, new TypeToken<Consultation>() {
            }.getType());
            if (consultation.getConsltReply() != null && !consultation.getConsltReply().isEmpty() && !consultation.getConsltReply().equalsIgnoreCase("null"))
                responseET.setText(consultation.getConsltReply());

        } else {
            consultationTypeSpinner.setEnabled(true);
            topicET.setEnabled(true);
            addPicTV.setVisibility(View.VISIBLE);
            imgLayout.setVisibility(View.VISIBLE);
            responseTV.setVisibility(View.GONE);
            responseWrapper.setVisibility(View.GONE);
            sendButton.setVisibility(View.VISIBLE);
        }

        consultationTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedIndex = Integer.parseInt(typesIds.get(i));
                if (spinnerSelectedIndex > -1)
                    typeWrapper.setError(null);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        topicET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                topicWrapper.setError(null);
            }
        });
    }

    private void getConsultaionTypes() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<ConsultationTypesResponesModel>> call = apiService.getConsultaionTypes();
        call.enqueue(new Callback<ArrayList<ConsultationTypesResponesModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ConsultationTypesResponesModel>> call, Response<ArrayList<ConsultationTypesResponesModel>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    typesNames.clear();
                    typesIds.clear();
                    for (int i = 0; i < response.body().size(); i++) {

                        if (PrefManager.getInstance(NewConsultation.this).readInt(Constants.SP_LANGUAGE_KEY) == 0 &&
                                response.body().get(i).getActive().equalsIgnoreCase("y")) {
                            typesNames.add(response.body().get(i).getNameAr());
                            typesIds.add(response.body().get(i).getId());
                        } else if (PrefManager.getInstance(NewConsultation.this).readInt(Constants.SP_LANGUAGE_KEY) == 1 &&
                                response.body().get(i).getActive().equalsIgnoreCase("y")) {
                            typesNames.add(response.body().get(i).getNameEn());
                            typesIds.add(response.body().get(i).getId());
                        } else Log.d("TEST_TYPES", "NOT SAVED");

                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            NewConsultation.this, android.R.layout.simple_spinner_item, typesNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    consultationTypeSpinner.setAdapter(adapter);
                    if (consultation != null) {
                        int position = -1;
                        for (int i = 0; i < typesNames.size(); i++) {
                            Log.d("TEST_CONS", typesNames.get(i) + " " + consultation.getConsltionTypeName());
                            if (typesNames.get(i).equalsIgnoreCase(consultation.getConsltionTypeName())) {
                                position = i;
                                break;
                            }
                        }
                        if (position > -1)
                            consultationTypeSpinner.setSelection(position);
                    }

                } else {
                    Log.d("TEST_TYPES", response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<ConsultationTypesResponesModel>> call, Throwable t) {
                Toast.makeText(NewConsultation.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToWS(String id) {
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PharmacyDistance> call = apiService.getCustomerFavouritePharmacy(id, customer.getId(), null, null);
        call.enqueue(new Callback<PharmacyDistance>() {
            @Override
            public void onResponse(Call<PharmacyDistance> call, Response<PharmacyDistance> response) {
                if (response.body() != null) {
                    model = response.body();
                    favoritePcyId = model.getPharmacy().getId();
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        favPharmDataTV.setText(getResources().getString(R.string.yourFavPcy) + "\n" + model.getPharmacy().getName() + "\n" +
                                model.getPharmacy().getAddress());
                        editFavPharmacyButton.setText(getResources().getString(R.string.edit));
                        callFavPharmacy.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        favPharmDataTV.setVisibility(View.VISIBLE);
                        editFavPharmacyButton.setVisibility(View.VISIBLE);
                    } else {
                        favPharmDataTV.setText(getResources().getString(R.string.noFavP));
                        editFavPharmacyButton.setText(getResources().getString(R.string.addP));
                        editFavPharmacyButton.setVisibility(View.VISIBLE);
                        callFavPharmacy.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(NewConsultation.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
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
                Toast.makeText(NewConsultation.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
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
                    Intent intent = new Intent(NewConsultation.this, SearchPharmacyByName.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_NEW_ORDER);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "cons");
                    startActivity(intent);
                    finish();
                } else if (surrounding.isChecked()) {
                    dialog.dismiss();
                    Intent intent = new Intent(NewConsultation.this, AddPharmacy.class);
                    intent.putExtra("next", Constants.FAVOURITE_PHARMACY_NEXT_NEW_ORDER);
                    intent.putExtra(Constants.ACTIVITY_STARTED_FROM, "cons");
                    startActivity(intent);
                    finish();

                }
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 124) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 510);
            } else {
                Toast.makeText(this, getResources().getString(R.string.camera), Toast.LENGTH_LONG).show();

            }

        } else if (requestCode == 220) {
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
        if (requestCode == 510 && resultCode == RESULT_OK) {

            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgLayout.setBackground(new BitmapDrawable(photo));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            imgByteArrStr = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("TEST_BYTE_CAM", imgByteArrStr + "");
        } else if (requestCode == 511 && resultCode == RESULT_OK && data != null && data.getData() != null) {
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
                imgLayout.setBackground(new BitmapDrawable(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void goToSendRequestWS() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        SaveConsultationRequestModel request = new SaveConsultationRequestModel();
        request.setConsltTypeId(spinnerSelectedIndex + "");
        request.setCstId(customer.getId());
        request.setPcyId(favoritePcyId);
        request.setSubject(topicET.getText().toString());
        request.setFileName();
        if (imgByteArrStr != null && !imgByteArrStr.isEmpty())
            request.setPhoto(imgByteArrStr);
        Call<Boolean> call = apiService.saveConsultation(request);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                dialog.dismiss();
                if (response.body() != null && response.isSuccessful()) {
                    if (response.body()) {
                        Toast.makeText(NewConsultation.this, getResources().getString(R.string.sendingConsSuccess), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(NewConsultation.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
                        finish();
                    } else {

                        Toast.makeText(NewConsultation.this, getResources().getString(R.string.sendingConsFailure), Toast.LENGTH_LONG).show();
                    }
                } else Log.d("TEST_NULL", response.code() + "");
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {

                Toast.makeText(NewConsultation.this, getResources().getString(R.string.sendingConsFailure), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NewConsultation.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
