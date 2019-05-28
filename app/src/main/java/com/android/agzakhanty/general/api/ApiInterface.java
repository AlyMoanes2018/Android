package com.android.agzakhanty.general.api;

//

import com.android.agzakhanty.sprints.one.models.Customer;
import com.android.agzakhanty.sprints.one.models.Pharmacy;
import com.android.agzakhanty.sprints.one.models.TagAndUser;
import com.android.agzakhanty.sprints.one.models.api_responses.City;
import com.android.agzakhanty.sprints.one.models.api_responses.CustomerInfoResponseModel;
import com.android.agzakhanty.sprints.one.models.TagsAndStatus;
import com.android.agzakhanty.sprints.one.models.api_responses.Governorate;
import com.android.agzakhanty.sprints.one.models.api_responses.PharmacyDistance;
import com.android.agzakhanty.sprints.one.models.api_responses.TagUserResponseModel;
import com.android.agzakhanty.sprints.three.models.Consultation;
import com.android.agzakhanty.sprints.three.models.Measurement;
import com.android.agzakhanty.sprints.three.models.MeasurementChartDetails;
import com.android.agzakhanty.sprints.three.models.Reminder;
import com.android.agzakhanty.sprints.three.models.api_requests.SaveConsultationRequestModel;
import com.android.agzakhanty.sprints.three.models.api_requests.SaveMeasurementRequestModel;
import com.android.agzakhanty.sprints.three.models.api_requests.SaveViolationtRequestModel;
import com.android.agzakhanty.sprints.three.models.api_requests.SendReminderNotificationRequestModel;
import com.android.agzakhanty.sprints.three.models.api_requests.UpdateMeasurementRequestModel;
import com.android.agzakhanty.sprints.three.models.api_responses.ConsultationTypesResponesModel;
import com.android.agzakhanty.sprints.three.models.api_responses.LastMeasureResponseModel;
import com.android.agzakhanty.sprints.three.models.api_responses.MeasureDetailsResponseModel;
import com.android.agzakhanty.sprints.three.models.api_responses.MeasurementChartDetailsResponseModel;
import com.android.agzakhanty.sprints.three.models.api_responses.SaveOrderResponseModel;
import com.android.agzakhanty.sprints.three.models.api_responses.UpdateMeasureResponseModel;
import com.android.agzakhanty.sprints.three.models.api_responses.ViolationTypesResponesModel;
import com.android.agzakhanty.sprints.two.models.CancelReason;
import com.android.agzakhanty.sprints.two.models.CategoriesResponseModel;
import com.android.agzakhanty.sprints.two.models.Order;
import com.android.agzakhanty.sprints.two.models.UserRatings;
import com.android.agzakhanty.sprints.two.models.api_responses.AdResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.CircleResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.DetailedOrderResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.ItemsResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.OrderResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.PharmacyResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.RateResponseModel;
import com.android.agzakhanty.sprints.two.models.api_responses.UpdatePcyStatusResponseModel;

import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by GAFI on 24/08/2017.
 */

public interface ApiInterface {

    @GET("Login/DirectLogin")
    Call<CustomerInfoResponseModel> login(@Query("Email") String email, @Query("PWD") String password);

    @GET("Login/LoginFB")
    Call<CustomerInfoResponseModel> loginFB(@Query("FBID") String fbId);

    @GET("Login/LoginGmail")
    Call<CustomerInfoResponseModel> loginGmail(@Query("GmailID") String gmailId);

    @POST("Registeration")
    Call<CustomerInfoResponseModel> register(@Body Customer cstmr);

    @PUT("Registeration/UpdateRegisteration")
    Call<CustomerInfoResponseModel> updateCustomerInfo(@Query("Id") String customerId, @Body Customer cstmr);

    @GET("Tags/GetCstmrTags")
    Call<ArrayList<TagsAndStatus>> getAllCustomerTags(@Query("CstmrId") String cstmrID);

