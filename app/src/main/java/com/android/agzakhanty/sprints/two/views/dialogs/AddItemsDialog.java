package com.android.agzakhanty.sprints.two.views.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.three.views.NewReminder;
import com.android.agzakhanty.sprints.two.adapters.HomeAdsAdapter;
import com.android.agzakhanty.sprints.two.adapters.ItemsAdapter;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.android.agzakhanty.sprints.two.views.AddOrderByItemsSelection;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by a.moanes on 05/07/2018.
 */

public class AddItemsDialog extends AlertDialog {

    private TextInputLayout searchLayout;
    private AppCompatEditText searchET;
    private RadioButton ar;
    private RadioButton en;
    private Button search, doneButton;
    private ProgressBar progressBar;
    private TextView noItems;
    private ListView results;
    private Context context;
    private ArrayList<ItemsResponseModel> models;
    private ArrayList<ItemsResponseModel> selectedItems;
    private Boolean isFromNewOrder;


    public AddItemsDialog(@NonNull Context context, ArrayList<ItemsResponseModel> selectedItems, Boolean isFromNewOrder) {
        super(context);
        this.context = context;
        this.selectedItems = selectedItems;
        this.isFromNewOrder = isFromNewOrder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_items);
        searchLayout = (TextInputLayout) findViewById(R.id.searchLayout);
        searchET = (AppCompatEditText) findViewById(R.id.searchET);
        ar = (RadioButton) findViewById(R.id.ar);
        en = (RadioButton) findViewById(R.id.en);
        search = (Button) findViewById(R.id.searchButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        noItems = (TextView) findViewById(R.id.noItems);
        results = (ListView) findViewById(R.id.results);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) textView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    validateInputDataAndSend();
                    // This is a test comment

                    // This is another test comment
                }
                return false;
            }
        });
        /*searchET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (i) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String query = searchET.getText().toString();
                            String searchType = ar.isChecked() ? "ar" : "en";
                            if (query.length() <= 3) {
                                searchLayout.setError(context.getResources().getString(R.string.moreThanThree));
                            } else {
                                //go to the web service
                                goToGetAdsWS(query, searchType);
                            }
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });*/

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateInputDataAndSend();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedItemsJSON = PrefManager.getInstance(context).read(Constants.ORDER_SELECTED_ITMES);
                Log.d("TEST_ITEMS_DIALOG", selectedItemsJSON + "    EE");
                selectedItems = new Gson().fromJson(selectedItemsJSON, new TypeToken<ArrayList<ItemsResponseModel>>() {
                }.getType());
                if (selectedItems != null) {
                    for (int i = 0; i < selectedItems.size(); i++) {
                        selectedItems.get(i).setQty("1");
                    }
                    //send items to activity
                    if (isFromNewOrder)
                        ((AddOrderByItemsSelection) context).onDialogDoneButtonClicked(selectedItems);
                    else
                        ((NewReminder) context).onDialogDoneButtonClicked(selectedItems);
                }

                dismiss();
            }
        });


    }

    private void goToGetAdsWS(String query, final String type) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<ItemsResponseModel>> call = apiService.searchItemsByName(type, query);
        call.enqueue(new Callback<ArrayList<ItemsResponseModel>>() {
            @Override
            public void onResponse(Call<ArrayList<ItemsResponseModel>> call, Response<ArrayList<ItemsResponseModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null && response.isSuccessful()) {
                    models = response.body();
                    Log.d("TEST_BUG_BUG", response.body().size() + " models");
                    ItemsAdapter adapter = new ItemsAdapter(models, selectedItems, context, type, isFromNewOrder);
                    results.setAdapter(adapter);
                    results.setVisibility(View.VISIBLE);

                } else {
                    Log.d("TEST_Full2", response.code() + "");
                    progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(Call<ArrayList<ItemsResponseModel>> call, Throwable t) {
                Toast.makeText(context, context.getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());

            }
        });
    }

    public void validateInputDataAndSend() {
        String query = searchET.getText().toString();
        String searchType = ar.isChecked() ? "ar" : "en";
        if (query.length() < 3) {
            searchLayout.setError(context.getResources().getString(R.string.moreThanThree));
        } else {
            //go to the web service
            goToGetAdsWS(query, searchType);
        }
    }
}
