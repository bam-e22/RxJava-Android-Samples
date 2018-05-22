package io.github.stack07142.rxjava_android_samples;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.github.stack07142.rxjava_android_samples.fragments.MainFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new MainFragment(), this.toString())
                    .commit();
        }
    }
}
