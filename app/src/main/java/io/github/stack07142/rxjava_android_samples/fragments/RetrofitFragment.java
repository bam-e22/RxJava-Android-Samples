package io.github.stack07142.rxjava_android_samples.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.github.stack07142.rxjava_android_samples.BaseApplication;
import io.github.stack07142.rxjava_android_samples.R;
import io.github.stack07142.rxjava_android_samples.RVDividerItemDecoration;
import io.github.stack07142.rxjava_android_samples.RVLoggerAdapter;
import io.github.stack07142.rxjava_android_samples.retrofit.GithubService;
import io.reactivex.disposables.CompositeDisposable;

public class RetrofitFragment extends BaseFragment {
    private Unbinder unbinder;
    private RVLoggerAdapter loggerAdapter;
    private GithubService githubService;
    private CompositeDisposable disposables;

    @BindView(R.id.rv_logger)
    RecyclerView rvLogger;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retrofit, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();

        githubService = ((BaseApplication) getActivity().getApplicationContext()).getGithubService();
        disposables = new CompositeDisposable();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
