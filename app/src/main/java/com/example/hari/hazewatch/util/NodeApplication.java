/* See http://variableinc.com/terms-use-license for the full license governing this code. */
package com.example.hari.hazewatch.util;

import android.app.Application;
import android.content.Context;

import com.variable.framework.node.NodeDevice;

/**
 * Created by coreymann on 6/10/13.
 */
public class NodeApplication extends Application {

    public static NodeDevice mActiveNode;
    private static Context context;


    public static void setActiveNode(NodeDevice node){ mActiveNode = node; }

    public static NodeDevice getActiveNode(){  return mActiveNode; }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        com.variable.application.NodeApplication.initialize(this);
    }

    @Override
    public void onTerminate() {
        com.variable.application.NodeApplication.unbindServiceAndReceiver();
        super.onTerminate();

    }
}
