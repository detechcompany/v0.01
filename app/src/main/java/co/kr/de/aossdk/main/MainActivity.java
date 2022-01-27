package co.kr.de.aossdk.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import co.kr.de.aossdk.R;
import timber.log.Timber;

public class MainActivity extends AbstractMainActivity {
    // context
    private Context context;

    // data
    private Bundle args;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("called : " + this.getClass().getName() + " > onCreate");

        super.onCreate(savedInstanceState);
        context = this;
        args = getIntent().getExtras();
        if (args == null) args = new Bundle();

        initView();
    }

    private void initView() {
        Timber.d("called : " + this.getClass().getName() + " > initView");

        initData();
    }

    private void initData() {
        goNext();
    }

    private void goNext() {
        startFragment(MainFragment.class, args, R.string.fragment_id_main);
    }
}