package com.jiradeto.liveat500px;

import android.app.Application;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

/**
 * Created by 515895 on 2/4/2016.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize

        Contextor.getInstance().init(getApplicationContext());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        ///

    }
}
