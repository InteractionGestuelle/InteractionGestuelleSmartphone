package com.igs.igs;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.math.BigInteger;

/**
 * Created by novar_000 on 20/01/14.
 */
public class NFCManager extends Activity {

    private final static String TAG = "NTC Test";
    private PendingIntent mPendingIntent;
    private static IntentFilter[] mIntentFiltersArray;
    private static String[][] mTechListsArray;
    private NfcAdapter mAdapter;
    private PhoneManager phm;
    private Tag tag ;
    private MifareClassic mfc;
    private Button btNum;
    private EditText txNum;
    private EditText smsNum;
    private MusicActivity mService;
    private boolean mBound;
    private boolean playMusic = false;
    public final static String FAVORITE_NUM="num";
    public final static String FAVORITE_SMS="sms";
    private PowerManager.WakeLock wl;

    ServiceConnection mConnexion = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mBound= true;
            MusicActivity.LocalBinder binder = (MusicActivity.LocalBinder) iBinder;
            mService = ((MusicActivity.LocalBinder) iBinder).getService();
        }
    };
    @Override
    protected void onStart() {

        super.onStart();
        if (wl != null) {
            wl.acquire();
        }
        Intent i = new Intent(this,MusicActivity.class);
        bindService(i, mConnexion, BIND_AUTO_CREATE);

    }

   /* @Override
    protected void onStop() {
        super.onStop();
        if(mBound){
            mService.unbindService(mConnexion);
            mBound=false;
        }
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wl != null) {
            wl.release();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       // Intent intent = new Intent(this, MusicActivity.class);
       // startService(intent);

        //empécher la mise en veille
        PowerManager pm = (PowerManager) this.getSystemService(this.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, this.getClass().getName());

        // numero de tel;
        SharedPreferences preferences = getSharedPreferences("Numero_Tel", MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        String numtest = preferences.getString("num", null);
        String smstest = preferences.getString("sms", null);
        if(numtest==null || smstest ==null){
            setContentView(R.layout.activity_main);
            btNum = (Button) findViewById(R.id.numeroB);
            txNum = (EditText) findViewById(R.id.numeroT);
            smsNum = (EditText) findViewById(R.id.smsTel);

            btNum.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(view == btNum){
                        Editable numero = txNum.getText();
                        Editable sms = smsNum.getText();
                        Log.e("", "Numero : " + numero);
                        editor.putString(FAVORITE_NUM, "" + numero);
                        editor.putString(FAVORITE_SMS, "" + sms);
                        editor.commit();
                        btNum.setVisibility(View.GONE);
                        txNum.setVisibility(View.GONE);
                        smsNum.setVisibility(View.GONE);
                    }
                }
            });
        }

        Log.i(TAG, "onCreate, action : " + getIntent().getAction());
        this.phm = new PhoneManager(this);
       // setContentView(R.layout.activity_main);
        btNum = (Button) findViewById(R.id.numeroB);
        txNum = (EditText) findViewById(R.id.numeroT);
        //Get NFC ADAPTER (if NFC enabled)
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            Log.e("","Pas de NFC");
        }
        // Android system will populate it with the details of the tag when it is scanned
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        Parcelable[] rawMsgs = getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(rawMsgs!=null)
        {
            NdefMessage[] msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {    msgs[i] = (NdefMessage) rawMsgs[i];
                Log.e("","Message" + msgs[i]);
            }
        }

        IntentFilter tagD = new IntentFilter("com.android.nfc.dhimpl.NativeNfcTag.mIsPresent");
        Log.e("","PASSAGE TAG = "+tagD);
        IntentFilter mndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            //Handles all MIME based dispatches !!! specify only the ones that you need.
            mndef.addDataType("*/*");

        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mIntentFiltersArray = new IntentFilter[] {mndef};
        //array of TAG TECHNOLOGIES that your application wants to handle
        mTechListsArray = new String[][] { new String[] { NfcA.class.getName()},
                new String[] {NfcB.class.getName()},
                new String[] {NfcV.class.getName()},
                new String[] {MifareClassic.class.getName()},
                new String[] {IsoDep.class.getName()},
                new String[] {Ndef.class.getName()}};



    }

    @Override
    public void onNewIntent(Intent intent){
        Log.i(TAG, "onNewIntent : " + intent.getAction());
        // get the tag object for the discovered tag
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        mfc = MifareClassic.get(tag);
        try {
            mfc.connect();
            Log.e("","MFC " + mfc.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("","TAG "+ bin2hex(tag.getId()));
        Log.e("","TAG ID "+ bin2hex(tag.getId()).charAt(0));
        if(!bin2hex(tag.getId()).equals(null)){
            if(mfc!=null){
                Log.e("ETAT2","connecté");
                while(mfc.isConnected()){
                    phm.VolumeUp();

                }
                Log.e("ETAT2","déconnecté");

            }
            //phm.VolumeUp();
            //phm.answerCall();
            //phm.readSms();
            // phm.sendSMS();
            //phm.answerCall();
            //phm.sendSOSSMS();
            /*Log.e("","Musique play : "+playMusic);
            if(bin2hex(tag.getId()).charAt(0) == '9'){
                if(!playMusic){
                    mService.play();
                    playMusic = true;
                }else{
                    mService.pause();
                    playMusic = false;
                }

            }*/

        }
        Log.e("", "TAG 0 : " + tag);
    }

    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,data));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        //treats all incoming intents when a tag is scanned and the appli is in foreground
        mAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFiltersArray, mTechListsArray);


    }
}