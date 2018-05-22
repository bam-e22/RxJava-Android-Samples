package io.github.stack07142.rxjava_android_samples.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.stack07142.rxjava_android_samples.R;
import io.github.stack07142.rxjava_android_samples.RVDividerItemDecoration;
import io.github.stack07142.rxjava_android_samples.RVLoggerAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class ConcurrencyWithSchedulersDemoFragment extends BaseFragment {
    private Unbinder unbinder;
    private RVLoggerAdapter loggerAdapter;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.rv_logger)
    RecyclerView rvLogger;

    private int index = 0;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concurrency_schedulers, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        compositeDisposable.clear();
    }

    private void log(String msg) {
        loggerAdapter.add(msg);
    }

    private void setupLogger() {
        loggerAdapter = new RVLoggerAdapter(30);
        rvLogger.setAdapter(loggerAdapter);
        rvLogger.addItemDecoration(new RVDividerItemDecoration.Builder(getActivity(), RVDividerItemDecoration.VERTICAL_SKIP_FIRST)
                .build());
    }

    @OnClick(R.id.btn_start_operation)
    void startLongOperation() {
        progressBar.setVisibility(View.VISIBLE);
        log("Button Clicked:: " + index++);

        Disposable d = Observable.just(true)
                .subscribeOn(Schedulers.newThread())
                .map(aBoolean -> {
                    longOperation();
                    return aBoolean;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        next -> {
                        },
                        err -> {
                            log("onError(): " + err.getMessage());
                            progressBar.setVisibility(View.GONE);
                        },
                        () -> {
                            log("onComplete()");
                            progressBar.setVisibility(View.GONE);
                        }
                );

        compositeDisposable.add(d);
    }

    private void longOperation() {
        log("performing long operation");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Timber.d("Operation was interrupted");
        }
    }
}
