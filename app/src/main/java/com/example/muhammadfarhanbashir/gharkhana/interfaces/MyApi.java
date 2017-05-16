package com.example.muhammadfarhanbashir.gharkhana.interfaces;

import com.example.muhammadfarhanbashir.gharkhana.adapters.CategoriesAdapter;
import com.example.muhammadfarhanbashir.gharkhana.models.SignupClass;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesClass;
import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginClass;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import static com.example.muhammadfarhanbashir.gharkhana.R.string.first_name;

/**
 * Created by muhammadfarhanbashir on 14/02/2017.
 */

public interface MyApi {

    @FormUrlEncoded
    @POST("getCategories")
    Call<JsonObject> getCategories(@Field("latitude") String latitude,
                                   @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("getMenu")
    Call<JsonObject> getMenu(@Field("category_id") String category_id,
                             @Field("latitude") String latitude,
                             @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("login")
    Call<JsonObject> login(@Field("email") String email,
                           @Field("password") String password);

    @FormUrlEncoded
    @POST("intimateOrder")
    Call<JsonObject> intimateOrder(@Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("signup")
    Call<JsonObject> signup(@Field("first_name") String first_name,
                             @Field("last_name") String last_name,
                             @Field("contact_number") String contact_number,
                             @Field("email") String email,
                             @Field("password") String password,
                             @Field("latitude") String latitude,
                             @Field("longitude") String longitude,
                             @Field("address") String address,
                             @Field("user_role") String role_id,
                             @Field("device_id") String device_id);

    @FormUrlEncoded
    @POST("editUser")
    Call<JsonObject> editUser(@Field("user_id") String user_id,
                            @Field("first_name") String first_name,
                            @Field("last_name") String last_name,
                            @Field("contact_number") String contact_number,
                            @Field("email") String email,
                            @Field("address") String address,
                            @Field("device_id") String device_id
                            );

    @FormUrlEncoded
    @POST("getHistory")
    Call<JsonObject> chefHistory(@Field("chef_id") String chef_id);

    @FormUrlEncoded
    @POST("getHistory")
    Call<JsonObject> customerHistory(@Field("customer_id") String customer_id);

    @FormUrlEncoded
    @POST("getItems")
    Call<JsonObject> getItems(@Field("chef_id") String chef_id);

    @FormUrlEncoded
    @POST("saveItem")
    Call<JsonObject> saveItem(@Field("category_id") String category_id,
                              @Field("parent_category_id") String parent_category_id,
                            @Field("chef_id") String chef_id,
                            @Field("item_name") String item_name,
                            @Field("price") String price,
                            @Field("description") String description,
                            @Field("serving") String serving,
                            @Field("additional") String additional,
                            @Field("time_taken") String time_taken
                              );


    @FormUrlEncoded
    @POST("deleteItem")
    Call<JsonObject> deleteItem(@Field("item_id") String item_id);

    @FormUrlEncoded
    @POST("saveOrder")
    Call<JsonObject> saveOrder(@Field("customer_id") String customer_id,
                               @Field("chef_id") String chef_id,
                               @Field("item_id") String item_id,
                               @Field("latitude") String latitude,
                               @Field("longitude") String longitude,
                               @Field("delivery_time") String delivery_time);

    @FormUrlEncoded
    @POST("saveReview")
    Call<JsonObject> saveReview(@Field("chef_id") String chef_id,
                                @Field("customer_id") String customer_id,
                                @Field("review") String review);

    @FormUrlEncoded
    @POST("getPendingReviews")
    Call<JsonObject> getPendingReviews(@Field("customer_id") String customer_id);


}
