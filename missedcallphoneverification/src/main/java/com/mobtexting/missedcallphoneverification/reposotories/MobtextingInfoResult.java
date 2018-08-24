package com.mobtexting.missedcallphoneverification.reposotories;

import android.os.Handler;

import com.mobtexting.missedcallphoneverification.model.ServerResponse;
import com.mobtexting.missedcallphoneverification.receiver.MissedCallReceiver;
import com.mobtexting.missedcallphoneverification.receiver.MobtextingResultReceiver;

public class MobtextingInfoResult implements MobtextingResultReceiver.ResultReceiverCallBack {
    private VerificationInterface verificationInterface;

    public MobtextingInfoResult(VerificationInterface verificationInterface){
        this.verificationInterface= verificationInterface;
    }
    @Override
    public void onSuccess(ServerResponse data) {
        verificationInterface.onResponse(data);
    }

    @Override
    public void onError(ServerResponse exception) {
        verificationInterface.onError(exception);
    }
}
