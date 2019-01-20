package com.android.agzakhanty.sprints.one.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.agzakhanty.R;

import com.android.agzakhanty.general.api.ApiClient;
import com.android.agzakhanty.general.api.ApiInterface;
import com.android.agzakhanty.general.application.CommonTasks;
import com.android.agzakhanty.general.application.Constants;
import com.android.agzakhanty.general.application.DialogCreator;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.general.push_notification.MyNotificationOpenedHandler;
import com.android.agzakhanty.general.push_notification.MyNotificationReceivedHandler;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Splash extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        OSSubscriptionObserver {

    @BindView(R.id.buttonsContainerLayout)
    LinearLayout buttonsContainerLayout;
    @BindView(R.id.loader)
    ImageView loader;
    @BindView(R.id.fblogin_button)
    LoginButton fbLoginBtn;
    CallbackManager callbackManager;
    Animation loaderRotation;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.UserScreensThemes);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //MyNotificationOpenedHandler : This will be called when a notification is tapped on.
        //MyNotificationReceivedHandler : This will be called when a notification is received while your app is running.
        PrefManager.getInstance(this).write(0, Constants.SP_LANGUAGE_KEY);
        OneSignal.addSubscriptionObserver(this);
        callbackManager = CallbackManager.Factory.create();
        ButterKnife.bind(this);
        Log.d("TEST_INTER_SPLASH", PrefManager.getInstance(this).readInt(Constants.SP_LANGUAGE_KEY) + "  E");
        dialog = DialogCreator.getInstance(this);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        loaderRotation = AnimationUtils.loadAnimation(this, R.anim.loader_rotation);
        loader.startAnimation(loaderRotation);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        fbLoginBtn.setReadPermissions("email");
        fbLoginBtn.setReadPermissions("public_profile");
        fbLoginBtn.setReadPermissions("user_birthday");
        fbLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());


                                try {
                                    String birthday = object.getString("birthday"); // 01/31/1980 format
                                    // Application code
                                    Customer cstmr = new Customer();
                                    cstmr.setE_Mail(object.getString("email"));
                                    cstmr.setFbId(object.getString("id"));
                                    cstmr.setName(object.getString("name"));
                                    //cstmr.setGender(object.getString("gender"));
                                    cstmr.setDateOfBirth(object.getString("birthday"));
                                    PrefManager.getInstance(Splash.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(cstmr));
                                    dialog.setMessage(getResources().getString(R.string.loginMsg));
                                    dialog.show();
                                    callFBLoginWS(cstmr.getFbId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toasty.normal(Splash.this, "Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toasty.normal(Splash.this, "Error", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!PrefManager.getInstance(Splash.this).read(Constants.SP_LOGIN_CUSTOMER_KEY).isEmpty()) {
                    Intent intent = new Intent(Splash.this, Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                } else {
                    buttonsContainerLayout.setVisibility(View.VISIBLE);
                    loader.clearAnimation();
                    loader.setVisibility(View.INVISIBLE);
                }
            }
        }, 500);
    }

    private void callFBLoginWS(String fbId) {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.loginFB(fbId);
        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                if (response.body().getStatus().equalsIgnoreCase("true")) {
                    PrefManager.getInstance(Splash.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(response.body().getCstmr()));
                    Intent intent = new Intent(Splash.this, Dashboard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                    finish();
                } else {
                    Toast.makeText(Splash.this, getResources().getString(R.string.externalLoginNotFound), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Splash.this, Register.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(Splash.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }

    private void callGmailLoginWS(String gId) {
        Log.d("TEST_NULL", gId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CustomerInfoResponseModel> call = apiService.loginGmail(gId);
        call.enqueue(new Callback<CustomerInfoResponseModel>() {
            @Override
            public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                if (response.body() != null) {
                    if (response.body().getStatus().equalsIgnoreCase("true")) {
                        PrefManager.getInstance(Splash.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(response.body().getCstmr()));
                        Intent intent = new Intent(Splash.this, Dashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                        finish();
                    } else {
                        Toast.makeText(Splash.this, getResources().getString(R.string.externalLoginNotFound), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Splash.this, Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                } else Log.d("TEST_NULL", response.code() + "");
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                Toast.makeText(Splash.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                Log.d("TEST_CERT", t.getMessage());
                dialog.dismiss();
            }
        });
    }

    public void forceCrash() {
        throw new RuntimeException("This is a crash");
    }

    @OnClick(R.id.registerFromLogin)
    public void goToRegister() {
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

    @OnClick(R.id.loginTV)
    public void goToLogin() {
        Intent i = new Intent(this, Login.class);
        startActivity(i);
    }

    @OnClick(R.id.fbLoginButton)
    public void loginWithFB() {
        String cstmrJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_TEMP_CUSTOMER_KEY);
        Type type = new TypeToken<Customer>() {
        }.getType();
        Customer cstmr = new Gson().fromJson(cstmrJSON, type);
        if (cstmr != null && cstmr.getFbId() != null && !cstmr.getFbId().isEmpty())
            callFBLoginWS(cstmr.getFbId());
        else
            fbLoginBtn.performClick();
    }

    @OnClick(R.id.gRegisterButton)
    public void loginWithGPlus() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 1);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("TEST_GOOGLE", result.getStatus().getStatusCode() + "");

            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                // Application code
                Customer cstmr = new Customer();
                Log.d("TEST_NULL", acct.getEmail() + " " + acct.getDisplayName());
                cstmr.setE_Mail(acct.getEmail());
                cstmr.setGmailId(acct.getId());
                cstmr.setName(acct.getDisplayName());
                PrefManager.getInstance(Splash.this).write(Constants.SP_LOGIN_CUSTOMER_KEY, new Gson().toJson(cstmr));
                dialog.setMessage(getResources().getString(R.string.loginMsg));
                dialog.show();
                callGmailLoginWS(cstmr.getGmailId());
            } else Toast.makeText(Splash.this, "ERROR", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Splash.this, "ERRORX", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().getSubscribed() &&
                stateChanges.getTo().getSubscribed()) {
            // The user is subscribed
            // Either the user subscribed for the first time
            // Or the user was subscribed -> unsubscribed -> subscribed
            //stateChanges.getTo().getUserId();
            //Save id to shared preferences
            PrefManager.getInstance(this).write(Constants.REGISTRATION_ID, stateChanges.getTo().getUserId());
            // Make a POST call to your server with the user ID
            sendRegistrationToServer(stateChanges.getTo().getUserId());
        }
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server

        String custJSON = PrefManager.getInstance(this).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        if (customer != null) {
            customer.setRegId(token);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CustomerInfoResponseModel> call = apiService.updateCustomerInfo(customer.getId(), customer);
            call.enqueue(new Callback<CustomerInfoResponseModel>() {
                @Override
                public void onResponse(Call<CustomerInfoResponseModel> call, Response<CustomerInfoResponseModel> response) {
                    if (response.body() != null) {
                        if (response.body().getStatus().equalsIgnoreCase("true")) {
                            Log.d("TEST_TOKEN_UPDATE", response.body().getCstmr().getRegId());
                        } else {
                            Toast.makeText(Splash.this, getResources().getString(R.string.apiStatusFalseMsg), Toast.LENGTH_LONG).show();
                        }
                    } else Log.d("TEST_NULL", response.code() + "");

                }

                @Override
                public void onFailure(Call<CustomerInfoResponseModel> call, Throwable t) {
                    Toast.makeText(Splash.this, getResources().getString(R.string.serverFailureMsg), Toast.LENGTH_LONG).show();
                    Log.d("TEST_CERT", t.getMessage());
                }
            });
        }
    }

}