    @POST("Tags/SaveCstmrTags")
    Call<TagUserResponseModel> saveCustomerTags(@Query("CstmrId") String cstmrID, @Body ArrayList<TagAndUser> tags);

    @GET("Pharmacy/GetNearestPharmacies")
    Call<ArrayList<PharmacyDistance>> getAllNearbyPharmacies(@Query("CstmrLatitude") String lat,
                                                             @Query("CstmrLongitude") String longitude,
                                                             @Query("CstmrId") String custID,
                                                             @Query("Distance") String distance);

    @GET("Pharmacy/GetNearestPharmacies")
    Call<ArrayList<PharmacyDistance>> getAllNearbyPharmaciesByName(@Query("CstmrLatitude") String lat,
                                                                   @Query("CstmrLongitude") String longitude,
                                                                   @Query("CstmrId") String custID,
                                                                   @Query("TypeDataReturn") String searchType,
                                                                   @Query("SearchText") String searchText);

    @GET("Pharmacy/GetFavPharmacy")
    Call<PharmacyDistance> getCustomerFavouritePharmacy(@Query("PcyId") String pharmacyID,
                                                        @Query("CstmrId") String custID,
                                                        @Query("CstmrLatitude") String lat,
                                                        @Query("CstmrLongitude") String longitude);

    @POST("AddToCircle/AddPcyToCircle")
    Call<CircleResponseModel> addPharmacyToCircle(@Query("CstmrId") String custmrID,
                                                  @Query("PcyId") String pcyID);

    @GET("AddToCircle/GetCirclePharmacy")
    Call<ArrayList<PharmacyDistance>> getallCustomerCirclePharmacies(@Query("CstmrId") String custID,
                                                                     @Query("CstmrLatitude") String lat,
                                                                     @Query("CstmrLongitude") String longitude);

    @GET("Order/GetCstmrOrders")
    Call<ArrayList<Order>> getAllCustomerOrders(@Query("CstmrId") String custID);

    @GET("Order/GetLastOrderActive")
    Call<Order> getLastActiveOrder(@Query("CstmrId") String custID);

    @GET("CirclePcyAdv/GetCirclePcyAdv")
    Call<ArrayList<AdResponseModel>> getCircleAds(@Query("CstmrId") String custID);

    @GET("CirclePcyAdv/GetPcyAdvDetails")
    Call<DetailedOrderResponseModel> getCircleAdDetails(@Query("AdvId") String advId);

    @GET("Order/GetItems")
    Call<ArrayList<ItemsResponseModel>> searchItemsByName(@Query("SearchType") String type, @Query("ItemName") String query);

    @GET("CirclePcyAdv/GetPcyAdvTags")
    Call<ArrayList<CategoriesResponseModel>> getAdvTags(@Query("AdvId") String advID);

    @GET("Order/GetCstmrOrders")
    Call<ArrayList<Reminder>> getAllCustomerReminders(@Query("CstmrId") String custID);

    @GET("CstConsultation/GetCstmrConslutions")
    Call<ArrayList<Consultation>> getAllCustomerConsultations(@Query("CstmrId") String custID);

    @POST("Order/SaveOrder")
    Call<Boolean> saveCustomerOrder(@Body SaveOrderResponseModel model);

    @GET("Order/GetCstmrOrdersDetails")
    Call<DetailedOrderResponseModel> getSingleOrderDetails(@Query("CstmrId") String customerID,
                                                           @Query("OrderID") String orderID);

    @PUT("Order/UpdateCstmrOrder")
    Call<Boolean> updateCustomerOrder(@Query("OrderID") String orderID,
                                      @Query("CsmrtID") String customerID,
                                      @Query("StatusId") String statusID);

    @GET("Order/GetCancelReasons")
    Call<ArrayList<CancelReason>> getOrderCanelReasons();

