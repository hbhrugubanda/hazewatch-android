/* See http://variableinc.com/terms-use-license for the full license governing this code. */
package com.example.hari.hazewatch.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.variable.demo.api.MessageConstants;
import com.variable.demo.api.NodeApplication;
import com.variable.demo.api.R;
import com.variable.framework.dispatcher.DefaultNotifier;
import com.variable.framework.node.NodeDevice;
import com.variable.framework.node.OxaSensor;
import com.variable.framework.node.enums.NodeEnums;
import com.variable.framework.node.reading.SensorReading;

import java.text.DecimalFormat;

/**
 * Created by coreymann on 8/13/13.
 */
public class OxaFragment extends Fragment implements OxaSensor.OxaListener {
    public static final String TAG = OxaFragment.class.getName();

    private TextView oxaText;
    private TextView oxaBaseLineA;
    private OxaSensor oxa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.oxa, null, false);
        oxaText = (TextView) root.findViewById(R.id.txtOxa);
        oxaBaseLineA = (TextView) root.findViewById(R.id.txtBaseLineA);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();

        DefaultNotifier.instance().removeOxaListener(this);

        //Turn off oxa
        oxa.stopSensor();

    }

    @Override
    public void onResume() {
        super.onResume();

        //Register Oxa Listener
        DefaultNotifier.instance().addOxaListener(this);

        NodeDevice node = NodeApplication.getActiveNode();
        if(node != null)
        {
            oxa = node.findSensor(NodeEnums.ModuleType.OXA);
            oxa.startSensor();
        }
    }

    @Override
    public void onOxaBaselineUpdate(OxaSensor sensor, final SensorReading<Float> baseline_reading) {
       mHandler.obtainMessage(MessageConstants.MESSAGE_OXA_BASELINE_A,baseline_reading.getValue()).sendToTarget();
    }

    @Override
    public void onOxaUpdate(OxaSensor sensor, SensorReading<Float> reading) {
        Message m = mHandler.obtainMessage(MessageConstants.MESSAGE_OXA_READING);
        m.getData().putFloat(MessageConstants.FLOAT_VALUE_KEY, reading.getValue());
        m.sendToTarget();
    }

    private final Handler mHandler = new Handler(){
     private final DecimalFormat formatter = new DecimalFormat("0.00");

     @Override
     public void handleMessage(Message message)
     {
        float value = message.getData().getFloat(MessageConstants.FLOAT_VALUE_KEY);
        switch(message.what){
            case MessageConstants.MESSAGE_OXA_READING:
                oxaText.setText(formatter.format(value) + " RAW");
                break;
            case MessageConstants.MESSAGE_OXA_BASELINE_A:
                oxaBaseLineA.setText(message.obj.toString());
                break;
        }
      }
    };
}
