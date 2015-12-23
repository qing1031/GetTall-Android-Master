package com.obenproto.howtall;

import android.app.Application;

import com.obenproto.howtall.api.HowTallAPIClient;

public class HowTallApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        HowTallAPIClient.init();
    }
}
