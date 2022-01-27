package co.kr.de.aossdk.biz.browser;

import static android.speech.tts.TextToSpeech.ERROR;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Locale;

import co.kr.de.aossdk.R;
import co.kr.de.aossdk.biz.AbstractServiceMainFragment;
import co.kr.de.aossdk.main.MainActivity;
import timber.log.Timber;

public class BrowserReadFragment extends AbstractServiceMainFragment implements TextToSpeech.OnInitListener {
    // context
    private Context context;

    // view
    private View view;
    private ListView mListView;
    // Button
    protected Button btnReadAll;
    protected Button btnStop;
    protected Button btnPause;

    // TTS 변수 선언
    private TextToSpeech ttsHtml;
    private String strHtml;

    // data
    private Bundle args;

    private ArrayList<String[]> htmlContList = null;

    @Override
    public void onInit(int status) {
        if (status != ERROR){
            int result = ttsHtml.setLanguage(Locale.KOREA); // 언어 선택
            if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                Timber.e("TTS : This Language is not supported");
            } else {
                Timber.e("WEB을 읽을 상태가 되었습니다.");
            }
        }else{
            Timber.e("TTS : Initialization Failed!");
        }
    }

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
        ttsHtml = new TextToSpeech(context, this);

        strHtml = args.getString("DET_READ_HTML");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.biz_browser_readcont_frag, container, false);
        Timber.d("called : " + this.getClass().getName() + " > onCreateView");
        initView();
        return view;
    }
    private void initReadbleListView() {
        String tmp = "";
        if ( (strHtml == null) || strHtml.length() <= 0 ) {
            strHtml = "<html><init>INIT</init></html>";
        }
        Document doc = Jsoup.parse(strHtml);
        Elements docs = doc.getAllElements().first().getAllElements();

        if ( docs == null ) return;
        htmlContList = new ArrayList<String[]>();
        for(int i = 0; i < docs.size(); i++){
            if ( i == 0 ) continue; // root tag 제외
            Element node = docs.get(i);
            if ( node.hasText() && ( node.childNodeSize() == 1 ) && node.text().trim().length() > 10 ) {
                htmlContList.add(new String[] {node.tagName().trim(), node.text().trim()});
            }
        }
    }

    private void initView() {
        Timber.e("READ HTML : " + strHtml);
        initReadbleListView(); // HTML Parsing & array 셋팅
        mListView = view.findViewById(R.id.listView);

        CustomAdapter adapter = new CustomAdapter(context, 0, htmlContList);
        mListView.setAdapter(adapter);

        btnPause = view.findViewById(R.id.btnPause);
        btnPause.setOnClickListener( v -> {
            ttsHtml.stop();
        });

        btnStop = view.findViewById(R.id.btnStop);
        btnStop.setOnClickListener( v -> {
            ttsHtml.stop();
        });

        btnReadAll = view.findViewById(R.id.btnReadAll);
        btnReadAll.setOnClickListener( v ->{
            int vlCnt = mListView.getAdapter().getCount();
            Timber.d("전체 갯수 : " + vlCnt);
            for ( int i = 0; i < vlCnt; i ++ ) {
                String[] vlItem = (String[])mListView.getAdapter().getItem(i);
                Timber.d("VIEW : " + vlItem[0] + " : " + vlItem[1]);
                ttsHtml.speak(vlItem[1], TextToSpeech.QUEUE_ADD, null,"speak");
            }
        });
    }

    private class CustomAdapter extends ArrayAdapter<String[]> {
        private ArrayList<String[]> items;
        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String[]> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }
        public View getView(int pos, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.biz_browser_readcontitem_frag, null);
            }
            TextView tvTag = (TextView)v.findViewById(R.id.tvTag);
            tvTag.setText(items.get(pos)[0]); // tag
            final String sTag = items.get(pos)[0]; // cont

            TextView tvCont = (TextView)v.findViewById(R.id.tvCont);
            tvCont.setText(items.get(pos)[1]); // tag
            final String sCont = items.get(pos)[1]; // cont

            Button button = (Button)v.findViewById(R.id.btnEread);
            button.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    Toast.makeText(activity, sTag + " : " + sCont, Toast.LENGTH_SHORT).show();
                    Timber.d("go HTML : " + sTag + " : " + sCont);
                    //if ( !ttsHtml.isSpeaking() )
                    ttsHtml.speak(sCont, TextToSpeech.QUEUE_ADD, null,"speak");
                }
            });
            return v;
        }
    }


    // source from stackoverflow
    public void speakCheckInBackground(String text) {
        Timber.e("post : " + this.getClass().getName() + " : speakCheckInBackground");

        ttsHtml.speak(text, TextToSpeech.QUEUE_FLUSH, null,null);
        new Waiter().execute();

    }

    class Waiter extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Timber.e("post : " + this.getClass().getName() + " : AsyncTask");
            while (ttsHtml.isSpeaking()){
                try{Thread.sleep(1000);}catch (Exception e){}
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Timber.e("post : " + this.getClass().getName() + " : onPostExecute");

            //TTS has finished speaking. WRITE YOUR CODE HERE


        }
    }
}
