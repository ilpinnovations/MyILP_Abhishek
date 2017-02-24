package com.ilp.ilpschedule.service;

import com.ilp.ilpschedule.model.LoginResult;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by 1115394 on 2/15/2017.
 */
public interface RetrofitService {
    @FormUrlEncoded
    @POST("/login.php")
    Call<LoginResult> login(@Field("emp_id")int empId, @Field("emp_email")String email, @Field("emp_batch")String batch);
}