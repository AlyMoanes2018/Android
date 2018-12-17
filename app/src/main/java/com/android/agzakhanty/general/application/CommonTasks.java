package com.android.agzakhanty.general.application;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.agzakhanty.R;
import com.android.agzakhanty.general.models.PrefManager;
import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.views.AddPharmacy;
import com.android.agzakhanty.sprints.one.views.FavouritePharmacy;
import com.android.agzakhanty.sprints.one.views.ProfilePhotoSetter;
import com.android.agzakhanty.sprints.one.views.Splash;
import com.android.agzakhanty.sprints.two.views.Ads;
import com.android.agzakhanty.sprints.two.views.Circles;
import com.android.agzakhanty.sprints.two.views.CirclesFull;
import com.android.agzakhanty.sprints.two.views.Dashboard;
import com.android.agzakhanty.sprints.three.views.Instructions;
import com.android.agzakhanty.sprints.three.views.MyConsultations;
import com.android.agzakhanty.sprints.three.views.MyMeasurements;
import com.android.agzakhanty.sprints.two.views.MyOrders;
import com.android.agzakhanty.sprints.three.views.Reminders;
import com.android.agzakhanty.sprints.three.views.ReportViolation;
import com.android.agzakhanty.sprints.three.views.Settings;
import com.android.agzakhanty.sprints.two.views.SearchPharmacyByName;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.android.agzakhanty.R.string.settings;

/**
 * Created by a.moanes on 18/10/2017.
 */

public class CommonTasks {

    private static Drawer result;

