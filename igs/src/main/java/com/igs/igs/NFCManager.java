package com.igs.igs;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

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
    private Button btNum;
    private EditText txNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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
        IntentFilter mtech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter mtag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        try {
            //Handles all MIME based dispatches !!! specify only the ones that you need.
            mndef.addDataType("*/*");

        }
        catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        mIntentFiltersArray = new IntentFilter[] {mndef,mtech,mtag };
        //array of TAG TECHNOLOGIES that your application wants to handle
        mTechListsArray = new String[][] { new String[] { NfcA.class.getName()},
                new String[] {NfcB.class.getName()},
                new String[] {NfcV.class.getName()},
                new String[] {IsoDep.class.getName()},
                new String[] {Ndef.class.getName()}};
    }

    @Override
    public void onNewIntent(Intent intent){
        Log.i(TAG, "onNewIntent : " + intent.getAction());
        // get the tag object for the discovered tag
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.e("","TAG "+ bin2hex(tag.getId()));
        Log.e("","TAG ID "+ bin2hex(tag.getId()).charAt(0));
        if(!bin2hex(tag.getId()).equals(null)){
            //phm.VolumeUp();
            //phm.answerCall();
            //phm.readSms();
            // phm.sendSMS();
            //phm.answerCall();
            //phm.sendSMS();
            phm.musicPlayer();
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