package com.igs.igs;

import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by novar_000 on 21/01/14.
 */
public class PhoneManager {
    private static final String TAG = "TAG :";
    private AudioManager audio;
    Context mContext;
    TelephonyManager telephony;
    MyPhoneStateListener phoneListener;
    public ITelephony telephonyService;
    public PhoneManager(Context mContext){
        this.mContext = mContext;
        audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);


        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        Log.e("","JE SUIS LA 2");
        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
        telephony = (TelephonyManager) mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        this.phoneListener=phoneListener;
        try {
            // Java reflection to gain access to TelephonyManager's
            // ITelephony getter
            Log.v(TAG, "Get getTeleService...");
            Class c = Class.forName(tm.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "FATAL ERROR: could not connect to telephony subsystem");
            Log.e(TAG, "Exception object: " + e);

        }

    }
/*
* Augmente le volume
* */
    public void VolumeUp(){
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }
/*
* Diminue le volume
* */
    public void VolumeDown(){
        audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
    }
/*
* Racroche l'appel si on est en cours de conversation
* */
    public void callEnd(){
        //Si state = 2 alors on est en cours de conversation
        Log.e("","THIS.ETAT : "+this.phoneListener);
        if(this.phoneListener.state==2){
            try {
                telephonyService.endCall();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
