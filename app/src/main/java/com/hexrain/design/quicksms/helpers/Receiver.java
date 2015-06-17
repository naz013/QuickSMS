package com.hexrain.design.quicksms.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.hexrain.design.quicksms.QuickSMS;

public class Receiver extends BroadcastReceiver {
    Context mContext;
    String incoming_nr;
    private int prev_state;
    long startCallTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); //TelephonyManager object
        CustomPhoneStateListener customPhoneListener = new CustomPhoneStateListener();
        telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_CALL_STATE); //Register our listener with TelephonyManager
        mContext = context;
    }

    /* Custom PhoneStateListener */
    public class CustomPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber){
            SharedPrefs prefs = new SharedPrefs(mContext);

            if(incomingNumber != null && incomingNumber.length() > 0) incoming_nr = incomingNumber;

            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    prev_state = state;
                    startCallTime = System.currentTimeMillis();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    prev_state = state;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if((prev_state == TelephonyManager.CALL_STATE_RINGING)){
                        prev_state = state;
                        //Rejected or Missed call
                        long currTime = System.currentTimeMillis();
                        if (currTime - startCallTime <= 1000 * 15){
                            //rejected call
                            //Show quick SMS sending window
                            if (incoming_nr != null && prefs.loadBoolean(Constants.PREFERENCES_QUICK_SMS)) {
                                mContext.startActivity(new Intent(mContext, QuickSMS.class)
                                        .putExtra(Constants.ITEM_ID_INTENT, incoming_nr).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                break;
                            }
                        }
                    }
                    break;
            }
        }
    }
}