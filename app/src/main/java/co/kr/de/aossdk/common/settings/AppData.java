package co.kr.de.aossdk.common.settings;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import cat.ereza.customactivityoncrash.config.CaocConfig;
import timber.log.Timber;

public class AppData extends Application {
    public static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        CaocConfig.Builder.create()
                .showErrorDetails(true)     // 오류내용을 볼 수 있는 버튼을 보여줄지 여부
                .trackActivities(true)      // registerActivityLifecycleCallbacks() 사용하여 Activity 로그 추가
                .apply();
    }

    // 환경설정값을 읽어들인다. CenterAuthSettingFragment.class 에서 저장되는 값과 동일한 저장공간을 사용하기 위해서 PreferenceManager 의 저장공간을 선택한다.
    public static SharedPreferences getPref() {
        return appContext.getSharedPreferences(appContext.getPackageName() + "_preferences", Context.MODE_PRIVATE);
    }

}
