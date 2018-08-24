package com.mobtexting.missedcallphoneverification.reposotories;

import com.mobtexting.missedcallphoneverification.model.ServerResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface MobtextingServiceInterface {
    @FormUrlEncoded
//    @POST(MobtextingConfig.verifyUrl)
    @POST("/api.v1/json/")
    Call<ServerResponse> post(
            @Field("api_key") String api_key,
            @Field("method") String method,
            @Field("missed_call_number") String missed_call_number,
            @Field("caller") String caller
    );
}
