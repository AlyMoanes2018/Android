package com.android.agzakhanty.sprints.three.views;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.android.agzakhanty.general.application.LocalDBHelper;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.three.adapters.DosesAdapter;
import com.android.agzakhanty.sprints.three.models.Dose;
import com.android.agzakhanty.sprints.three.models.Reminder;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.two.views.dialogs.AddItemsDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewReminder extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    @BindView(R.id.toolbar)
    Toolbar appBar;
    @BindView(R.id.nameWrapper)
    TextInputLayout nameWrapper;
    @BindView(R.id.nameET)
    EditText nameET;
    @BindView(R.id.typeWrapper)
    TextInputLayout typeWrapper;
    @BindView(R.id.spinner1)
    Spinner typeSpinner;
    @BindView(R.id.addItemTV)
    TextView addItemTV;
    @BindView(R.id.itemNameAndDeleteAction)
    RelativeLayout itemNameAndDeleteAction;
    @BindView(R.id.addedItemNameTV)
    TextView addedItemNameTV;
    @BindView(R.id.deletIcon)
    ImageView deletIcon;
    @BindView(R.id.endDateET)
    EditText endDateET;
    @BindView(R.id.repeatSpinner)
    Spinner repeatSpinner;
    @BindView(R.id.everySpinner)
    Spinner everySpinner;
    @BindView(R.id.listOfGor3at)
    ListView listOfGor3at;

    int spinnerSelectedIndex;
    Calendar c;
    PrefManager prefManager;
    int yearNow, monthNow, dayNow;
    Boolean isMedicineReminder;
    Boolean isTaskReminder;
    ArrayList<ItemsResponseModel> reminderItem;
    ArrayList<Dose> doses;
    ArrayList<String> formattedFinalDates;
    DosesAdapter dosesAdapter;
    int dosesNumber;
    String repeatRate;
    String endDateStr;
    String today;
    Boolean isReminderValid;
    LocalDBHelper db;

    @OnClick(R.id.addItemTV)
    public void addItem() {
        AddItemsDialog dialog = new AddItemsDialog(NewReminder.this, reminderItem, false);
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setWindowAnimations(R.style.PauseDialogAnimation);
    }

    @OnClick(R.id.submit)
    public void submit() {
        if (DataValidator.isStringEmpty(nameET.getText().toString()))
            nameWrapper.setError(getResources().getString(R.string.chooseConsultTTopic));

        if (reminderItem.size() != 1)
            Toast.makeText(NewReminder.this, getResources().getString(R.string.noItems), Toast.LENGTH_LONG).show();

        /*if (spinnerSelectedIndex > 0 && !DataValidator.isStringEmpty(topicET.getText().toString())) {
            goToSendRequestWS();
        }*/
        else {

            for (int i = 0; i < doses.size(); i++) {
                if (doses.get(i).getDayName() == null)
                    doses.get(i).setDayName(getResources().getStringArray(R.array.week_days)[0]);
                if (doses.get(i).getDayOfMonth() == null)
                    doses.get(i).setDayOfMonth("1");
                if (doses.get(i).getHours() == null)
                    doses.get(i).setHours("0");
                if (doses.get(i).getMinutes() == null)
                    doses.get(i).setMinutes("0");
            }


            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy/MM/dd").withLocale(new Locale("ar"));
            DateTime startDate = formatter.parseDateTime(today);
            DateTime endDate = formatter.parseDateTime(endDateStr);

            int days = Days.daysBetween(startDate, endDate).getDays();
            Log.d("TEST_JODA", days + "");
            List<DateTime> dates = new ArrayList<DateTime>(days);  // Set initial capacity to `days`.
            if (repeatRate.equals("d")) {
                for (int i = 0; i < days; i++) {
                    DateTime d = startDate.withFieldAdded(DurationFieldType.days(), i);
                    for (int j = 0; j < doses.size(); j++) {
                        d = d.withHourOfDay(Integer.parseInt(doses.get(j).getHours()));
                        d = d.withMinuteOfHour(Integer.parseInt(doses.get(j).getMinutes()));
                        dates.add(d);
                    }
                }
            } else if (repeatRate.equals("w")) {
                for (int i = 0; i < days; i++) {
                    DateTime d = startDate.withFieldAdded(DurationFieldType.days(), i);

                    for (int j = 0; j < doses.size(); j++) {
                        Log.d("TEST_DATES", "DOSES: " + doses.get(j).getDayName()
                                + " JODA: " + d.dayOfWeek().getAsText(new Locale("ar")));
                        if (doses.get(j).getDayName().equalsIgnoreCase(d.dayOfWeek().getAsText(new Locale("ar")))) {
                            d = d.withHourOfDay(Integer.parseInt(doses.get(j).getHours()))
                                    .withMinuteOfHour(Integer.parseInt(doses.get(j).getMinutes()));
                            Log.d("TEST_TIMES", d.hourOfDay().get() + ":" + d.minuteOfHour().get());
                            dates.add(d);
                        }
                    }

                }
            } else if (repeatRate.equals("m")) {
                for (int i = 0; i < days; i++) {
                    DateTime d = startDate.withFieldAdded(DurationFieldType.days(), i);
                    for (int j = 0; j < doses.size(); j++) {
                        if (Integer.parseInt(doses.get(j).getDayOfMonth()) == d.dayOfMonth().get()) {
                            d = d.withHourOfDay(Integer.parseInt(doses.get(j).getHours()));
                            d = d.withMinuteOfHour(Integer.parseInt(doses.get(j).getMinutes()));
                            dates.add(d);
                        }
                    }

                }
            }
            DateTimeFormatter writeFormatter = DateTimeFormat.forPattern("yyyy/MM/dd HH:mm:ss").withLocale(new Locale("ar"));
            for (int i = 0; i < dates.size(); i++) {
                formattedFinalDates.add(writeFormatter.print(dates.get(i)));
            }

            for (int i = 0; i < formattedFinalDates.size(); i++)
                Log.d("TEST_JODA_HH", formattedFinalDates.get(i));

            //do not forget to clear this list after sending it to the WS

            //Creating reminder record to add in local DB
            Reminder reminder = new Reminder();
            reminder.setName(nameET.getText().toString());
            reminder.setStatus("y");
            reminder.setDate(today);
            long id = db.insertReminder(reminder);
            Log.d("TEST_DB", id + " E");
            if (id > 0) {


                //call ws
                callSaveWS(formattedFinalDates);

            } else
                Toast.makeText(NewReminder.this, getResources().getString(R.string.reminderNotSaved), Toast.LENGTH_LONG).show();
        }

    }

    private void callSaveWS(ArrayList<String> dates) {
        Customer customer;
        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<Boolean> call = apiService.sendMeasureNotifications(dates.toString(), customer.getId(), reminderItem.get(0).getNameAr());

        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.body()) {
                    Toast.makeText(NewReminder.this, getResources().getString(R.string.reminderSaved), Toast.LENGTH_LONG).show();
                    //go to dashboard
                    Intent intent = new Intent(NewReminder.this, Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                } else {
                    Toast.makeText(NewReminder.this, getResources().getString(R.string.reminderNotSaved), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(NewReminder.this, getResources().getString(R.string.reminderNotSaved), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.deletIcon)
    public void removeSelectedItem() {
        reminderItem.clear();
        addItemTV.setVisibility(View.VISIBLE);
        addedItemNameTV.setText("");
        itemNameAndDeleteAction.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);
        db = new LocalDBHelper(this);
        isReminderValid = false;
        ButterKnife.bind(this);
        CommonTasks.setUpTranslucentStatusBar(this);
        reminderItem = new ArrayList<>();
        formattedFinalDates = new ArrayList<>();
        setSupportActionBar(appBar);
        doses = new ArrayList<>();
        dosesNumber = 1;
        repeatRate = "d";
        prefManager = PrefManager.getInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_dehaze_black_24dp));
        CommonTasks.addLeftMenu(this, appBar);
        spinnerSelectedIndex = 0;
        c = Calendar.getInstance();
        yearNow = c.get(Calendar.YEAR);
        monthNow = c.get(Calendar.MONTH);
        dayNow = c.get(Calendar.DAY_OF_MONTH);
        today = yearNow + "/" + (monthNow + 1) + "/" + dayNow;
        endDateStr = yearNow + "/" + (monthNow + 1) + "/" + (dayNow + 1);
        endDateET.setText(endDateStr);

        isMedicineReminder = false;
        isTaskReminder = false;
        doses.add(new Dose(String.valueOf(0)));
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedIndex = i;
                if (spinnerSelectedIndex > 0) {
                    typeWrapper.setError(null);

                }
                if (spinnerSelectedIndex == 0) {
                    Log.d("TEST_SPINNER", "ONE");
                    isMedicineReminder = true;
                    isTaskReminder = false;
                    addItemTV.setVisibility(View.VISIBLE);
                } else if (spinnerSelectedIndex == 1) {
                    Log.d("TEST_SPINNER", "TWO");
                    isMedicineReminder = false;
                    isTaskReminder = true;
                    addItemTV.setVisibility(View.GONE);
                    itemNameAndDeleteAction.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dosesNumber = i + 1;
                updateDosesUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        everySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        repeatRate = "d";
                        break;
                    case 1:
                        repeatRate = "w";
                        break;
                    case 2:
                        repeatRate = "m";
                        break;
                    default:
                        repeatRate = "d";
                        break;
                }
                updateDosesUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                nameWrapper.setError(null);
            }
        });
        endDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        NewReminder.this, NewReminder.this, yearNow, monthNow, dayNow);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() -1000);
                datePickerDialog.show();
            }
        });
    }

    private void updateDosesUI() {
        Log.d("TEST_DOSE", dosesNumber + "");
        doses.clear();
        for (int i = 0; i < dosesNumber; i++)
            doses.add(new Dose(String.valueOf(i)));

        dosesAdapter = new DosesAdapter(doses, this, repeatRate, dosesNumber);
        listOfGor3at.setAdapter(dosesAdapter);
        listOfGor3at.setVisibility(View.VISIBLE);


    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        endDateStr = i + "/" + (i1 + 1) + "/" + i2;
        endDateET.setText(endDateStr);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onDialogDoneButtonClicked(ArrayList<ItemsResponseModel> selectedItemsFromDialog) {
        reminderItem = selectedItemsFromDialog;
        if (reminderItem != null) {
            if (prefManager.readInt(Constants.APP_LANGUAGE) == 0)
                addedItemNameTV.setText(reminderItem.get(0).getNameAr());
            else if (prefManager.readInt(Constants.APP_LANGUAGE) == 1)
                addedItemNameTV.setText(reminderItem.get(0).getNameEn());
            itemNameAndDeleteAction.setVisibility(View.VISIBLE);
        } else {
            itemNameAndDeleteAction.setVisibility(View.GONE);
            Toast.makeText(NewReminder.this, getResources().getString(R.string.noItems), Toast.LENGTH_LONG).show();
        }
        prefManager.write(Constants.ORDER_SELECTED_ITMES, "");
    }

    public void syncDosesArray(ArrayList<Dose> dosesUpdated) {
        doses = dosesUpdated;
        Log.d("TEST_DOSE", new Gson().toJson(doses));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = null;
        if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM) != null) {
            if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("dash"))
                intent = new Intent(NewReminder.this, Dashboard.class);
            else if (getIntent().getStringExtra(Constants.ACTIVITY_STARTED_FROM).equals("reminders"))
                intent = new Intent(NewReminder.this, Reminders.class);
            else
                intent = new Intent(NewReminder.this, Dashboard.class);
        } else intent = new Intent(NewReminder.this, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.activity_leave, R.anim.activity_enter);
        finish();

    }
}
