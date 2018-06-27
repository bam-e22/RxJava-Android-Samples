package io.github.stack07142.rxjava_android_samples.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.stack07142.rxjava_android_samples.R;
import io.github.stack07142.rxjava_android_samples.RVDividerItemDecoration;
import io.github.stack07142.rxjava_android_samples.RVLoggerAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class PollingFragment extends BaseFragment {
    private Unbinder unbinder;
    private RVLoggerAdapter loggerAdapter;
    private Disposable disposable;

    @BindView(R.id.rv_logger)
    RecyclerView rvLogger;

    @BindView(R.id.tv_current_value)
    TextView tvCurrentValue;

    @BindView(R.id.tv_max_value)
    TextView tvMaxValue;

    private int currentValue = 0;
    private static final int MAX_VALUE = 5;
    private static final int TIME_OUT = 10000;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_polling, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();

        tvCurrentValue.setText("" + currentValue);
        tvMaxValue.setText("/ " + MAX_VALUE);

        polling();
    }

    private void polling() {

        // Returns an Observable that emits a 0L after the initialDelay and ever increasing numbers after each period of time thereafter.
        disposable = Observable.interval(500L, TimeUnit.MILLISECONDS)
                .doOnSubscribe(disposable -> rvLogger.post(() -> log("doOnSubscribe()")))
                .doOnNext(sequence -> rvLogger.post(() -> log("Executing polling task, milliseconds= %d", sequence * 500L)))
                .takeWhile(__ -> currentValue < MAX_VALUE)
                .ignoreElements()
                .timeout(10L, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> log("onComplete"),
                        error -> log("onError()= %s", error.getMessage()));
    }

    @OnClick(R.id.btn_add_value)
    void onClickAddButton() {
        if (currentValue < MAX_VALUE) {
            currentValue++;
            tvCurrentValue.setText(String.valueOf(currentValue));
        }
    }

    @OnClick(R.id.btn_clear_value)
    void onClickClearButton() {
        currentValue = 0;
        loggerAdapter.clear();

        tvCurrentValue.setText(String.valueOf(currentValue));
        disposable.dispose();

        polling();
    }

    @Override public void onDestroyView() {
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
