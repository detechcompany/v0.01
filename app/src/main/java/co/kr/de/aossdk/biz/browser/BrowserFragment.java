package co.kr.de.aossdk.biz.browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import static android.speech.tts.TextToSpeech.ERROR;

import static java.lang.Thread.sleep;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
//
import co.kr.de.aossdk.R;
import co.kr.de.aossdk.biz.AbstractServiceMainFragment;
import co.kr.de.aossdk.common.navi.NaviMainFragment;
import timber.log.Timber;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class BrowserFragment extends AbstractServiceMainFragment {
    // context
    private Context context;

    // view
    public DrawerLayout layBaseWin;         // 네비게이션뷰 드로우어
    private View view;
    protected Button btnGo;
    protected Button btnBack;
    protected Button btnRead;
    protected WebView vwWeb;
    protected EditText edtUrl;

    // TTS 변수 선언
    private TextToSpeech ttsHtml;
    private String strHtml;

    // data
    private Bundle args;

    // for tts
    private final Bundle params = new Bundle();

    @Override
    public void onDestroy() {
        if(ttsHtml != null){ // 사용한 TTS객체 제거
            ttsHtml.stop();
            ttsHtml.shutdown();
        }
        super.onDestroy();
    }

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
        view = inflater.inflate(R.layout.biz_browser_frag, container, false);
        Timber.d("called : " + this.getClass().getName() + " > onCreateView");
        initView();
        return view;
    }

    void initView() {
        // 브라우저 셋팅
        vwWeb = view.findViewById(R.id.webView1);
        vwWeb.setWebViewClient(new CookWebViewClient());

        vwWeb.getSettings().setJavaScriptEnabled(true); //Javascript를 사용하도록 설정
        vwWeb.addJavascriptInterface(new MyJavascriptInterface(), "Android");
        vwWeb.getSettings().setUseWideViewPort(true);
        vwWeb.getSettings().setLoadWithOverviewMode(true);
        vwWeb.getSettings().setBuiltInZoomControls(true);
        vwWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        vwWeb.getSettings().setSupportZoom(true);

        // 주소창 셋팅
        edtUrl = view.findViewById(R.id.edtUrl);
        String tmp = edtUrl.getText().toString();
        if ( tmp == null || tmp.length() <= 0 ) {
            tmp = "http://m.bobaedream.co.kr";
            edtUrl.setText(tmp);
        }

        // 버튼 셋팅
        btnRead = view.findViewById(R.id.btnRead);
        btnRead.setVisibility(View.GONE);


        //
        layBaseWin = view.findViewById(R.id.base_window);
        layBaseWin = activity.layBaseWin; // 이렇게 접근하는게 맞나??
        //activity.getSupportFragmentManager().beginTransaction().replace(R.id.navi_drawer, new BrowserReadFragment(), getString(R.string.frag_id_browser_readcont)).commit();
        btnRead.setOnClickListener( v -> {
            args.putString("DET_READ_HTML", strHtml);
            //drawer로 열기
            Timber.d("called : " + this.getClass().getName() + " > drawer로 열기1");
            BrowserReadNaviFragment naviFrag = new BrowserReadNaviFragment();
            naviFrag.setArguments(args);
            //activity.getSupportFragmentManager().beginTransaction().replace(R.id.navi_drawer, new BrowserReadNaviFragment(), getString(R.string.frag_id_browser_readcont)).commit();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.navi_drawer, naviFrag, getString(R.string.frag_id_browser_readcont)).commit();

            /**/
            activity.closeSoftKeypad();

            Timber.d("called : " + this.getClass().getName() + " > drawer로 열기2");

            if (layBaseWin.isDrawerOpen(GravityCompat.START)) {
                layBaseWin.closeDrawer(GravityCompat.START);
            } else {
                layBaseWin.openDrawer(GravityCompat.START);
            }
             /**/
            //새 fragment로 열기
            // activity.startFragment(BrowserReadFragment.class, args, R.string.frag_id_browser_readcont);
        });

        btnGo = view.findViewById(R.id.btnGo);
        btnGo.setOnClickListener( v -> {
            activity.closeSoftKeypad();
            String strCurUrl;
            strCurUrl = edtUrl.getText().toString();
            Timber.d("go HTML : " + strCurUrl);
            vwWeb.loadUrl(strCurUrl);
        });

        // 버튼 셋팅
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener( v -> {
            vwWeb.goBack();
        });

    }

    class CookWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            btnRead.setVisibility(View.GONE);
            showProgress();
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideProgress();
            if ( btnRead.getVisibility() == View.GONE ) {
                btnRead.setVisibility(View.VISIBLE);
            }
            /* * html 중 body 부분을 추출하기 위한 javascript * JavaScriptInterface의 getHtml()로 간다. */
            view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('html')[0].innerHTML);");
            //view.loadUrl("javascript:window.Android.getHtml(document.getElementsByTagName('head')[0].innerHTML);");
        }
    }

    public class MyJavascriptInterface {
        @JavascriptInterface
        public void getHtml(String html) { //위 자바스크립트가 호출되면 여기로 html이 반환됨
            strHtml = html;
            //Timber.d("called : " + html);
        }
    }

}
