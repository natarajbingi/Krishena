package com.krishe.govern.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
            Toast.LENGTH_SHORT).show();
            //Intent pushIntent = new Intent(context, LocalService.class);
            //context.startService(pushIntent);
        }
    }
}