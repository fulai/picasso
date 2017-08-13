package com.example.picasso.broadcase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import static android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED;
import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Created by xiaoyun on 2017/8/8.
 * 网络变化广播
 * 广播封装
 */

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    static final String EXTRA_AIRPLANE_STATE = "state";
    private Context mContext;

    public NetworkBroadcastReceiver(Context mContext) {
        this.mContext = mContext;
    }

    public void register() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction(CONNECTIVITY_ACTION);
        mContext.registerReceiver(this, intentFilter);
    }

    public void unRegister() {
        mContext.unregisterReceiver(this);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        final String action = intent.getAction();
        if (ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
            if (!intent.hasExtra(EXTRA_AIRPLANE_STATE)) {
                return; // No airplane state, ignore it. Should we query Utils.isAirplaneModeOn?
            }
            //飞行模式
        } else if (CONNECTIVITY_ACTION.equals(action)) {
            //网络变化
        }
    }
}
