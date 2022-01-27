package co.kr.de.aossdk.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;
import java.util.Locale;
//
import co.kr.de.aossdk.R;
import co.kr.de.aossdk.biz.ServiceMainFragment;
import co.kr.de.aossdk.biz.browser.BrowserReadFragment;
import co.kr.de.aossdk.biz.browser.BrowserReadNaviFragment;
import co.kr.de.aossdk.common.settings.Const;
import co.kr.de.aossdk.common.util.view.CustomContextWrapper;
import co.kr.de.aossdk.common.util.view.onKeyBackPressedListener;
import co.kr.de.aossdk.common.util.Utils;
import co.kr.de.aossdk.common.navi.NaviMainFragment;
import co.kr.de.aossdk.common.util.view.KmDialogDefault;
//
import timber.log.Timber;


/**
 * 메인 Activity 의 추상클래스.
 */
public abstract class AbstractMainActivity extends AppCompatActivity {
    // context
    private Context context;

    // view
    public DrawerLayout layBaseWin;         // 네비게이션뷰 드로우어
    protected Toolbar topToolbar;          // 툴바 레이아웃
    protected Button btnBackToolbar;          // 툴바 뒤로가기 버튼
    protected TextView tvTitleToolbar;         // 툴바 타이블
    protected Button btnNavi;           // 툴바 네비게이션 햄버거 버튼
    protected Button btnSetting;        // 툴바 설정


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Timber.d("called : " + this.getClass().getName() + " > onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initView();
    }

    private void initView() {
        Timber.d("called : " + this.getClass().getName() + " > initView");
        // App 기본 윈도우 그리기
        layBaseWin = findViewById(R.id.base_window);

        // 메인 상단 툴바 생성(그리기)
        topToolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(topToolbar);

        // 뒤로가기 버튼
        btnBackToolbar = findViewById(R.id.btnArrow);
        //btnBackToolbar.setOnClickListener(v -> onBackPressed());
        btnBackToolbar.setOnClickListener(v -> {
            Timber.d("called : " + this.getClass().getName() + " > btnBackToolbar clicked!!");
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof onKeyBackPressedListener) {
                    closeSoftKeypad();
                    ((onKeyBackPressedListener) fragment).onBackPressed();
                }
            }
        });

        // 툴바 타이틀
        tvTitleToolbar = findViewById(R.id.tvTitle);
        tvTitleToolbar.setOnLongClickListener(v -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.flContent);
            if (f != null) {
                new AlertDialog.Builder(context).setTitle("(TEST)클래스명").setMessage(f.getClass().getName()).show();
            }
            return true;
        });

        // 설정 버튼
        btnSetting = findViewById(R.id.btnSetting);
        btnSetting.setOnClickListener(v -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.flContent);
            if (f == null) {
                return;
            }
