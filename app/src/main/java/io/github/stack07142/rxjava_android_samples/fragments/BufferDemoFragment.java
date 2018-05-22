package io.github.stack07142.rxjava_android_samples.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.stack07142.rxjava_android_samples.R;
import io.github.stack07142.rxjava_android_samples.RVDividerItemDecoration;
import io.github.stack07142.rxjava_android_samples.RVLoggerAdapter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import timber.log.Timber;

public class BufferDemoFragment extends BaseFragment {
    private Unbinder unbinder;
    private RVLoggerAdapter loggerAdapter;

    @BindView(R.id.rv_logger)
    RecyclerView rvLogger;

    @BindView(R.id.btn_tap_me)
    Button btnTapMe;

    private Disposable disposable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buffer, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();

        disposable = RxView.clicks(btnTapMe)
                .map(onClickEvent -> {
                    log("Got a tap");
                    return 1;
                })
                .buffer(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(
                        new DisposableObserver<List<Integer>>() {

                            @Override
                            public void onNext(List<Integer> integers) {
                                if (integers.size() > 0) {
                                    log("%d taps", integers.size());
                                } else {
                                    Timber.d("No taps");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                log("onError(): " + e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                // fyi: you'll never reach here
                                log("onComplete()");
                            }
                        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        disposable.dispose();
    }

    private void setupLogger() {
        loggerAdapter = new RVLoggerAdapter(30);
        rvLogger.setAdapter(loggerAdapter);
        rvLogger.addItemDecoration(new RVDividerItemDecoration.Builder(getActivity(), RVDividerItemDecoration.VERTICAL_SKIP_FIRST)
                .build());
    }

    private void log(String msg) {
        loggerAdapter.add(msg);
    }

    private void log(String msg, Object... formatArgs) {
        loggerAdapter.add(String.format(msg, formatArgs));
    }
}
