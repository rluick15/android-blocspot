package com.bloc.blocspot.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.bloc.blocspot.blocspot.R;

public class Utils {

    private static Context context = null;

    public static void setContext(Context context)
    {
        Utils.context = context;
    }

    /**
     * This method is used to set the Color of the category based on the color string passed in
     */
    public static void setColorString(String color, TextView colorLabel) {
        if(context != null) {
            if (color.equals(Constants.CYAN)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.cyan));
            } else if (color.equals(Constants.BLUE)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.blue));
            } else if (color.equals(Constants.GREEN)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.green));
            } else if (color.equals(Constants.MAGENTA)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.magenta));
            } else if (color.equals(Constants.ORANGE)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.orange));
            } else if (color.equals(Constants.RED)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.red));
            } else if (color.equals(Constants.ROSE)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.rose));
            } else if (color.equals(Constants.VIOLET)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.violet));
            } else if (color.equals(Constants.YELLOW)) {
                colorLabel.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            }
        }
    }

    /*
    * Check if the network if connected to wifi or the mobile network
    *
    * return: Boolean true if connected, false if not
    */
    public static boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase(Constants.NETWORK_WIFI))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase(Constants.NETWORK_MOBILE))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void checkIfConnected() {
        if(!haveNetworkConnection()) {
            Toast.makeText(context, context.getString(R.string.toast_no_network), Toast.LENGTH_SHORT).show();
        }
    }
}
