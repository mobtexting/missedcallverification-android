package com.mobtexting.missedcallphoneverification.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.support.annotation.Nullable;


import com.mobtexting.missedcallphoneverification.config.MobtextingConfig;
import com.mobtexting.missedcallphoneverification.model.ServerResponse;
import com.mobtexting.missedcallphoneverification.receiver.MobtextingResultReceiver;
import com.mobtexting.missedcallphoneverification.reposotories.MobtextingServiceInterface;

import java.lang.annotation.Annotation;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MobtextingServices extends IntentService{
    private Retrofit retrofit;
    private String api_key;
    private int errCode;

    private Handler handler;
    private enum Actions {
        MISSCALLVERIFICATIONS
    }
    private enum PARAM {
        REGMOBILENUMBER,MISSEDCALLNUMBER, RESULT_RECEIVER
    }


    public MobtextingServices(String name) {
        super(name);
    }

    public MobtextingServices() {
        super(MobtextingServices.class.getName());
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        final ResultReceiver resultReceiver = intent.getParcelableExtra(MobtextingServices.PARAM.RESULT_RECEIVER.name());
        if(intent!=null){
            final String action = intent.getAction();
            if (Actions.MISSCALLVERIFICATIONS.name().equals(action)) {
                final String regMobileNumber = intent.getStringExtra(PARAM.REGMOBILENUMBER.name());
                final String missedcallNumber=intent.getStringExtra(PARAM.MISSEDCALLNUMBER.name());

                //wait for 10 seconds
                SystemClock.sleep(10000); // 10 seconds

                hitApi(resultReceiver,missedcallNumber,regMobileNumber);
            }
        }
    }


    /**
     * Verify the status of user mobile number
     * @param resultReceiver
     */
    private void hitApi(final ResultReceiver resultReceiver,String missedcallNumber,String regMobileNumber){
        final Bundle bundle = new Bundle();

        if(missedcallNumber!=null && regMobileNumber!=null) {

            try {
                ApplicationInfo ai = getBaseContext().getPackageManager().getApplicationInfo(getBaseContext().getPackageName(), PackageManager.GET_META_DATA);
                Bundle apikeyBundle = ai.metaData;
                api_key = apikeyBundle.getString("mobtexting.api_key");
            } catch (Exception e) {
                errCode = MobtextingResultReceiver.RESULT_CODE_ERROR;
                bundle.putSerializable(MobtextingResultReceiver.PARAM_EXCEPTION, new ServerResponse("Dear developer. Don't forget to configure <meta-data android:name=\"mobtexting.api_key\" android:value=\"testValue\"/> in your AndroidManifest.xml file.", false, 500));
                if (resultReceiver != null) {
                    resultReceiver.send(errCode, bundle);
                }
                return;
            }

            if (api_key != null && !api_key.equals("")) {
                final int code = MobtextingResultReceiver.RESULT_CODE_OK;

                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(logging);
                retrofit = new Retrofit.Builder()
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(MobtextingConfig.MOBTEXTING_SERVER_BASE_URL)
                        .build();

                MobtextingServiceInterface service = retrofit.create(MobtextingServiceInterface.class);

                Call<ServerResponse> call = service.post(api_key, "missedcall", missedcallNumber, regMobileNumber);

                call.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                bundle.putSerializable(MobtextingResultReceiver.PARAM_RESULT, response.body());
                                if (resultReceiver != null) {
                                    resultReceiver.send(code, bundle);
                                }
                            } else {
                                try {
                                    Converter<ResponseBody, ServerResponse> errorConverter = retrofit.responseBodyConverter(ServerResponse.class, new Annotation[0]);
                                    ServerResponse error = errorConverter.convert(response.errorBody());
                                    bundle.putSerializable(MobtextingResultReceiver.PARAM_RESULT, error);
                                    if (resultReceiver != null) {
                                        resultReceiver.send(code, bundle);
                                    }
                                } catch (Exception e) {
                                    bundle.putSerializable(MobtextingResultReceiver.PARAM_RESULT, new ServerResponse("Check your internet connection!/parsing Json exception", false, 500));
                                    if (resultReceiver != null) {
                                        resultReceiver.send(code, bundle);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            bundle.putSerializable(MobtextingResultReceiver.PARAM_RESULT,
                                    new ServerResponse("Something Went Wrong!", false, 501));
                            if (resultReceiver != null) {
                                resultReceiver.send(code, bundle);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        bundle.putSerializable(MobtextingResultReceiver.PARAM_RESULT, new ServerResponse("Check your internet connection!/parsing Json exception", false, 500));
                        if (resultReceiver != null) {
                            resultReceiver.send(code, bundle);
                        }
                    }
                });
            }else{
                errCode = MobtextingResultReceiver.RESULT_CODE_ERROR;
                bundle.putSerializable(MobtextingResultReceiver.PARAM_RESULT, new ServerResponse("Dear developer. Don't forget to configure <meta-data android:name=\"mobtexting.api_key\" android:value=\"testValue\"/> in your AndroidManifest.xml file.", false, 500));
                if (resultReceiver != null) {
                    resultReceiver.send(errCode, bundle);
                }
            }
        }else{
            final int code = MobtextingResultReceiver.RESULT_CODE_ERROR;
            bundle.putSerializable(MobtextingResultReceiver.PARAM_EXCEPTION, new ServerResponse("provide misscall number and registered mobile number", false, 201));
            if (resultReceiver != null) {
                resultReceiver.send(code, bundle);
            }


        }
    }

    /**
     * receive data from activity to process the verification request
     * @param context
     * @param mobileNumber
     * @param resultReceiverCallBack
     */
    public static void sendDataToService(Context context, String missedcallNumber,String mobileNumber,MobtextingResultReceiver.ResultReceiverCallBack resultReceiverCallBack){

        MobtextingResultReceiver mobtextingResultReceiver = new MobtextingResultReceiver(new Handler(context.getMainLooper()));
        mobtextingResultReceiver.setReceiver(resultReceiverCallBack);

        Intent intent = new Intent(context, MobtextingServices.class);
        intent.setAction(Actions.MISSCALLVERIFICATIONS.name());
        intent.putExtra(PARAM.REGMOBILENUMBER.name(), mobileNumber);
        intent.putExtra(PARAM.MISSEDCALLNUMBER.name(),missedcallNumber);
        intent.putExtra(PARAM.RESULT_RECEIVER.name(), mobtextingResultReceiver);

        context.startService(intent);
    }
}
