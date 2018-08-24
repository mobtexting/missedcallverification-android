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




public class MainActivity extends AppCompatActivity{

    private Button btn;
    private TextView testTv;
    private ProgressDialog pd;

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
                performDial("123");
            }
        });
    }

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

}
