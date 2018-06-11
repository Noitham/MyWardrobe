package com.soft.morales.mysmartwardrobe.model.persist;

import com.soft.morales.mysmartwardrobe.model.Garment;
import com.soft.morales.mysmartwardrobe.model.Look;
import com.soft.morales.mysmartwardrobe.model.User;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface APIService {

    // Request method and URL specified in the annotation


    // Get list of garments by the given parameters
    @GET("garments/")
    Call<List<Garment>> getGarment(@QueryMap Map<String, String> options);

    // Get list of garments by the given id
    @GET("garments/{id}/")
    Call<Garment> getGarment(@Path("id") Integer id);

    // Get list of users
    @GET("users/")
    Call<List<User>> loginUser();

    // Get list of looks by the given parameters
    @GET("looks/")
    Call<List<Look>> getLooks(@QueryMap Map<String, String> options);

    // Create a new garment by the given parameters
    @Multipart
    @POST("garments/")
    @FormUrlEncoded
    Call<Garment> createGarment(
            @Field("name") String name,
            @Part MultipartBody.Part image, @Part("name") RequestBody nameImage,
            @Field("category") String category,
            @Field("season") String season,
            @Field("price") String price,
            @Field("username") String username,
            @Field("color") String color,
            @Field("size") String size,
            @Field("brand") String brand);

    // Create a new user by the given parameters
    @POST("users/")
    @FormUrlEncoded
    Call<User> createAccount(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("age") String age);

    // Create a new brand by the given parameters
    @POST("brands/")
    @FormUrlEncoded
    Call<Garment> createBrand(
            @Field("name") String name);

    // Create a new look by the given parameters
    @POST("looks/")
    @FormUrlEncoded
    Call<Look> createLook(
            @Field("garment_id") List<Integer> garment_id,
            @Field("username") String username,
            @Field("date") String date);

    // Deletes an existing garment by the given id
    @DELETE("garments/{id}/")
    Call<Garment> deleteGarment(@Path("id") String id);

    // Deletes an existing garment by the given id
    @DELETE("looks/{id}/")
    Call<Look> deleteLook(@Path("id") String id);

    // For uploading picture
    @Multipart
    @POST("garments/")
    Call<ResponseBody> postImage(@Part("name") String name,
                                 @Part MultipartBody.Part image, @Part("name") RequestBody nameImage,
                                 @Part("category") String category,
                                 @Part("season") String season,
                                 @Part("price") String price,
                                 @Part("username") String username,
                                 @Part("color") String color,
                                 @Part("size") String size,
                                 @Part("brand") String brand);

}
