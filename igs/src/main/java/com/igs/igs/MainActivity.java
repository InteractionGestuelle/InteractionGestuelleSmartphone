package com.igs.igs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends Activity {


    private Button btNum;
    private EditText txNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btNum = (Button) findViewById(R.id.numeroB);
        txNum = (EditText) findViewById(R.id.numeroT);
         Intent intent = new Intent(MainActivity.this, NFCManager.class);
         MainActivity.this.startActivity(intent);

        btNum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(view == btNum){
                    Editable text = txNum.getText();
                    Log.e("", "Numero : " + text);

                }
            }
        });

    }
}



