package com.hexrain.design.quicksms;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hexrain.design.quicksms.helpers.ColorSetter;
import com.hexrain.design.quicksms.helpers.Constants;
import com.hexrain.design.quicksms.helpers.SharedPrefs;


public class SettingsActivity extends ActionBarActivity {

    TextView versionName, thanks;
    RelativeLayout enable, dark;
    CheckBox enableCheck, darkCheck;
    Toolbar toolbar;
    RadioButton red_checkbox, violet_checkbox, green_checkbox, light_green_checkbox, blue_checkbox, light_blue_checkbox,
            yellow_checkbox, orange_checkbox, grey_checkbox, pink_checkbox, sand_checkbox, brown_checkbox,
            deepPurple, indigoCheckbox, limeCheckbox, deepOrange;
    RadioGroup themeGroup, themeGroup2, themeGroup3, themeGroupPro;

    ColorSetter cSetter = new ColorSetter(SettingsActivity.this);
    SharedPrefs prefs = new SharedPrefs(SettingsActivity.this);

    boolean runned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(cSetter.getStyle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cSetter.colorStatus());
        }
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setTitle(R.string.title_activity_settings);
        findViewById(R.id.background).setBackgroundColor(cSetter.getBackgroundStyle());

        versionName = (TextView) findViewById(R.id.versionName);
        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int versionCode = pInfo.versionCode;
            versionName.setText(getString(R.string.string_version) + " " + version + " (" + versionCode + ")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        enable = (RelativeLayout) findViewById(R.id.enable);
        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableChange();
            }
        });

        enableCheck = (CheckBox) findViewById(R.id.enableCheck);
        enableCheck.setChecked(prefs.loadBoolean(Constants.PREFERENCES_QUICK_SMS));

        dark = (RelativeLayout) findViewById(R.id.dark);
        dark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                darkChange();
            }
        });

        darkCheck = (CheckBox) findViewById(R.id.darkCheck);
        darkCheck.setChecked(prefs.loadBoolean(Constants.PREFERENCES_USE_DARK_THEME));

        red_checkbox = (RadioButton) findViewById(R.id.red_checkbox);
        violet_checkbox = (RadioButton) findViewById(R.id.violet_checkbox);
        green_checkbox = (RadioButton) findViewById(R.id.green_checkbox);
        light_green_checkbox = (RadioButton) findViewById(R.id.light_green_checkbox);
        blue_checkbox = (RadioButton) findViewById(R.id.blue_checkbox);
        light_blue_checkbox = (RadioButton) findViewById(R.id.light_blue_checkbox);
        yellow_checkbox = (RadioButton) findViewById(R.id.yellow_checkbox);
        orange_checkbox = (RadioButton) findViewById(R.id.orange_checkbox);
        grey_checkbox = (RadioButton) findViewById(R.id.grey_checkbox);
        pink_checkbox = (RadioButton) findViewById(R.id.pink_checkbox);
        sand_checkbox = (RadioButton) findViewById(R.id.sand_checkbox);
        brown_checkbox = (RadioButton) findViewById(R.id.brown_checkbox);

        deepPurple = (RadioButton) findViewById(R.id.deepPurple);
        indigoCheckbox = (RadioButton) findViewById(R.id.indigoCheckbox);
        limeCheckbox = (RadioButton) findViewById(R.id.limeCheckbox);
        deepOrange = (RadioButton) findViewById(R.id.deepOrange);

        themeGroup = (RadioGroup) findViewById(R.id.themeGroup);
        themeGroup2 = (RadioGroup) findViewById(R.id.themeGroup2);
        themeGroup3 = (RadioGroup) findViewById(R.id.themeGroup3);
        themeGroupPro = (RadioGroup) findViewById(R.id.themeGroupPro);

        themeGroup.clearCheck();
        themeGroup2.clearCheck();
        themeGroup3.clearCheck();
        themeGroupPro.clearCheck();
        themeGroup.setOnCheckedChangeListener(listener1);
        themeGroup2.setOnCheckedChangeListener(listener2);
        themeGroup3.setOnCheckedChangeListener(listener3);
        themeGroupPro.setOnCheckedChangeListener(listener4);

        thanks = (TextView) findViewById(R.id.thanks);
        thanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,
                        ThanksDialog.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        setUpRadio();
    }

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                themeGroup2.setOnCheckedChangeListener(null);
                themeGroup3.setOnCheckedChangeListener(null);
                themeGroupPro.setOnCheckedChangeListener(null);
                themeGroup2.clearCheck();
                themeGroup3.clearCheck();
                themeGroupPro.clearCheck();
                themeGroup2.setOnCheckedChangeListener(listener2);
                themeGroup3.setOnCheckedChangeListener(listener3);
                themeGroupPro.setOnCheckedChangeListener(listener4);
                themeColorSwitch(group.getCheckedRadioButtonId());
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                themeGroup.setOnCheckedChangeListener(null);
                themeGroup3.setOnCheckedChangeListener(null);
                themeGroupPro.setOnCheckedChangeListener(null);
                themeGroup.clearCheck();
                themeGroup3.clearCheck();
                themeGroupPro.clearCheck();
                themeGroup.setOnCheckedChangeListener(listener1);
                themeGroup3.setOnCheckedChangeListener(listener3);
                themeGroupPro.setOnCheckedChangeListener(listener4);
                themeColorSwitch(group.getCheckedRadioButtonId());
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener listener3 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                themeGroup.setOnCheckedChangeListener(null);
                themeGroup2.setOnCheckedChangeListener(null);
                themeGroupPro.setOnCheckedChangeListener(null);
                themeGroup.clearCheck();
                themeGroup2.clearCheck();
                themeGroupPro.clearCheck();
                themeGroup.setOnCheckedChangeListener(listener1);
                themeGroup2.setOnCheckedChangeListener(listener2);
                themeGroupPro.setOnCheckedChangeListener(listener4);
                themeColorSwitch(group.getCheckedRadioButtonId());
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener listener4 = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                themeGroup.setOnCheckedChangeListener(null);
                themeGroup2.setOnCheckedChangeListener(null);
                themeGroup3.setOnCheckedChangeListener(null);
                themeGroup.clearCheck();
                themeGroup2.clearCheck();
                themeGroup3.clearCheck();
                themeGroup.setOnCheckedChangeListener(listener1);
                themeGroup2.setOnCheckedChangeListener(listener2);
                themeGroup3.setOnCheckedChangeListener(listener3);
                themeColorSwitch(group.getCheckedRadioButtonId());
            }
        }
    };

    public void setUpRadio(){
        String loaded = prefs.loadPrefs(Constants.PREFERENCES_THEME);
        switch (loaded) {
            case "1":
                red_checkbox.setChecked(true);
                break;
            case "2":
                violet_checkbox.setChecked(true);
                break;
            case "3":
                light_green_checkbox.setChecked(true);
                break;
            case "4":
                green_checkbox.setChecked(true);
                break;
            case "5":
                light_blue_checkbox.setChecked(true);
                break;
            case "6":
                blue_checkbox.setChecked(true);
                break;
            case "7":
                yellow_checkbox.setChecked(true);
                break;
            case "8":
                orange_checkbox.setChecked(true);
                break;
            case "9":
                grey_checkbox.setChecked(true);
                break;
            case "10":
                pink_checkbox.setChecked(true);
                break;
            case "11":
                sand_checkbox.setChecked(true);
                break;
            case "12":
                brown_checkbox.setChecked(true);
                break;
            case "13":
                deepPurple.setChecked(true);
                break;
            case "14":
                deepOrange.setChecked(true);
                break;
            case "15":
                limeCheckbox.setChecked(true);
                break;
            case "16":
                indigoCheckbox.setChecked(true);
                break;
            default:
                green_checkbox.setChecked(true);
                break;
        }

        runned = true;
    }

    private void themeColorSwitch(int radio){
        switch (radio){
            case R.id.red_checkbox:
                saveColor("1");
                break;
            case R.id.violet_checkbox:
                saveColor("2");
                break;
            case R.id.green_checkbox:
                saveColor("4");
                break;
            case R.id.light_green_checkbox:
                saveColor("3");
                break;
            case R.id.light_blue_checkbox:
                saveColor("5");
                break;
            case R.id.blue_checkbox:
                saveColor("6");
                break;
            case R.id.yellow_checkbox:
                saveColor("7");
                break;
            case R.id.orange_checkbox:
                saveColor("8");
                break;
            case R.id.grey_checkbox:
                saveColor("9");
                break;
            case R.id.pink_checkbox:
                saveColor("10");
                break;
            case R.id.sand_checkbox:
                saveColor("11");
                break;
            case R.id.brown_checkbox:
                saveColor("12");
                break;
            case R.id.deepPurple:
                saveColor("13");
                break;
            case R.id.deepOrange:
                saveColor("14");
                break;
            case R.id.limeCheckbox:
                saveColor("15");
                break;
            case R.id.indigoCheckbox:
                saveColor("16");
                break;
        }
    }

    void saveColor(String string) {
        prefs = new SharedPrefs(SettingsActivity.this);
        prefs.savePrefs(Constants.PREFERENCES_THEME, string);
        if (runned) recreate();
    }

    private void enableChange (){
        if (enableCheck.isChecked()){
            prefs.saveBoolean(Constants.PREFERENCES_QUICK_SMS, false);
            enableCheck.setChecked(false);
        } else {
            prefs.saveBoolean(Constants.PREFERENCES_QUICK_SMS, true);
            enableCheck.setChecked(true);
        }
    }

    private void darkChange (){
        if (darkCheck.isChecked()){
            prefs.saveBoolean(Constants.PREFERENCES_USE_DARK_THEME, false);
            darkCheck.setChecked(false);
        } else {
            prefs.saveBoolean(Constants.PREFERENCES_USE_DARK_THEME, true);
            darkCheck.setChecked(true);
        }
        recreate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
