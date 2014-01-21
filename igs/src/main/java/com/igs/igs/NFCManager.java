package com.igs.igs;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.util.Log;

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
    public static boolean etat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate, action : " + getIntent().getAction());
        etat = false;

        //Get NFC ADAPTER (if NFC enabled)
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) {
            Log.e("","Pas de NFC");
        }
        // Android system will populate it with the details of the tag when it is scanned
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        //Launched by tag scan ?
        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.e("","Info Tag : "+tag);
        if(tag != null && (getIntent().getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)
                || getIntent().getAction().equals(NfcAdapter.ACTION_TECH_DISCOVERED)
                || getIntent().getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED))){
            Log.i(TAG, "onCreate, tag found, calling onNewTag");
            getNewTag(tag, getIntent());

            //Clear intent
            setIntent(null);
        }
// add intent filter
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



    private void getNewTag(Tag tag, Intent intent){
        if(tag == null) return;
        //Indicate to childs that a new tag has been detected
        onNewTag(tag);
    }

    @Override
    public void onNewIntent(Intent intent){
        Log.i(TAG, "onNewIntent : " + intent.getAction());

        // get the tag object for the discovered tag
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Log.e("","TAG 0 : "+tag);
        getNewTag(tag, intent);
    }

    //This function is called in child activities when a new tag is scanned.
    public void onNewTag(Tag tag){
        NfcA nfca = NfcA.get(tag);
        Log.e("","NFC Action");

        // MifareClassic mfc=MifareClassic.get(tagFromintent);
        PhoneManager phm = new PhoneManager(this);
        if(!bin2hex(tag.getId()).equals(null)){

            //phm.VolumeUp();
            etat=true;
        }
        else{
            etat = false;
        }
        phm.callEnd(etat);
        Log.e("","Etat NFC :"+etat);
        Log.e("","TAG 1 : "+  bin2hex(tag.getId()));
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