    @PUT("Order/CancelCstmrOrder")
    Call<Boolean> cancelCustomerOrder(@Query("OrderId") String orderID,
                                      @Query("CancelReasonId") String cancelReasonID,
                                      @Query("CsmrtID") String customerID);


    @GET("Order/ReminderRecievedOrder")
    Call<Boolean> notifyAfter(@Query("RegID") String regId,
                              @Query("Comment") String notificationText,
                              @Query("OrderID") String orderId,
                              @Query("NoCstmrRepeat") String repeatCounter,
                              @Query("AddedMinute ") String minutes

    );

    @GET("Rating/SaveRating")
    Call<RateResponseModel> saveUserRate(@Query("CstmrId") String CstmrId,
                                         @Query("rate") float rate,
                                         @Query("TrnsId") String TrnsId,
                                         @Query("PcyId") String PcyId,
                                         @Query("EventType") String EventType,
                                         @Query("Comment") String Comment);

    @GET("Rating/GetCstmrsPcyAdvRating")
    Call<ArrayList<UserRatings>> loadAdUserRates(@Query("PcyAdvId") String PcyAdvId);

    @GET("CstConsultation/GetConsltionTypes")
    Call<ArrayList<ConsultationTypesResponesModel>> getConsultaionTypes();

    @POST("CstConsultation/SaveCstmrConsltion")
    Call<Boolean> saveConsultation(@Body SaveConsultationRequestModel request);

    @GET("ReportViolation/GetReportTypes")
    Call<ArrayList<ViolationTypesResponesModel>> getViolationTypes();

    @POST("ReportViolation/SaveCstmrViolation")
    Call<Boolean> saveViolation(@Query("CstmrId") String CstmrId,
                                @Body SaveViolationtRequestModel request);

    @GET("CstMeasure/GetAllMeasurements")
    Call<ArrayList<Measurement>> getAllMeasures();

    @POST("CstMeasure/SaveCstmrMeasure")
    Call<Boolean> saveMeasure(@Query("Date") String date,
                              @Body SaveMeasurementRequestModel request);

    @GET("CstMeasure/GetMeasurementDetails")
    Call<ArrayList<MeasureDetailsResponseModel>> getMeasureDetails(@Query("CstmrId") String CstmrId,
                                                                   @Query("PcyId") String PcyId);

    @GET("CstMeasure/GetChartMeasurementDetails")
    Call<MeasurementChartDetailsResponseModel> getMeasureChartDetails(@Query("CstmrId") String CstmrId,
                                                                      @Query("PcyId") String PcyId,
                                                                      @Query("MsrId") String MsrId);

    @GET("CstMeasure/GetLatestMeasure")
    Call<LastMeasureResponseModel> getLastMeasureDetails(@Query("CstmrId") String CstmrId,
                                                         @Query("MsrId") String MsrId,
                                                         @Query("PcyId") String PcyId);

    @PUT("CstMeasure/UpdateCstmrMeasure")
    Call<UpdateMeasureResponseModel> updateMeasure(@Query("Id") String Id,
                                                   @Query("Date") String date,
                                                   @Body UpdateMeasurementRequestModel request);

    @POST("Reminder/CstmrReminder")
    Call<Boolean> sendMeasureNotifications(
            @Query("CstmrId") String CstmrId,
            @Body SendReminderNotificationRequestModel srnrm
    );

    @GET("Login/ForgetPassword")
    Call<String> forgetPassword(@Query("ForgetPassMail") String email);

    @GET("Pharmacy/GetAllGovernorates")
    Call<ArrayList<Governorate>> getAllGovernrates();

    @GET("Pharmacy/GetAllCities")
    Call<ArrayList<City>> getAllGovernrateCities(@Query("GovId") int govId);

    @PUT("AddToCircle/UpdateCirclePcyStatus")
    Call<UpdatePcyStatusResponseModel> updateCirclePcyStatus(@Query("PcyId") String pcyId, @Query("CstmrId") String customerId);

}

