package com.android.laporan;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by acer on 11/17/2017.
 */

public class dex extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