    public static void setUpTextInputLayoutErrors(final TextInputLayout layout, EditText et) {
        et.addTextChangedListener(new

                                          TextWatcher() {
                                              @Override
                                              public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                              }

                                              @Override
                                              public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                  layout.setError(null);
                                              }

                                              @Override
                                              public void afterTextChanged(Editable s) {

                                              }
                                          });
    }

    public static void setUpPasswordETWithCustomFont(Context ctx, EditText et) {
        Typeface face = Typeface.createFromAsset(ctx.getAssets(), CalligraphyConfig.get().getFontPath());
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et.setTypeface(face);
    }

    public static void setUpTranslucentStatusBar(Activity act) {
        act.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }


    public static void addLeftMenu(final Context context, Toolbar toolbar) {

        String custJSON = PrefManager.getInstance(context).read(Constants.SP_LOGIN_CUSTOMER_KEY);
        Customer customer = new Gson().fromJson(custJSON, new TypeToken<Customer>() {
        }.getType());
        Drawable profile = null;
        String profileURL = null;
        if (customer.getProfilePhotoImgUrl() != null && !customer.getProfilePhotoImgUrl().isEmpty()) {
            profileURL = customer.getProfilePhotoImgUrl();
        } else {
            profileURL = Constants.NO_IMG_FOUND_URL;

        }
        //Drawer Menu
        SecondaryDrawerItem item1 = new SecondaryDrawerItem().withIdentifier(1).withName(context.getResources().getString(R.string.nav_orders)).withIcon(R.drawable.ic_add_shopping_cart);
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withIdentifier(2).withName(context.getResources().getString(R.string.nav_offers)).withIcon(R.drawable.ic_local_offer);
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withIdentifier(3).withName(context.getResources().getString(R.string.nav_consultations)).withIcon(R.drawable.ic_phone_in_talk);
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(4).withName(context.getResources().getString(R.string.nav_measurements)).withIcon(R.drawable.ic_phone_in_talk);
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(5).withName(context.getResources().getString(R.string.nav_my_circle)).withIcon(R.drawable.ic_mode_edit);
        SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(6).withName(context.getResources().getString(R.string.nav_reminders)).withIcon(R.drawable.ic_alarm_add);
        SecondaryDrawerItem item7 = new SecondaryDrawerItem().withIdentifier(7).withName(context.getResources().getString(R.string.nav_violation)).withIcon(R.drawable.ic_warning);
        SecondaryDrawerItem item8 = new SecondaryDrawerItem().withIdentifier(8).withName(context.getResources().getString(R.string.nav_instructions)).withIcon(R.drawable.ic_instructions);
        SecondaryDrawerItem item9 = new SecondaryDrawerItem().withIdentifier(9).withName(context.getResources().getString(R.string.nav_settings)).withIcon(R.drawable.ic_settings);
        SecondaryDrawerItem item10 = new SecondaryDrawerItem().withIdentifier(10).withName(context.getResources().getString(R.string.editData_settings)).withIcon(R.drawable.ic_settings);
        SecondaryDrawerItem item11 = new SecondaryDrawerItem().withIdentifier(11).withName(context.getResources().getString(R.string.signOut_settings)).withIcon(R.drawable.ic_settings);
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity((Activity) context)
                .withHeaderBackground(R.drawable.gradient_background)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withName(customer.getName()).withIcon(profileURL))
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        //create the drawer and remember the `Drawer` result object
        result = new DrawerBuilder()
                .withAccountHeader(headerResult)
                .withActivity((Activity) context)
                .withToolbar(toolbar)
                .addDrawerItems(
                        item1, item2, item3, item4, item5, item6, item7, /*item8, item9*/ item10, item11
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        Log.d("TEST_LOGOUT", position + "");
                        Intent i;

                        switch (position) {
                            case 1:
                                i = new Intent(context, MyOrders.class);
                                i.putExtra(Constants.ACTIVITY_STARTED_FROM, "dash");
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                ((Activity) context).finish();
                                break;

                            case 2:
                                i = new Intent(context, Ads.class);
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                ((Activity) context).finish();
                                break;
                            case 3:
                                i = new Intent(context, MyConsultations.class);
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                ((Activity) context).finish();
                                break;
                            case 4:
                                i = new Intent(context, MyMeasurements.class);
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                ((Activity) context).finish();
                                break;

                            case 5:
                                i = new Intent(context, CirclesFull.class);
                                i.putExtra(Constants.ACTIVITY_STARTED_FROM, "dash");
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                ((Activity) context).finish();
                                break;

                            case 6:
                                i = new Intent(context, Reminders.class);
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                ((Activity) context).finish();
                                break;

                            case 7:
                                i = new Intent(context, ReportViolation.class);
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                ((Activity) context).finish();
                                break;

                           /* case 8:
                                i = new Intent(context, Instructions.class);
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                ((Activity) context).finish();
                                break;

                            case 9:
                                i = new Intent(context, Settings.class);
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                PrefManager.getInstance(context).write(Constants.SP_LOGIN_CUSTOMER_KEY, "");
                                ((Activity) context).finish();
                                break;*/


                            case 8:

                                i = new Intent(context, ProfilePhotoSetter.class);
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                ((Activity) context).finish();
                                break;


                            case 9:
                                i = new Intent(context, Splash.class);
                                result.closeDrawer();
                                context.startActivity(i);
                                ((Activity) context).overridePendingTransition(R.anim.activity_enter, R.anim.activity_leave);
                                PrefManager.getInstance(context).write(Constants.SP_LOGIN_CUSTOMER_KEY, "");
                                // we still need to empty everything possible related to the user
                                ((Activity) context).finish();
                                break;
                        }
                        return true;
                    }
                })
                .build();
        result.setSelection(0);
    }

    public static void showImagesSelectionDialog(final Context ctx){
        View view = ((Activity)ctx).getLayoutInflater().inflate(R.layout.fragment_bottom_sheet_dialog_pics, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(ctx);
        dialog.setContentView(view);
        RadioGroup rg = (RadioGroup) view.findViewById(R.id.radioSelect);
        final RadioButton gallery = (RadioButton) view.findViewById(R.id.radioGallery);
        final RadioButton cam = (RadioButton) view.findViewById(R.id.radioCam);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if (cam.isChecked()){
                    dialog.dismiss();
                    if (ctx.checkSelfPermission(Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ((Activity)ctx).requestPermissions(new String[]{Manifest.permission.CAMERA},
                                124);
                    } else {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        ((Activity) ctx).startActivityForResult(cameraIntent, 510);
                    }
                }
                else if (gallery.isChecked()){
                    dialog.dismiss();
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    ((Activity)ctx).startActivityForResult(Intent.createChooser(intent,
                            ctx.getResources().getString(R.string.selectPic2)), 511);
                }
            }
        });
        dialog.show();
    }
}
