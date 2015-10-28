package com.example.hari.hazewatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MainActivity extends AppCompatActivity{
    public boolean recordingState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set variables
        recordingState = false;

        //initialise all the components needed to establish the service
        //TODO - need to look at the below
        //https://developers.google.com/maps/documentation/android-api/map
        // and
        //https://developer.android.com/training/location/retrieve-current.html


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openMaps(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /** Called when the user clicks the Send button */
    public void startRecording(View view) {
        // Do something in response to button

        final Button button = (Button) findViewById(R.id.button);

        if (recordingState == false)
        {
            button.setBackgroundColor(Color.GREEN);
            button.setText("Recording");
            recordingState = true;
        }
        else
        {
            button.setBackgroundColor(Color.RED);
            button.setText("Record");
            recordingState = false;
        }

        System.out.println("Recording State has changed to: ");
        System.out.println(recordingState);

//
//        System.out.format("Background Colour: %d\n", Color.BLACK);
    }
}
