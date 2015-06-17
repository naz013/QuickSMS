package com.hexrain.design.quicksms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hexrain.design.quicksms.helpers.ColorSetter;
import com.hexrain.design.quicksms.helpers.Constants;
import com.hexrain.design.quicksms.helpers.Contacts;
import com.hexrain.design.quicksms.helpers.Crypter;
import com.hexrain.design.quicksms.helpers.Database;
import com.hexrain.design.quicksms.helpers.QuickAdapter;
import com.hexrain.design.quicksms.helpers.SharedPrefs;

public class QuickSMS extends Activity {

    Database DB;
    Typeface typeface;
    TextView contactInfo, buttonSend, characters;
    ListView messagesList;
    EditText textField;
    RadioGroup radioGroup;
    RadioButton text, template;
    LinearLayout customContainer;

    SharedPrefs sPrefs;
    Contacts contacts;
    String number;
    ColorSetter cs = new ColorSetter(QuickSMS.this);
    BroadcastReceiver deliveredReceiver, sentReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(cs.getFullscreenStyle());
        sPrefs = new SharedPrefs(QuickSMS.this);
        runOnUiThread(new Runnable() {
            public void run() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
        });

        setContentView(R.layout.quick_message_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cs.colorStatus());
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        text = (RadioButton) findViewById(R.id.text);
        template = (RadioButton) findViewById(R.id.template);
        textField = (EditText) findViewById(R.id.textField);
        customContainer = (LinearLayout) findViewById(R.id.customContainer);
        characters = (TextView) findViewById(R.id.characters);

        typeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        messagesList = (ListView) findViewById(R.id.messagesList);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.template){
                    customContainer.setVisibility(View.GONE);
                    messagesList.setVisibility(View.VISIBLE);
                }

                if (checkedId == R.id.text){
                    messagesList.setVisibility(View.GONE);
                    customContainer.setVisibility(View.VISIBLE);
                }
            }
        });
        template.setChecked(true);

        Intent i = getIntent();
        number = i.getStringExtra(Constants.ITEM_ID_INTENT);

        buttonSend = (TextView) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DB = new Database(QuickSMS.this);
                DB.open();
                Cursor x = DB.queryTemplates();
                if (x == null || x.getCount() < 0) {
                    text.setChecked(true);
                    Toast.makeText(QuickSMS.this, getString(R.string.empty_list_warming),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (template.isChecked()) {
                    int position = messagesList.getCheckedItemPosition();
                    long id = messagesList.getAdapter().getItemId(position);

                    Cursor c = DB.getTemplate(id);
                    if (c != null && c.moveToFirst()) {
                        String message = new Crypter().decrypt(c.getString(c.getColumnIndex(Constants.COLUMN_TEXT)));
                        sendSMS(number, message);
                    }
                    if (c != null) c.close();
                }
                if (text.isChecked()){
                    String message = textField.getText().toString().trim();
                    if (message.matches("")) return;

                    sendSMS(number, message);
                }
            }
        });
        buttonSend.setTypeface(typeface);

        DB = new Database(QuickSMS.this);
        sPrefs = new SharedPrefs(QuickSMS.this);
        contacts = new Contacts(QuickSMS.this);

        DB.open();
        String name = contacts.getContactNameFromNumber(number, QuickSMS.this);

        contactInfo = (TextView) findViewById(R.id.contactInfo);
        contactInfo.setTypeface(typeface);
        contactInfo.setText(name + "\n" + number);

        loadTemplates();
        if (messagesList.getAdapter().getCount() > 0) {
            messagesList.setItemChecked(0, true);
        }
    }

    private void loadTemplates(){
        DB.open();
        QuickAdapter simpleCursorAdapter = new QuickAdapter(
                QuickSMS.this,
                DB.queryTemplates());
        messagesList.setAdapter(simpleCursorAdapter);
        Cursor c = DB.queryTemplates();
        if (c == null || c.getCount() < 0) text.setChecked(true);
    }

    public void removeFlags(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    }

    private void sendSMS(String number, String message){
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(QuickSMS.this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(QuickSMS.this,
                0, new Intent(DELIVERED), 0);

        registerReceiver(sentReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                sPrefs = new SharedPrefs(QuickSMS.this);
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        removeFlags();
                        finish();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        buttonSend.setText(getString(R.string.string_retry));
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        buttonSend.setText(getString(R.string.string_retry));
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        buttonSend.setText(getString(R.string.string_retry));
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        buttonSend.setText(getString(R.string.string_retry));
                        break;

                }
            }
        }, new IntentFilter(SENT));

        // ---when the SMS has been delivered---
        registerReceiver( deliveredReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(QuickSMS.this, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(QuickSMS.this, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, sentPI, deliveredPI);
    }

    @Override
    public void onBackPressed() {
        sPrefs = new SharedPrefs(QuickSMS.this);
        removeFlags();
        finish();
    }
}
