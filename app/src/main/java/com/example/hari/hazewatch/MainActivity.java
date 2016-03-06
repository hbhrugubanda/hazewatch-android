package com.example.hari.hazewatch;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;
import android.widget.Toast;

import com.variable.framework.android.bluetooth.BluetoothService;
import com.variable.framework.dispatcher.DefaultNotifier;
import com.variable.framework.node.BaseSensor;
import com.variable.framework.node.NodeDevice;
import com.variable.framework.node.enums.NodeEnums;

import com.example.hari.hazewatch.fragment.NodeConnectionDialog;
import com.example.hari.hazewatch.fragment.OxaFragment;
import com.example.hari.hazewatch.util.NodeApplication;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NodeDevice.SensorDetector {
    public boolean recordingState;
//    private NodeDevice mConnectedNODE;

    private static final String TAG = MainActivity.class.getName();

    private boolean isPulsing = false;
    private ProgressDialog mProgressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set variables
        recordingState = false;
//        com.variable.application.NodeApplication.initialize(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    @Override
    public void onResume(){
        super.onResume();

        ensureBluetoothIsOn();

        //Start Options Fragment
        Fragment frag = new MainOptionsFragment().setOnClickListener(this);
        animateToFragment(frag, MainOptionsFragment.TAG);

        //Registering for Events.
        DefaultNotifier.instance().addSensorDetectorListener(this);
    }

    @Override
    public void onPause(){
        super.onPause();

        NodeDevice node = NodeApplication.getActiveNode();
        if(isNodeConnected(node)){
            node.disconnect(); //Clean up after ourselves.
        }

        //Registering for Events
        DefaultNotifier.instance().removeSensorDetectorListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){ ensureBluetoothIsOn();  }
    }

    @Override
    public void onClick(View view) {
        NodeDevice node = NodeApplication.getActiveNode();
        if(view.getId() == R.id.btnPairedNodes){
            NodeConnectionDialog f = NodeConnectionDialog.newInstance();
            animateToFragment(f, NodeConnectionDialog.FRAGMENT_TAG);
            return;
        }

        if(!isNodeConnected(node))
        {
            Toast.makeText(this, "No Connection Available", Toast.LENGTH_SHORT ).show();
            return;
        }
        switch(view.getId()){
            case R.id.btnOxa:
                if(checkForSensor(node, NodeEnums.ModuleType.OXA, true))
                    animateToFragment(new OxaFragment(), OxaFragment.TAG);
                break;

            //NODE must be polled to maintain an up to date array of sensors.
            case R.id.btnRefreshSensors:
                node.requestSensorUpdate();
                break;

            case R.id.btnPulseLed:
                if(isPulsing){
                    ((Button) view).setText("Pulse LEDs" );
                    node.ledRestoreDefaultBehavior();
                }else{
                    ((Button) view).setText("Restore LEDs");
                    node.ledsPulse((byte) 0xFF, (byte) 0x0F, (byte) 0xFF, (byte) 0xF0, (short) 2000, (short) 25);
                }

                isPulsing = !isPulsing;
        }
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

    //region Private Methods

    /**
     * Invokes a new intent to request to start the bluetooth, if not already on.
     */
    private boolean ensureBluetoothIsOn(){
        if(!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            btIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(btIntent, 200);
            return false;
        }

        return true;
    }

    /**
     * Checks if a fragment with the specified tag exists already in the Fragment Manager. If present, then removes fragment.
     *
     * Animates out to the specified fragment.
     *
     *
     * @param frag
     * @param tag
     */
    public void animateToFragment(final Fragment frag, final String tag){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment existingFrag = getSupportFragmentManager().findFragmentByTag(tag);
        if(existingFrag != null){
            getSupportFragmentManager().beginTransaction().remove(existingFrag).commitAllowingStateLoss();
        }

        ft.replace(R.id.center_fragment_container, frag, tag);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Checks for a specific sensor on a node.
     * @param node - the node
     * @param type - the module type to check for on the node parameter.
     * @param displayIfNotFound - allows toasting a message if module is not found on node.
     * @return true, if the node contains the module
     */

    private boolean checkForSensor(NodeDevice node, NodeEnums.ModuleType type, boolean displayIfNotFound){
        BaseSensor sensor = node.findSensor(type);
        if(sensor == null && displayIfNotFound){
            Toast.makeText(MainActivity.this, type.toString() + " not found on " + node.getName(), Toast.LENGTH_SHORT).show();
        }

        return sensor != null;
    }

    //node stuff
//    public void setActiveNode(NodeDevice node) { this.mConnectedNODE = node; }
//    public NodeDevice getActiveNode(NodeDevice node) { return this.mConnectedNODE; }


    /**
     * Determines if the node is connected. Null is permitted.
     * @param node
     * @return
     */
    private boolean isNodeConnected(NodeDevice node) { return node != null && node.isConnected(); }


    //Convenience Method
    private final void dismissProgressDialog(){
        if(mProgressDialog != null){
            try { mProgressDialog.dismiss(); } catch(Exception e){ e.printStackTrace(); }
        }
    }

    //Convenience Method
    private final void updateProgressDialog(String title, String message){
        if(mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Restart and Kill all connections....
                    BluetoothService.killConnections();
                }
            });
        }

        if(title != null) { mProgressDialog.setTitle(title);    }
        if(message != null) {   mProgressDialog.setMessage(message);    }

        if(!mProgressDialog.isShowing()){
            try { mProgressDialog.show(); } catch (Exception e){e.printStackTrace(); }
        }
    }
    //endregion
    //region Sensor Detector Callbacks

    @Override
    public void onSensorConnected(NodeDevice nodeDevice, final BaseSensor baseSensor) {
        Log.d(TAG, "Sensor Found: " + baseSensor.getModuleType() + " SubType: " + baseSensor.getSubtype() + " Serial: " + baseSensor.getSerialNumber());
        Toast.makeText(MainActivity.this, baseSensor.getModuleType() + " has been detected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorDisconnected(NodeDevice nodeDevice, final BaseSensor baseSensor) {
        Toast.makeText(MainActivity.this, baseSensor.getModuleType() + " has been removed", Toast.LENGTH_SHORT).show();
    }

    //endregionE
}
