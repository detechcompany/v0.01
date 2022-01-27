package co.kr.de.aossdk.common.util;

import android.text.TextUtils;

import java.util.regex.Pattern;

import co.kr.de.aossdk.common.settings.AppData;

public class Utils {

    // 앱내 공간에 저장된 값을 가져온다.
    public static boolean getSavedBoolValue(String key) {
        return AppData.getPref().getBoolean(key, false);
    }


    /**
     * html 태크 사용여부
     * 출처 : https://stackoverflow.com/a/22581832/8910486
     * @param str
     * @return
     */
    // html 태그 사용여부
    public static boolean isHtml(CharSequence str) {
        return !TextUtils.isEmpty(str) && isHtml(str.toString());
    }
    public static boolean isHtml(String str) {
        if (TextUtils.isEmpty(str)) return false;

        String tagStart= "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)\\>";
        String tagEnd= "\\</\\w+\\>";
        String tagSelfClosing= "\\<\\w+((\\s+\\w+(\\s*\\=\\s*(?:\".*?\"|'.*?'|[^'\"\\>\\s]+))?)+\\s*|\\s*)/\\>";
        String htmlEntity= "&[a-zA-Z][a-zA-Z0-9]+;";
        Pattern htmlPattern=Pattern.compile("("+tagStart+".*"+tagEnd+")|("+tagSelfClosing+")|("+htmlEntity+")", Pattern.DOTALL);
        return htmlPattern.matcher(str).find();
    }
}
