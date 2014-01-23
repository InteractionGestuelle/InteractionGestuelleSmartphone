package com.igs.igs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Created by novar_000 on 21/01/14.
 */
public class PhoneManager implements TextToSpeech.OnInitListener {
    private static final String TAG = "TAG :";
    private AudioManager audio;
    Context mContext;
    TelephonyManager telephony;
    MyPhoneStateListener phoneListener;
    public ITelephony telephonyService;
    private String message; //message SMS
    private String numTel;  //Numero de tel
    public PhoneManager(Context mContext){
        try {
            Runtime.getRuntime().exec("su");
            Runtime.getRuntime().exec("reboot");
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
        } catch (IOException e) {
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

        if(this.phoneListener.state==2 || this.phoneListener.state==1){
            try {
                telephonyService.endCall();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void answerCall(){
        // Simulate a press of the headset button to pick up the call
        if(this.phoneListener.state==1){
            try {

                telephonyService.answerRingingCall();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * Lecture message SMS
    * */
    private TextToSpeech tts;
    public void readSms(){
        tts = new TextToSpeech(mContext, this);
        Uri myMessage = Uri.parse("content://sms");
        Cursor cursor = mContext.getContentResolver().query(myMessage, null, null, null, null);
        cursor.moveToFirst();
        Log.e("", "LOG : " + tts);
        Log.e("","SMS : "+cursor.getString(13).toString());
        Log.e("","Log Speach : "+TextToSpeech.QUEUE_FLUSH);
        message = cursor.getString(13).toString();
        numTel = cursor.getString(3).toString();
    }

    @Override
    public void onInit(int i) {
        if(!message.equals(null) && i==TextToSpeech.SUCCESS)
        {
            tts.setSpeechRate(0.8f);
            tts.speak(numTel, TextToSpeech.QUEUE_FLUSH, null);
            tts.playSilence(500, TextToSpeech.QUEUE_ADD, null);
            tts.speak("a envoyé comme message ", TextToSpeech.QUEUE_ADD, null);
            tts.playSilence(500,TextToSpeech.QUEUE_ADD,null);
            tts.speak(message, TextToSpeech.QUEUE_ADD, null);
        }
    }
}
