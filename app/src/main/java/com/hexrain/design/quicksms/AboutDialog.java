package com.hexrain.design.quicksms;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.hexrain.design.quicksms.helpers.ColorSetter;

public class AboutDialog extends Activity {

    TextView appVersion, appName;
    ColorSetter cs = new ColorSetter(AboutDialog.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(cs.getDialogStyle());
        setContentView(R.layout.about_dialog_layout);

        appName = (TextView) findViewById(R.id.appName);
        String name = getString(R.string.app_name);
        appName.setText(name.toUpperCase());

        appVersion = (TextView) findViewById(R.id.appVersion);
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            appVersion.setText(getString(R.string.string_version) + " " + version + " (" + versionCode + ")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}