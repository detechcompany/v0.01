package co.kr.de.aossdk.main;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

//
import co.kr.de.aossdk.R;
import co.kr.de.aossdk.common.util.view.onKeyBackPressedListener;
import co.kr.de.aossdk.common.util.KmProgressBar;
import timber.log.Timber;

public abstract class AbstractMainFragment extends Fragment implements onKeyBackPressedListener {
    // context
    private Context context;
    protected MainActivity activity;
    // progress
    private KmProgressBar progressBar;

    // listener
    protected interface OnClickListener {
        void onClick(String selectedItem);
    }

    @Override
    public void onBackPressed() {
        activity.onDefaultBackPressed();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("called : " + this.getClass().getName() + " > onCreate");
        super.onCreate(savedInstanceState);
        context = getContext();

        // 메인액티빅티 개체 연결
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.d("called : " + this.getClass().getName() + " > onActivityCreated");
        // 메인액티빅티 개체 연결
        activity = (MainActivity) getActivity();

        // 네이게이션 메뉴버튼 안보여주기
        //lockNavi(true);
        lockNavi(false);

        // 툴바 보여주기
        setToolbarVisible(true);

        // 툴바 타이틀 설정
        setTitle(getTag());

        // 세팅 아이콘 보여주기
        //showSetting(true);
        showSetting(false);
    }

    @Override
    public void onStart() {
        Timber.d("called : " + this.getClass().getName() + " > onStart");
        super.onStart();
        // 메인화면에서는 뒤로가기 버튼 제거
        if (getFragmentManager() != null) {
            Fragment f = getFragmentManager().findFragmentById(R.id.flContent);
            if (f instanceof MainFragment) {
                setArrowVisible(false);
            } else {
                setArrowVisible(true);
            }
        }
    }
    // 네비게이션 메뉴버튼 보여주기/감추기
    public void lockNavi(boolean enableLock) {
        activity.lockNavi(enableLock);
    }

    // 툴바 보여주기/감추기
    public void setToolbarVisible(boolean isVisible) {
        activity.setToolbarVisible(isVisible);
    }

    // 타이틀 설정
    public void setTitle(String title) {
        activity.setTitle(title);
    }

    // 세팅 아이콘 보여주기/감추기
    public void showSetting(boolean enable) {
        activity.showSetting(enable);
    }

    // 백버튼 보여주기/감추기
    public void setArrowVisible(boolean bool) {
        activity.setArrowVisible(bool);
    }

    // fragment 시작
    public void startFragment(Class fragmentClass, Bundle args, @StringRes int tagResId) {
        activity.startFragment(fragmentClass, args, tagResId);
    }

    // fragment 시작
    public void startFragment(Class fragmentClass, Bundle args, String TAG_FRAGMENT, boolean replace, boolean keep) {
        activity.startFragment(fragmentClass, args, TAG_FRAGMENT, replace, keep);
    }

    // child fragment 시작
    public void startChildFragment(Class fragmentClass, Bundle args) {
        if (fragmentClass == null) return;
        Fragment fragment;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Timber.e(e);
            return;
        }

        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.setArguments(args);
        fragmentManager.beginTransaction().commitAllowingStateLoss();
    }

    // 통신 처리 등


    // 프로그래스바
    protected void showProgress() {
        if (progressBar == null) {
            progressBar = new KmProgressBar(context);
        }
        progressBar.show();
    }

    protected void hideProgress() {
        if (progressBar != null) {
            progressBar.hide();
        }
    }
}
