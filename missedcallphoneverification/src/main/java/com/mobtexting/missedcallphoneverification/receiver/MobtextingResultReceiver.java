package com.mobtexting.missedcallphoneverification.receiver;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import com.mobtexting.missedcallphoneverification.model.ServerResponse;


/**
 * Mobtexting result receiver
 * @param <T>
 */
public class MobtextingResultReceiver<T> extends ResultReceiver{

    public static final int RESULT_CODE_OK = 1100;
    public static final int RESULT_CODE_ERROR = 666;
    public static final String PARAM_EXCEPTION = "exception";
    public static final String PARAM_RESULT = "result";
    private ResultReceiverCallBack mReceiver;

    public MobtextingResultReceiver(Handler handler) {
        super(handler);
    }
    public void setReceiver(ResultReceiverCallBack<ServerResponse> receiver) {
        mReceiver = receiver;
    }
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (mReceiver != null) {
            if(resultCode == RESULT_CODE_OK){
                mReceiver.onSuccess((ServerResponse) resultData.getSerializable(PARAM_RESULT));
            } else {
                mReceiver.onError((ServerResponse) resultData.getSerializable(PARAM_EXCEPTION));
            }
        }
    }

    /**
     * result receiver callback interface
     * @param <T>
     */
    public interface ResultReceiverCallBack<T>{
        public void onSuccess(ServerResponse data);
        public void onError(ServerResponse exception);
    }
}
