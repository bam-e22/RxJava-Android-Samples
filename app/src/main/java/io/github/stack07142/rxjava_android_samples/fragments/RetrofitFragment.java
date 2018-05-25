package io.github.stack07142.rxjava_android_samples.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.stack07142.rxjava_android_samples.BaseApplication;
import io.github.stack07142.rxjava_android_samples.R;
import io.github.stack07142.rxjava_android_samples.RVDividerItemDecoration;
import io.github.stack07142.rxjava_android_samples.RVLoggerAdapter;
import io.github.stack07142.rxjava_android_samples.retrofit.GithubService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RetrofitFragment extends BaseFragment {
    private Unbinder unbinder;
    private RVLoggerAdapter loggerAdapter;
    private GithubService githubService;
    private CompositeDisposable disposables;

    @BindView(R.id.rv_logger)
    RecyclerView rvLogger;

    @BindView(R.id.et_owner)
    EditText etOwner;

    @BindView(R.id.et_repo)
    EditText etRepo;

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
        disposables.clear();
    }

    @OnClick(R.id.btn_get_contributors)
    void getContributors() {
        disposables.add(
                githubService.contributors(etOwner.getText().toString(), etRepo.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                next -> {
                                    for (GithubService.Contributor c : next) {
                                        log("%s has made %d contributions to %s",
                                                c.login, c.contributions, etRepo.getText().toString());
                                    }
                                },
                                error -> log("onError(): %s", error.getMessage()),
                                () -> log("onComplete()")
                        )
        );
    }

    @OnClick(R.id.btn_get_contributors_with_email)
    void getContributorsWithEmail() {
        disposables.add(
                githubService.contributors(etOwner.getText().toString(), etRepo.getText().toString())
                        .flatMap(Observable::fromIterable)
                        .doOnNext(contributor -> {
                            Timber.d("doOnNext= %s", contributor.login);
                        })
                        .flatMap(contributor -> {
                            Observable<GithubService.User> userObservable =
                                    githubService.user(contributor.login)
                                            .doOnNext(user -> {
                                                Timber.d("name= %s, email= %s", user.name, user.email);
                                            });
                                            //.filter(user -> !TextUtils.isEmpty(user.name) && !TextUtils.isEmpty(user.email));
                            return Observable.zip(userObservable, Observable.just(contributor), Pair::new);
                        })
                        .doOnNext(pair -> {
                            Timber.d("name= %s, contribution= %d", pair.first.name, (int) pair.second.contributions);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                next -> {
                                    GithubService.User user = next.first;
                                    GithubService.Contributor contributor = next.second;
                                    log("%s(%s) has made %d contributions to %s",
                                            user.name, user.email, contributor.contributions, etRepo.getText().toString());
                                    Timber.d("%s(%s) has made %d contributions to %s",
                                            user.name, user.email, contributor.contributions, etRepo.getText().toString());
                                },
                                error -> {
                                    log("onError(): %s", error.getMessage());
                                    Timber.d("onError(): %s", error.getMessage());
                                },
                                () -> {
                                    log("onComplete()");
                                    Timber.d("onComplete()");
                                }
                        )
        );
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
