package com.mobtexting.missedcallphoneverification.receiver;

import android.content.Context;
import android.util.Log;


import com.mobtexting.missedcallphoneverification.reposotories.VerificationInterface;

import java.util.Date;

/**
 * Missedcall receiver for OutgoingCallStarted and OutgoingCallEnd event
 */
public class MissedCallReceiver extends PhonecallReceiver {
    public static String TAG = "MissedcallReceiver";
    private static VerificationInterface missCallListener;

    public MissedCallReceiver(VerificationInterface missCallListener) {
        this.missCallListener = missCallListener;
    }

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {

    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Log.d(TAG, "onOutgoingCallStarted " + number);
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d(TAG, "onIncomingCallEnded " + number);
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        Log.d("broadcast", "onOutgoingCallEnded " + number);
        missCallListener.missedCallReceived(true, number);
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {

    }

}
