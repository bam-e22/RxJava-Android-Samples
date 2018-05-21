package io.github.stack07142.rxjava_android_samples;

import android.app.Application;

import timber.log.Timber;

public class BaseApplication extends Application {

    BaseApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = (BaseApplication) getApplicationContext();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public BaseApplication getInstance() {
        return instance;
    }
}
