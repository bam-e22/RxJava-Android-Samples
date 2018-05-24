package io.github.stack07142.rxjava_android_samples;

import android.app.Application;

import io.github.stack07142.rxjava_android_samples.retrofit.GithubService;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class BaseApplication extends Application {
    BaseApplication instance;
    GithubService githubService;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = (BaseApplication) getApplicationContext();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        setupAPIClient();
    }

    public BaseApplication getInstance() {
        return instance;
    }

    private void setupAPIClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
                message -> Timber.tag("API LOG").d(message)
        );
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com/")
                .build();

        githubService = retrofit.create(GithubService.class);
    }

    public GithubService getGithubService() {
        return githubService;
    }
}
