package io.github.stack07142.rxjava_android_samples.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.github.stack07142.rxjava_android_samples.R;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

import static android.util.Patterns.EMAIL_ADDRESS;

public class FormValidationCombineLatestFragment extends BaseFragment {
    private Unbinder unbinder;

    @BindView(R.id.et_email)
    EditText etEmail;

    @BindView(R.id.et_password)
    EditText etPassword;

    @BindView(R.id.et_number)
    EditText etNumber;

    @BindView(R.id.tv_valid_result)
    TextView tvValidResult;

    private Flowable<CharSequence> emailChangeFlowable;
    private Flowable<CharSequence> passwordChangeFlowable;
    private Flowable<CharSequence> numberChangeFlowable;
    private Disposable disposable;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_from_validation, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailChangeFlowable = RxTextView.textChanges(etEmail).skip(1).toFlowable(BackpressureStrategy.LATEST);
        passwordChangeFlowable = RxTextView.textChanges(etPassword).skip(1).toFlowable(BackpressureStrategy.LATEST);
        numberChangeFlowable = RxTextView.textChanges(etNumber).skip(1).toFlowable(BackpressureStrategy.LATEST);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        disposable.dispose();
    }

    @OnClick(R.id.btn_submit)
    void onClickSubmitButton() {
        Timber.d("onClickSubmitButton()");
        disposable = Flowable.combineLatest(emailChangeFlowable, passwordChangeFlowable, numberChangeFlowable,
                (email, password, number) -> {
                    boolean emailValid = !TextUtils.isEmpty(email) && EMAIL_ADDRESS.matcher(email).matches();
                    if (!emailValid) {
                        etEmail.setError("Invalid Email");
                    }

                    boolean passwordValid = !TextUtils.isEmpty(password) && password.length() > 8;
                    if (!passwordValid) {
                        etPassword.setError("Invalid Password");
                    }

                    boolean numberValid = !TextUtils.isEmpty(number);
                    if (numberValid) {
                        int num = Integer.parseInt(number.toString());
                        numberValid = num > 0 && num <= 100;
                    }

                    if (!numberValid) {
                        etNumber.setError("Invalid Number");
                    }

                    return emailValid && passwordValid && numberValid;
                })
                .subscribe(
                        formValid -> {
                            if (formValid) {
                                tvValidResult.setText("Valid");
                            } else {
                                tvValidResult.setText("Invalid");
                            }
                        }, e -> {
                            Timber.e(e, "onError()");
                        },
                        () -> {
                            Timber.d("onSuccess()");
                        }
                );
    }
}
