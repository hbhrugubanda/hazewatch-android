package com.example.hari.hazewatch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;

import com.variable.framework.node.NodeDevice;

public class MainActivity extends AppCompatActivity{
    public boolean recordingState;
    private NodeDevice mConnectedNODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set variables
        recordingState = false;
        //TODO - breaks here, change sdk version for 1.7?
        //com.variable.application.NodeApplication.initialize(this);

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

    //node stuff
    public void setActiveNode(NodeDevice node) { this.mConnectedNODE = node; }
    public NodeDevice getActiveNode(NodeDevice node) { return this.mConnectedNODE; }

    /** @returns true if the NODE is connected **/
    public boolean isConnected(){ return mConnectedNODE != null && mConnectedNODE.isConnected();  }
}
