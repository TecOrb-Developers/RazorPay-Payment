package com.example.razorpaydemo.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import com.example.razorpaydemo.MyApplication;

import java.math.RoundingMode;
import java.text.DecimalFormat;


public class NetworkUtils {

    public static boolean isNetworkAvailable() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivityManager != null && connectivityManager.getActiveNetworkInfo() != null &&
                    connectivityManager.getActiveNetworkInfo().isConnected();
        } catch (Exception e) {
            return true;
        }
    }

    public static boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static double roundLocationPoint(double point){
        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return Double.valueOf(df.format(point).replace(",","."));
    }

}
