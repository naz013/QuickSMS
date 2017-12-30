package com.hexrain.design.quicksms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.hexrain.design.quicksms.helpers.TemplateItem;

public class QuickSMSActivity extends Activity {

    private static final int REQ_PERM = 1236;

    private TextView buttonSend;
    private RecyclerView messagesList;
    private EditText textField;
    private RadioButton text, template;
    private LinearLayout customContainer;

    private String number;
    private ColorSetter cs = new ColorSetter(QuickSMSActivity.this);
    private QuickAdapter quickAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(cs.getFullscreenStyle());
        runOnUiThread(() -> getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD));

        setContentView(R.layout.quick_message_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(cs.colorStatus());
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        text = findViewById(R.id.text);
        template = findViewById(R.id.template);
        textField = findViewById(R.id.textField);
        customContainer = findViewById(R.id.customContainer);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        messagesList = findViewById(R.id.messagesList);
        messagesList.setLayoutManager(new LinearLayoutManager(this));

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.template){
                customContainer.setVisibility(View.GONE);
                messagesList.setVisibility(View.VISIBLE);
            }

            if (checkedId == R.id.text){
                messagesList.setVisibility(View.GONE);
                customContainer.setVisibility(View.VISIBLE);
            }
        });
        template.setChecked(true);

        Intent i = getIntent();
        number = i.getStringExtra(Constants.ITEM_ID_INTENT);

        buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(v -> sendMessage());
        buttonSend.setTypeface(typeface);

        String name = Contacts.getContactNameFromNumber(QuickSMSActivity.this, number);

        TextView contactInfo = findViewById(R.id.contactInfo);
        contactInfo.setTypeface(typeface);
        contactInfo.setText(name + "\n" + number);

        loadTemplates();
    }

    private void sendMessage() {
        if (!checkPermissions()) {
            askPermissions();
            return;
        }
        if (quickAdapter.getItemCount() == 0) {
            text.setChecked(true);
            Toast.makeText(QuickSMSActivity.this, getString(R.string.empty_list_warming),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (template.isChecked()) {
            int position = quickAdapter.getSelectedPosition();
            TemplateItem item = quickAdapter.getItem(position);
            String message = Crypter.decrypt(item.getMessage());
            sendSMS(number, message);
        }
        if (text.isChecked()){
            String message = textField.getText().toString().trim();
            if (message.matches("")) return;
            sendSMS(number, message);
        }
    }

    private void loadTemplates(){
        Database db = new Database(this);
        db.open();
        quickAdapter = new QuickAdapter(db.getItems(), QuickSMSActivity.this);
        db.close();
        messagesList.setAdapter(quickAdapter);
        if (quickAdapter.getItemCount() == 0) text.setChecked(true);
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

        PendingIntent sentPI = PendingIntent.getBroadcast(QuickSMSActivity.this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(QuickSMSActivity.this,
                0, new Intent(DELIVERED), 0);

        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
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
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(QuickSMSActivity.this, "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(QuickSMSActivity.this, "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, sentPI, deliveredPI);
    }

    private void askPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                            android.Manifest.permission.READ_PHONE_STATE,
                            android.Manifest.permission.SEND_SMS,
                            android.Manifest.permission.ACCESS_NETWORK_STATE,
                            android.Manifest.permission.READ_CONTACTS},
                    REQ_PERM);
        }
    }

    private boolean checkPermissions() {
        return !(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) return;
        switch (requestCode) {
            case REQ_PERM:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMessage();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        removeFlags();
        finish();
    }
}
