package com.obenproto.howtall.api;

import com.obenproto.howtall.response.HowTallApiResponse;
import com.squareup.okhttp.RequestBody;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * HowTallAPIService
 * <p/>
 * Created by Petro Rington on 12/22/2015.
 */
public interface HowTallAPIService {

    /**
     * Recall of user initialization
     * REST endpoint to initialize the user
     */
    @FormUrlEncoded
    @POST("howtall/rest/HowTallService/initUser")
    Call<HowTallApiResponse> initUser(@Field("phoneId") String phoneId);

    /**
     * Recall to save user voice
     * REST endpoint to save user recording and produce estimated age, gender and height
     */
    @Multipart
    @POST("howtall/rest/HowTallService/saveUserVoice")
    Call<HowTallApiResponse> saveUserVoice(@Part("userId") int userId,
                                           @Part("audioFile") RequestBody audioFile);

    /**
     * Recall to save user selfie
     * REST endpoint to save user selfie
     */
    @Multipart
    @POST("howtall/rest/HowTallService/saveUserSelfie")
    Call<HowTallApiResponse> saveUserSelfie(@Part("recordId") int recordId,
                                            @Part("selfieFile") RequestBody selfieFile);

    /**
     * Recall to save user actual height
     * REST endpoint to save user actual height
     */
    @FormUrlEncoded
    @POST("howtall/rest/HowTallService/saveUserHeight")
    Call<HowTallApiResponse> saveUserHeight(@Field("recordId") int recordId,
                                            @Field("actualHeight") int actualHeight);

    /**
     * Recall to save user actual age
     * REST endpoint to save user actual age
     */
    @FormUrlEncoded
    @POST("howtall/rest/HowTallService/saveUserAge")
    Call<HowTallApiResponse> saveUserAge(@Field("recordId") int recordId,
                                         @Field("actualAge") int actualAge);

    /**
     * Recall to save user actual gender
     * REST endpoint to save user actual gender
     */
    @FormUrlEncoded
    @POST("howtall/rest/HowTallService/saveUserGender")
    Call<HowTallApiResponse> saveUserGender(@Field("recordId") int recordId,
                                            @Field("actualGender") int actualGender);

    /**
     * Recall to save user email
     * REST endpoint to save user email
     */
    @FormUrlEncoded
    @POST("howtall/rest/HowTallService/saveUserEmail")
    Call<HowTallApiResponse> saveUserEmail(@Field("recordId") int recordId,
                                           @Field("email") String email);

}