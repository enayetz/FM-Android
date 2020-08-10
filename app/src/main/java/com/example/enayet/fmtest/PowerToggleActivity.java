package com.example.enayet.fmtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.nablabs.sdk.NABFMRadio;

public class PowerToggleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_toggle);

        //set the text for our status and button
        ((TextView) findViewById(R.id.powerTV_ID)).setText("Power: " + (MainActivity.sRadio.getIsPoweredOn() ? "On" : "Off"));
        ((Button) findViewById(R.id.powerBTN_ID)).setText("Turn " + (MainActivity.sRadio.getIsPoweredOn() ? "Off" : "On"));


        //setup our click handling
        findViewById(R.id.powerBTN_ID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.sRadio.getIsPoweredOn())
                    MainActivity.sRadio.powerOffAsync();
                else
                    MainActivity.sRadio.powerOnAsync(87700000, NABFMRadio.REGION.NORTH_AMERICA);
            }
        });
    }
}
