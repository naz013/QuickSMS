package com.hexrain.design.quicksms;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hexrain.design.quicksms.helpers.ColorSetter;
import com.hexrain.design.quicksms.helpers.Constants;
import com.hexrain.design.quicksms.helpers.SharedPrefs;

public class RateDialog extends Activity {

    TextView buttonRate, rateLater, rateNever;
    SharedPrefs sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        ColorSetter cs = new ColorSetter(RateDialog.this);
        setTheme(cs.getDialogStyle());
        setContentView(R.layout.rate_dialog_layout);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        sharedPrefs = new SharedPrefs(RateDialog.this);

        buttonRate = (TextView) findViewById(R.id.buttonRate);
        buttonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefs.saveBoolean(Constants.PREFERENCES_APP_RUNS, true);
                launchMarket();
                finish();
            }
        });

        rateLater = (TextView) findViewById(R.id.rateLater);
        rateLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefs.saveBoolean(Constants.PREFERENCES_RATE_SHOWN, false);
                sharedPrefs.saveInt(Constants.PREFERENCES_APP_RUNS, 0);
                finish();
            }
        });

        rateNever = (TextView) findViewById(R.id.rateNever);
        rateNever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPrefs.saveBoolean(Constants.PREFERENCES_RATE_SHOWN, true);
                finish();
            }
        });
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Couldn't launch market", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {


    }
}