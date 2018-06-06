package com.navoki.megamovies.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.navoki.megamovies.R;

/**
 * Created by Name name on 6/5/2018.
 */
public class Util {

    public static void showError(Exception error, Context context) {
        String message = null;
        if (error instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (error instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (error instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (error instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

 /*   public static void log(String tag, String msg) {
        // if (BuildConfig.LOG_DEBUG_MODE)
        Log.e(tag, msg);


    }*/

    /**
     * Animation with
     * Exit current Activity- SlideOut to Left
     * Entry new Activity- SlideIn from Right
     *
     * @param context
     */
    public static void finishEntryAnimation(Context context, Intent intent) {
        AppCompatActivity activity = (AppCompatActivity) context;
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.anim_slide_in_from_right, R.anim.anim_slide_out_to_left);
        activity.finish();

    }



    public static void checkConnection(Context context) {

        final ConnectivityManager connectivityManager =
                ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));

        if (connectivityManager.getActiveNetworkInfo() != null) {
            if (!connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected();
            }
        }

    }
}
