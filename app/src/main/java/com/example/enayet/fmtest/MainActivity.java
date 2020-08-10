package com.example.enayet.fmtest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.nablabs.sdk.INABEventListener;
import org.nablabs.sdk.NABFMRadio;


public class MainActivity extends AppCompatActivity  implements INABEventListener, View.OnClickListener {

    public static final int MULTIPLIER = 1000000;
    public static final String UNAVAILABLE_CHANNEL_NUMBER = "FM Channel: Unavailable";
    public static final String CHANNEL_NUMBER_IS = "Channel Number: ";


    public static NABFMRadio sRadio;
    Button powerBTN;
    TextView statusTV;
    EditText frequencyTuneET;
    Button frequencyTuneBTN;
    Button toggleToSpeakerBTN;
    NumberPicker freqNumberPicker;
    private boolean speaker;
    private int takeSignalStrength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize our Objects
        speaker = false;
        statusTV = findViewById(R.id.showChannelTV_ID);
        powerBTN = findViewById(R.id.powerBTN_ID);
        frequencyTuneET = findViewById(R.id.frequencyET_ID);
        frequencyTuneBTN = findViewById(R.id.frequencyTuneBTN_ID);
        toggleToSpeakerBTN = findViewById(R.id.toggleSpeaker_ID);
/*        freqNumberPicker = findViewById(R.id.numberPicker_ID);

        freqNumberPicker.setMinValue(88);
        freqNumberPicker.setMaxValue(288);
        freqNumberPicker.setWrapSelectorWheel(true);

        String[] freqenciesForNumberPicker = new String[201];
        float freqVal = 88.0f;
        for (int i = 0; i < freqenciesForNumberPicker.length; i++){
            freqenciesForNumberPicker[i] = Float.toString((float) (freqVal + i*0.1));
            Log.d("FMR", ""+freqenciesForNumberPicker[i]);
        }
        freqNumberPicker.setDisplayedValues(freqenciesForNumberPicker);*/


        //set click listener for our buttons
        powerBTN.setOnClickListener(this);
        frequencyTuneBTN.setOnClickListener(this);
        toggleToSpeakerBTN.setOnClickListener(this);


/*
        // Android permission check
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Alert message to be shown");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
*/

        //initialize the NABRadio
        sRadio = new NABFMRadio();
        sRadio.initialize(this, getString(R.string.api_key), getString(R.string
                .auth_url), this);
        statusTV.setText(UNAVAILABLE_CHANNEL_NUMBER);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.powerBTN_ID:
                //if on then turn it off
                if (sRadio.getIsPoweredOn())
                {
                    sRadio.powerOffAsync();
                    statusTV.setText(UNAVAILABLE_CHANNEL_NUMBER);
                }
                else
                {
                    int initialFreq = (int) (103.7 * MULTIPLIER);
                    sRadio.powerOnAsync(initialFreq, NABFMRadio.REGION.NORTH_AMERICA);
                    statusTV.setText(CHANNEL_NUMBER_IS + (sRadio.getIsPoweredOn()
                            ? ((initialFreq/MULTIPLIER) + " MHz") : UNAVAILABLE_CHANNEL_NUMBER));
                }

                break;

            case R.id.frequencyTuneBTN_ID:
                //show the frequency tune activity
                float freq = (float) (88.0 * MULTIPLIER); //use our default in case the number entered is too high
                try {
                    freq = Float.parseFloat(frequencyTuneET.getText().toString());
                }catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (freq >= 88.0 && freq <= 108.0)
                {
                    sRadio.setFrequencyAsync((int) (freq * MULTIPLIER));
                    frequencyTuneET.setText("");
                    statusTV.setText(CHANNEL_NUMBER_IS + (sRadio.getIsPoweredOn()
                            ? (freq + " MHz") : UNAVAILABLE_CHANNEL_NUMBER));
                }
                else
                {
                    frequencyTuneET.setText("");
                    Toast.makeText(this, "Please Enter Valid Frequency Range", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.toggleSpeaker_ID:
                // toggle to speaker and headphone
                if (sRadio.getIsPoweredOn()) {
                    if (speaker) {
                        sRadio.toggleSpeaker(false);
                        toggleToSpeakerBTN.setText("Speaker");
                        speaker = false;
                    }
                    else
                    {
                        sRadio.toggleSpeaker(true);
                        toggleToSpeakerBTN.setText("Headphone");
                        speaker = true;
                    }
                }
                else
                    Toast.makeText(this, "Please turn on Power first.", Toast.LENGTH_SHORT).show();
            }

        }


    @Override
    public void onRadioPoweredOff() {

    }

    @Override
    public void onRadioPoweredOff(int i) {

    }

    @Override
    public void onRadioPoweredOn() {

    }

    @Override
    public void onRadioFrequencyChanged(int newFrequency) {
        Log.d("New frequency is", ""+newFrequency);
    }

    @Override
    public void onSignalStrengthChanged(int signalStrength) {
        Log.d("Enayet", "Signal Testing!!!"+signalStrength);
/*        takeSignalStrength = signalStrength;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //update our signal strength text, default to UNAVAILABLE
                String signal = "";
                if (takeSignalStrength == NABFMRadio.SIGNALSTRENGTH.GOOD) {
                    Toast.makeText(MainActivity.this, "Good Signal", Toast.LENGTH_SHORT).show();
                }
                else if (takeSignalStrength == NABFMRadio.SIGNALSTRENGTH.OK) {
                    Toast.makeText(MainActivity.this, "OK Signal", Toast.LENGTH_SHORT).show();
                }
                else if (takeSignalStrength == NABFMRadio.SIGNALSTRENGTH.POOR) {
                    Toast.makeText(MainActivity.this, "Poor Signal Strength", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(MainActivity.this, "Unavailable", Toast.LENGTH_SHORT).show();


                switch (signalStrength) {
                    case NABFMRadio.SIGNALSTRENGTH.GOOD:
                        signal = "GOOD";
                        Toast.makeText(MainActivity.this, "Good Signal", Toast.LENGTH_SHORT).show();
                        break;
                    case NABFMRadio.SIGNALSTRENGTH.OK:
                        signal = "OK";
                        Toast.makeText(MainActivity.this, "OK Signal", Toast.LENGTH_SHORT).show();
                        break;
                    case NABFMRadio.SIGNALSTRENGTH.POOR:
                        signal = "POOR";
                        Toast.makeText(MainActivity.this, "Poor Signal Strength", Toast.LENGTH_SHORT).show();
                        break;
                }

            }
        });*/

    }

    @Override
    public void onRadioRDSAlternativeFrequenciesChanged(int[] ints) {

    }

    @Override
    public void onRadioRDSProgramServiceChanged(String s) {

    }

    @Override
    public void onRadioRDSProgramIdentificationChanged(String s) {

    }

    @Override
    public void onRadioRDSRadioTextChanged(String s) {

    }

    @Override
    public void onAuthorizationComplete(int i) {

    }

    @Override
    public void onUnauthorized() {

    }

    @Override
    public void onUnsupported() {

    }

    @Override
    public void onError(int i) {

    }
}
