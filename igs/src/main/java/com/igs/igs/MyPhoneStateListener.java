package com.igs.igs;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by novar_000 on 21/01/14.
 */
public class MyPhoneStateListener extends PhoneStateListener {

    public static int state;
    public void onCallStateChanged(int state, String incomingNumber) {
Log.e("","STATUE : "+state);
        this.state=state;

    }

}