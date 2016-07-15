package com.example.nguyennghia.passcodeview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private PassCode pcConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pcConfirm = (PassCode)findViewById(R.id.passcode);
        pcConfirm.setHeaderText("Reenter the passcode");

        //pcConfirm.setStyle(R.style.Passcode);

//        pcConfirm.setCodebarColor(Color.RED);
//        pcConfirm.setNumberTextColor(Color.parseColor("#1abc9c"));
//        pcConfirm.setPressColor(Color.parseColor("#2980b9"));
//        pcConfirm.setHeaderText("Reenter the passcode");
//        pcConfirm.setHeaderTextColor(Color.parseColor("#2c3e50"));

        pcConfirm.setActionCompleted(new PassCode.OnActionCompleted() {
            @Override
            public void onCompleted(String value) {
                Log.i(TAG, "onComplete: " + value);
                if(value.equals("1111")){
                    pcConfirm.setHeaderText("Validate suscess");
                }
            }
        });
    }
}
