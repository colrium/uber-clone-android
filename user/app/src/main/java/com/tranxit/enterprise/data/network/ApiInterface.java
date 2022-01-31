package com.tranxit.enterprise.data.network;

import com.tranxit.enterprise.data.network.model.AddressResponse;
import com.tranxit.enterprise.data.network.model.Card;
import com.tranxit.enterprise.data.network.model.Coupon;
import com.tranxit.enterprise.data.network.model.DataResponse;
import com.tranxit.enterprise.data.network.model.Datum;
import com.tranxit.enterprise.data.network.model.EstimateFare;
import com.tranxit.enterprise.data.network.model.ForgotResponse;
import com.tranxit.enterprise.data.network.model.Help;
import com.tranxit.enterprise.data.network.model.Message;
import com.tranxit.enterprise.data.network.model.MyOTP;
import com.tranxit.enterprise.data.network.model.Provider;
import com.tranxit.enterprise.data.network.model.RateCardResponse;
import com.tranxit.enterprise.data.network.model.Service;
import com.tranxit.enterprise.data.network.model.SettingsResponse;
import com.tranxit.enterprise.data.network.model.Status;
import com.tranxit.enterprise.data.network.model.Token;
import com.tranxit.enterprise.data.network.model.User;
import com.tranxit.enterprise.data.network.model.Wallet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by santhosh@appoets.com on 02-05-2018.
 */

public interface ApiInterface {

    @GET("nearbysearch/json?key=AIzaSyCRLa4LQZWNQBcjCYcIVYA45i9i8zfClqc&sensor=true&radius=50")
    Observable<Object> doPlaces(@Query(value = "type", encoded = true) String type, @Query(value = "location", encoded = true) String location, @Query(value = "keyword", encoded = true) String key);

    @FormUrlEncoded
    @POST("api/user/signup")
    Observable<Token> register(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/send/otp")
    Observable<MyOTP> sendOtp(@Field("username") Object params);

    @FormUrlEncoded
    @POST("user/check/mobile")
    Observable<Status> verifyMobileAlreadyExits(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("api/user/voice/sms")
    Observable<MyOTP> sendVoiceOtp(@Field("username") Object mobile);

    @FormUrlEncoded
    @POST("api/user/verify/otp")
    Observable<Token> verifyOTP(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/auth/google")
    Observable<Token> loginGoogle(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/auth/facebook")
    Observable<Token> loginFacebook(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/login")
    Observable<Token> login(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/forgot/password")
    Observable<ForgotResponse> forgotPassword(@Field("mobile") String email);

    @FormUrlEncoded
    @POST("api/user/reset/password")
    Observable<Object> resetPassword(@FieldMap HashMap<String, Object> params);

    @GET("api/user/details")
    Observable<User> profile();

    @FormUrlEncoded
    @POST("api/user/logout")
    Observable<Object> logout(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/user/change/password")
    Observable<Object> changePassword(@FieldMap HashMap<String, Object> params);

    @GET("api/user/request/check")
    Observable<DataResponse> checkStatus();

    @GET("api/user/show/providers")
    Observable<List<Provider>> providers(@QueryMap HashMap<String, Object> params);

    @Multipart
    @POST("api/user/update/profile")
    Observable<User> profile(@PartMap() Map<String, RequestBody> partMap, @Part MultipartBody.Part filename);

    @GET("api/user/services")
    Observable<List<Service>> services();

    @GET("api/user/estimated/fare")
    Observable<EstimateFare> estimateFare(@QueryMap Map<String, Object> params);

    @GET("api/user/service/geo_fencing")
    Observable<RateCardResponse> estimateFareService(@QueryMap Map<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/send/request")
    Observable<Object> sendRequest(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/cancel/request")
    Observable<Object> cancelRequest(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/payment")
    Observable<Message> payment(@Field("request_id") Integer params);

    @FormUrlEncoded
    @POST("api/user/rate/provider")
    Observable<Object> rate(@FieldMap HashMap<String, Object> params);

    @GET("api/user/trips")
    Observable<List<Datum>> pastTrip();

    @GET("api/user/upcoming/trips")
    Observable<List<Datum>> upcomingTrip();

    @FormUrlEncoded
    @POST("api/user/cancel/request")
    Observable<Object> dispute(@FieldMap HashMap<String, Object> params);

    @FormUrlEncoded
    @POST("api/user/card")
    Observable<Object> card(@Field("stripe_token") String stripeToken);

    @GET("api/user/card")
    Observable<List<Card>> card();

    @FormUrlEncoded
    @POST("api/user/card/destroy")
    Observable<Object> deleteCard(@Field("card_id") String cardId, @Field("_method") String method);

    @FormUrlEncoded
    @POST("api/user/add/money")
    Observable<Object> addMoney(@FieldMap HashMap<String, Object> params);

    @GET("api/user/help")
    Observable<Help> help();

    @FormUrlEncoded
    @POST("api/user/promocode/add")
    Observable<Object> coupon(@Field("promocode") String promoCode);

    @GET("api/user/promocodes")
    Observable<List<Coupon>> coupon();

    @FormUrlEncoded
    @POST("api/user/location")
    Observable<Object> addAddress(@FieldMap HashMap<String, Object> params);

    @GET("api/user/location")
    Observable<AddressResponse> address();

    @DELETE("api/user/location" + "/" + "{id}")
    Observable<Object> deleteAddress(@Path("id") Integer id);

    @GET("api/user/wallet/passbook")
    Observable<List<Wallet>> wallet();

    @FormUrlEncoded
    @POST("api/user/chat/push")
    Observable<Object> chatPush(@FieldMap HashMap<String, Object> params);

    @GET("/api/user/settings")
    Observable<SettingsResponse> getSettings();
}
