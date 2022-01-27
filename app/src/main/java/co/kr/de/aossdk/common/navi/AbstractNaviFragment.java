package co.kr.de.aossdk.common.navi;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//
import co.kr.de.aossdk.R;
import co.kr.de.aossdk.main.MainActivity;
//
import timber.log.Timber;

public class AbstractNaviFragment extends Fragment {
    // context
    private Context context;
    protected MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("called : " + this.getClass().getName() + " > onCreate");
        context = getContext();
        activity = (MainActivity) getActivity();
    }

    public void startChildFragment(@NonNull Class fragmentClass) {
        startChildFragment(fragmentClass, R.id.flMenuSub);
    }

    public void startChildFragment(@NonNull Class fragmentClass, @IdRes int frameLayout) {
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            Timber.e(e);
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(frameLayout, fragment);
            fragmentTransaction.commit();
        }
    }
}
