package co.kr.de.aossdk.main;

import co.kr.de.aossdk.R;
import co.kr.de.aossdk.biz.ServiceMainFragment;
import timber.log.Timber;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainFragment extends AbstractMainFragment {
    // context
    private Context context;
    // view
    private View view;
    // data
    private Bundle args;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("called : " + this.getClass().getName() + " > onCreate");

        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        initView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView();
    }

    private void initView() {
        initData();
    }

    private void initData() {
        goNext();
    }

    private void goNext() {
        activity.startFragment(ServiceMainFragment.class, args, R.string.fragment_id_servicemain);
    }

}
