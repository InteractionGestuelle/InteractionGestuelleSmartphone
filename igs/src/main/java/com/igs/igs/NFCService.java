package com.igs.igs;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by novar_000 on 21/01/14.
 */
public class NFCService extends IntentService{


    public NFCService() {
        super("service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("", "Je suis dans le service");
        intent = new Intent(this, NFCManager.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }
}
