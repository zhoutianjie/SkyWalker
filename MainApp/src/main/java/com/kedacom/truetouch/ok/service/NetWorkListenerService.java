package com.kedacom.truetouch.ok.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.kedacom.baseutil.StringUtil;

/**
 * Created by zhoutianjie on 2018/12/4.
 */

public class NetWorkListenerService extends Service {

    private final String TAG = this.getClass().getSimpleName();

    private boolean firstRunApp = true;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(!TextUtils.equals(action,ConnectivityManager.CONNECTIVITY_ACTION)){
                return;
            }
            Log.d(TAG,"network change begin");
            if(firstRunApp){
                firstRunApp = false;
                return;
            }

            Log.d(TAG,"network change end");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver,mFilter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
