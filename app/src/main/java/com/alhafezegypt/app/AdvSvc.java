package com.alhafezegypt.app;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


/**
 * Created by DEV9S on 9/20/14.
 */
public interface AdvSvc {

//dr-monaelmekabaty.com/notification.asmx/sendnotification?Device_ID=sdsdsd&APP_ID=sdsdsdsdsd&APP_Name=sjdhsjdhjskdhs

    @FormUrlEncoded
    @POST("/notification.asmx/sendnotification")
    Call<ResponseBody> addRegisterUser(
            @Field("Device_ID") String action,
            @Field("APP_ID") String registerId,
            @Field("APP_Name") String appName,
            @Field("email") String email,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("appver") String appVer);
}
