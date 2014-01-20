package com.igs.igs;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by novar_000 on 20/01/14.
 */
public class PhoneAction extends BroadcastReceiver {
    private static final String TAG = "Tag : ";
    com.android.internal.telephony.ITelephony telephonyService;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("","JE SUIS LA 1");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Log.e("","JE SUIS LA 2");
        try {
            // Java reflection to gain access to TelephonyManager's
            // ITelephony getter
            Log.v(TAG, "Get getTeleService...");
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(tm);
            Log.e("", "JE SUIS LA 3");

            Log.e("","OOOOOOOOOOOOOOOOOOOO");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,
                    "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);

        }

    }
    public void endCall(){
        try {
            telephonyService.endCall();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


}
