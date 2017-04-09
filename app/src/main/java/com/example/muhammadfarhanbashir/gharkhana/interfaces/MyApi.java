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

    @POST("getCategories")
    Call<JsonObject> getCategories();

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
    @POST("signup")
    Call<JsonObject> signup(@Field("first_name") String first_name,
                             @Field("last_name") String last_name,
                             @Field("contact_number") String contact_number,
                             @Field("email") String email,
                             @Field("password") String password,
                             @Field("latitude") String latitude,
                             @Field("longitude") String longitude,
                             @Field("user_role") String role_id);

    @FormUrlEncoded
    @POST("editUser")
    Call<JsonObject> editUser(@Field("user_id") String user_id,
                            @Field("first_name") String first_name,
                            @Field("last_name") String last_name,
                            @Field("contact_number") String contact_number,
                            @Field("email") String email
                            );

    @FormUrlEncoded
    @POST("getHistory")
    Call<JsonObject> chefHistory(@Field("chef_id") String chef_id);

    @FormUrlEncoded
    @POST("getHistory")
    Call<JsonObject> customerHistory(@Field("customer_id") String customer_id);

    @FormUrlEncoded
    @POST("getItems")
    Call<JsonObject> chefItems(@Field("chef_id") String chef_id);

    @FormUrlEncoded
    @POST("saveItem")
    Call<JsonObject> saveItem(@Field("category_id") String category_id,
                            @Field("chef_id") String chef_id,
                            @Field("item_name") String item_name,
                            @Field("price") String price,
                            @Field("description") String description,
                            @Field("quantity") String quantity);

    @FormUrlEncoded
    @POST("deleteItem")
    Call<JsonObject> deleteItem(@Field("item_id") String item_id);

    @FormUrlEncoded
    @POST("saveOrder")
    Call<JsonObject> saveOrder(@Field("customer_id") String customer_id,
                               @Field("chef_id") String chef_id,
                               @Field("item_id") String item_id,
                               @Field("quantity") String quantity);

    @FormUrlEncoded
    @POST("saveReview")
    Call<JsonObject> saveReview(@Field("chef_id") String chef_id,
                                @Field("customer_id") String customer_id,
                                @Field("review") String review);


}
