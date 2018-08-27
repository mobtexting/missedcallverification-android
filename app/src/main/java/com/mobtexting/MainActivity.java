package com.mobtexting;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mobtexting.missedcallphoneverification.model.ServerResponse;
import com.mobtexting.missedcallphoneverification.receiver.MissedCallReceiver;
import com.mobtexting.missedcallphoneverification.reposotories.MobtextingInfoResult;
import com.mobtexting.missedcallphoneverification.reposotories.VerificationInterface;
import com.mobtexting.missedcallphoneverification.service.MobtextingServices;


public class MainActivity extends AppCompatActivity implements VerificationInterface{

    private Button btn;
    private TextView testTv;
    private ProgressDialog pd;
    private MissedCallReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.btn);
        testTv = (TextView) findViewById(R.id.testTv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testTv.setText("");
                registerReceiver();
                performDial("123");
            }
        });
    }

    /**
     * perform dial to particular number
     * @param numberString
     */
    private void performDial(String numberString) {
        if (!numberString.equals("")) {
            Uri number = Uri.parse("tel:" + numberString);
            Intent dial = new Intent(Intent.ACTION_CALL, number);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(dial);
        }
    }


    @Override
    protected void onDestroy() {
        //recomanded to unregister the receiver
        unRegisterReceiver();
        super.onDestroy();
    }


    /**
     * show progress dialog while verifying the verification request
     */
    private void showProgressBar(){
        pd = new ProgressDialog(MainActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setTitle("Verifying your number");
        pd.setMessage("Wait for few seconds......");
        pd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD4D9D0")));
        pd.setIndeterminate(false);
        pd.setCancelable(false);
        pd.show();
    }

    //server success response from mobtexting
    @Override
    public void onResponse(ServerResponse serverResponse) {
        pd.dismiss();
        testTv.append(serverResponse.getMessage()+"  "+serverResponse.getResponseCode()+" \n");
        if(serverResponse.getResponseCode()==718){

            finish();
            startActivity(new Intent(getBaseContext(),VerifyActivity.class));
        }
    }

    //server response from mobtexting when any error occurs
    @Override
    public void onError(ServerResponse serverResponse) {
        pd.dismiss();
        testTv.append(serverResponse.getMessage()+"  "+serverResponse.getResponseCode()+" \n");
    }

    //dial a number from activity and call back with dialed number
    @Override
    public void missedCallReceived(boolean b, String s) {
        testTv.setText(s);
        if (b) {
            verifyMobile();
        } else {
            unRegisterReceiver();
        }
    }



    /**
     * for mobile verification to send data to mobtexing service for processing
     */
    private void verifyMobile() {
        showProgressBar();
        MobtextingServices.sendDataToService(this, "99999", "123", new MobtextingInfoResult(this));
    }

    /**
     * recomanded to unregister the receiver
     */
    private void unRegisterReceiver() {
        if (receiver != null) {
            Log.d("activity", "broadcast receiver unregistered");
            MainActivity.this.unregisterReceiver(receiver);
            receiver = null;
        } else {
            Log.d("activity", "broadcast receiver not unregistered");
        }
    }

    /**
     * register receiver for outgoing call and phone state
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        receiver = new MissedCallReceiver(this);
        registerReceiver(receiver, intentFilter);
    }
}
