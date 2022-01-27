package co.kr.de.aossdk.biz;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
//
import co.kr.de.aossdk.R;
import co.kr.de.aossdk.main.MainActivity;
import co.kr.de.aossdk.main.AbstractMainFragment;

public class AbstractServiceMainFragment extends AbstractMainFragment {
    // context
    private Context context;
    protected MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        // 메인액티빅티 개체 연결
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 메인액티빅티 개체 연결
        activity = (MainActivity) getActivity();

    }


}
