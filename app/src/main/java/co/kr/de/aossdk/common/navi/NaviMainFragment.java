package co.kr.de.aossdk.common.navi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
//
import co.kr.de.aossdk.R;
import timber.log.Timber;

public class NaviMainFragment extends AbstractNaviFragment {
    // context
    private Context context;

    // view
    private View v;
    private ImageView ivNaviClose;
    private RadioButton rbAuth;
    private RadioButton rbApi;
    private RadioButton rbSetting;
    private RadioButton rbOpenLicense;

    // data
    private Bundle args;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("called : " + this.getClass().getName() + " > onCreate");

        context = getContext();
        args = getArguments();
        if (args == null) {
            args = new Bundle();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("called : " + this.getClass().getName() + " > onCreateView");

        v = inflater.inflate(R.layout.fragment_navi_main, container, false);
        initView();
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Timber.d("called : " + this.getClass().getName() + " > onViewCreated");

        initView();
    }

    void initView() {
        ivNaviClose = v.findViewById(R.id.ivNaviClose);
        ivNaviClose.setOnClickListener(onClick);
        rbAuth = v.findViewById(R.id.rbAuth);
        rbAuth.setOnClickListener(onClick);
        rbApi = v.findViewById(R.id.rbApi);
        rbApi.setOnClickListener(onClick);
        rbSetting = v.findViewById(R.id.rbSetting);
        rbSetting.setOnClickListener(onClick);
        rbOpenLicense = v.findViewById(R.id.rbOpenLicense);
        rbOpenLicense.setOnClickListener(onClick);

        rbAuth.performClick();

        initData();
    }

    void initData() {
        // ??????
    }

    // ?????? ?????????
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.ivNaviClose:
                    activity.closeNavi();
                    break;

                case R.id.rbAuth:
                    //startChildFragment(NaviAuthFragment.class);
                    break;

                case R.id.rbApi:
                    //startChildFragment(NaviAPICallFragment.class);
                    break;

                case R.id.rbSetting:
                    activity.closeNavi();
                    //activity.startFragment(SelfAuthSettingFragment.class, args, R.string.fragment_id_center_auth_setting);
                    break;

                case R.id.rbOpenLicense:
                    activity.closeNavi();
                    //activity.startFragment(OpenLicense.class, args, R.string.fragment_id_license);
                    break;


            }
        }
    };
}