/**
            // 센터인증 화면이면 센터인증 설정으로, 자체인증 화면이면 자체인증 설정으로 이동
            if (f instanceof AbstractCenterAuthMainFragment) {
                startFragment(CenterAuthSettingFragment.class, null, R.string.fragment_id_center_auth_setting);
            } else if (f instanceof AbstractSelfAuthMainFragment) {
                startFragment(SelfAuthSettingFragment.class, null, R.string.fragment_id_self_auth_setting);
            }
**/
        });
        
        // 네비게이션 초기화
        Timber.d("called : " + this.getClass().getName() + " > initView > NaviMainFragment");

        getSupportFragmentManager().beginTransaction().replace(R.id.navi_drawer, new NaviMainFragment(), getString(R.string.fragment_id_navi)).commit();
        //getSupportFragmentManager().beginTransaction().replace(R.id.navi_drawer, new BrowserReadNaviFragment(), getString(R.string.frag_id_browser_readcont)).commit();

        // 햄버거 메뉴
        btnNavi = findViewById(R.id.btnNavi);
        btnNavi.setOnClickListener(v -> {
            Timber.d("called : " + this.getClass().getName() + " > btnNavi Clicked!!");
            closeSoftKeypad();
            if (layBaseWin.isDrawerOpen(GravityCompat.START)) {
                layBaseWin.closeDrawer(GravityCompat.START);
            } else {
                layBaseWin.openDrawer(GravityCompat.START);
            }
        });
        
    }
    // 백버튼 처리
    @Override
    public void onBackPressed() {
        Timber.d("called : " + this.getClass().getName() + " > onBackPressed override");

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof onKeyBackPressedListener) {
                closeSoftKeypad();
                ((onKeyBackPressedListener) fragment).onBackPressed();
            }
        }
    }

    public void onDefaultBackPressed() {
        Timber.d("called : " + this.getClass().getName() + " > onDefaultBackPressed");

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.flContent);
        if (f == null) {
            startFragment(MainFragment.class, null, R.string.app_name);
            return;
        }

        if (f.getClass() == ServiceMainFragment.class) {
            KmDialogDefault dialog = new KmDialogDefault(context);
            dialog.setTitle("종료");
            dialog.setMessage("앱을 종료하시겠습니까?");
            dialog.setPositiveButton("확인", (dialog1, which) -> exitApp());
            dialog.setNegativeButton("취소", null);
            dialog.setCancelable(false);
            dialog.show();
        } else {
            super.onBackPressed();
        }

    }


    public void closeSoftKeypad() {
        Timber.d("called : " + this.getClass().getName() + " > closeSoftKeypad");
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    // 네이게이션 메뉴 보여주기/감추기
    public void lockNavi(boolean enableLock) {
        Timber.d("called : " + this.getClass().getName() + " > lockNavi");
        if (enableLock) {
            btnNavi.setVisibility(View.GONE);
            layBaseWin.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            btnNavi.setVisibility(View.VISIBLE);
            layBaseWin.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    // 네이게이션 메뉴 닫기
    public void closeNavi() {
        Timber.d("called : " + this.getClass().getName() + " > closeNavi");
        layBaseWin.closeDrawer(GravityCompat.START);
    }

    // 툴바 보여주기/감추기
    public void setToolbarVisible(boolean bool) {
        Timber.d("called : " + this.getClass().getName() + " > setToolbarVisible");
        if (topToolbar == null) return;
        if (bool) {
            topToolbar.setVisibility(View.VISIBLE);
        } else {
            topToolbar.setVisibility(View.GONE);
        }
    }

    // 백버튼 보여주기/감추기
    public void setArrowVisible(boolean bool) {
        Timber.d("called : " + this.getClass().getName() + " > setArrowVisible");

        if (bool) {
            btnBackToolbar.setVisibility(View.VISIBLE);
        } else {
            btnBackToolbar.setVisibility(View.GONE);
        }
    }

    // fragment 시작
    public void startFragment(@NonNull Class fragmentClass, Bundle args, @StringRes int tagResId) {
        Timber.d("called : " + this.getClass().getName() + " > startFragment");

        String tag = getResources().getString(tagResId);
        startFragment(fragmentClass, args, tag, true, true);
    }

    // fragment 시작
    public void startFragment(@NonNull Class fragmentClass, Bundle args, String TAG_FRAGMENT, boolean replace, boolean keep) {
        Timber.d("called : " + this.getClass().getName() + " > startFragment 2");
        if (isFinishing() || isDestroyed()) {
            Timber.e("Activity가 종료중이거나 종료되어서 이동할수 없습니다.");
            return;
        }

        Fragment fragment;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Timber.e(e);
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager == null) {
            Timber.e("이동할 수 없음");
            return;
        }

        fragment.setArguments(args);

        // 현재 떠있는 Fragment와 이동할 프래그먼트가 같으면 add 하지 않음
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.flContent);
        if (currentFragment != null) {
            if (currentFragment.getClass() == fragment.getClass()) {
                Timber.d("fragment가 동일하여 이동하지 않습니다. : " + currentFragment.getClass().toString());
                return;
            }
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (replace) {
            transaction.replace(R.id.flContent, fragment, TAG_FRAGMENT);
        } else {
            transaction.add(R.id.flContent, fragment, TAG_FRAGMENT);
        }
        if (keep) {
            transaction.addToBackStack(TAG_FRAGMENT); // 해당 프래그먼트는 중단되고 사용자가 뒤로 탐색하면 재개
        }
        transaction.commitAllowingStateLoss();
    }

    public void setTitle(String title) {
        Timber.d("called : " + this.getClass().getName() + " > setTitle");
        tvTitleToolbar.setText(title);
    }

    public void showSetting(boolean enable) {
        Timber.d("called : " + this.getClass().getName() + " > showSetting");
        btnSetting.setVisibility(enable ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        Timber.d("called : " + this.getClass().getName() + " > attachBaseContext");
        // 사용언어 처리(설정에서 개발자언어 사용여부에 따라 표시)
        // Locale newLocale = Utils.getSavedBoolValue(Const.IS_DEV_LANG) ? Locale.ENGLISH : Locale.KOREA;
        Locale newLocale = Locale.KOREA;
        Context context = CustomContextWrapper.wrap(newBase, newLocale);
        super.attachBaseContext(context);
    }



    // 앱종료
    public void exitApp() {
        Timber.d("called : " + this.getClass().getName() + " > exitApp");

        finishAffinity();
    }
}
