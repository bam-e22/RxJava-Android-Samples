package io.github.stack07142.rxjava_android_samples.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.Unbinder;
import io.github.stack07142.rxjava_android_samples.R;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;

public class TwoWayDataBindingFragment extends BaseFragment {
    private Unbinder unbinder;
    private Disposable disposable;
    private PublishProcessor<Float> _resultEmitterSubject;

    @BindView(R.id.et_num_1)
    private EditText etNum1;

    @BindView(R.id.et_num_2)
    private EditText etNum2;

    @BindView(R.id.tv_result)
    private TextView tvResult;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_two_way_databinding, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnTextChanged({R.id.et_num_1, R.id.et_num_2})
    void onTextChanged() {
        float num1;
        float num2;

        String strNum1 = etNum1.getText().toString();
        String strNum2 = etNum2.getText().toString();

        if (!TextUtils.isEmpty(strNum1)) {
            num1 = Float.parseFloat(strNum1);
        } else {
            num1 = 0f;
        }

        if (!TextUtils.isEmpty(strNum2)) {
            num2 = Float.parseFloat(strNum2);
        } else {
            num2 = 0f;
        }
    }
}
