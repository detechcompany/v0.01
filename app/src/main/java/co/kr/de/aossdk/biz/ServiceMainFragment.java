package co.kr.de.aossdk.biz;

import android.content.Context;
import android.os.Bundle;
import android.provider.Browser;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//
import co.kr.de.aossdk.R;
import co.kr.de.aossdk.biz.browser.BrowserFragment;
import timber.log.Timber;

public class ServiceMainFragment extends AbstractServiceMainFragment{
    // context
    private Context context;
    // view
    private View view;
    // data
    private Bundle args;

    // view
    protected Button btnBrowser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("called : " + this.getClass().getName() + " > onCreate");
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.biz_servicemain_frag, container, false);
        Timber.d("called : " + this.getClass().getName() + " > onCreateView");
        initView();
        return view;
    }

    void initView() {
        btnBrowser = view.findViewById(R.id.btnSrvBrowser);
        btnBrowser.setOnClickListener( v -> {
            activity.startFragment(BrowserFragment.class, args, R.string.frag_id_browser);
        });
    }

}
