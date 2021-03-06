package co.kr.de.aossdk.common.util.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import co.kr.de.aossdk.R;
import co.kr.de.aossdk.common.util.Utils;

@SuppressWarnings({"UnusedReturnValue", "WeakerAccess", "SameParameterValue", "unused"})
public class KmDialogDefault extends AlertDialog.Builder {
    private Context context;

    public KmDialogDefault(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    private String title;
    private CharSequence message;
    private CharSequence detailMessage;
    private String positiveBtnStr;
    private String negativeBtnStr;
    private ListAdapter adapter;
    private AdapterView.OnItemClickListener listener;
    private String[] multiChoiceItems;
    private boolean[] multiChoiceCheckedItems;
    private AdapterView.OnItemClickListener multiChoiceListener;
    private String[] singleChoiceItems;
    private int singleChoiceCheckedItem;
    private AdapterView.OnItemClickListener singleChoiceListener;

    @Override
    public AlertDialog.Builder setTitle(@Nullable CharSequence title) {
        this.title = String.valueOf(title);
        return super.setTitle("");
    }
    @Override
    public AlertDialog.Builder setMessage(@Nullable CharSequence message) {
        this.message = message;
        return super.setMessage("");
    }
    public AlertDialog.Builder setDetailMessage(@Nullable CharSequence detailMessage) {
        this.detailMessage = detailMessage;
        return super.setMessage("");
    }
    @Override
    public AlertDialog.Builder setPositiveButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.positiveBtnStr = String.valueOf(text);
        return super.setPositiveButton("", listener);
    }
    @Override
    public AlertDialog.Builder setNegativeButton(CharSequence text, DialogInterface.OnClickListener listener) {
        this.negativeBtnStr = String.valueOf(text);
        return super.setNegativeButton("", listener);
    }

    public AlertDialog.Builder setAdapter(ListAdapter adapter, AdapterView.OnItemClickListener listener) {
        this.adapter = adapter;
        this.listener = listener;
        return this;
    }

    public AlertDialog.Builder setMultiChoiceItems(String[] items, boolean[] checkedItems, AdapterView.OnItemClickListener multiChoiceListener) {
        this.multiChoiceItems = items;
        this.multiChoiceCheckedItems = checkedItems;
        this.multiChoiceListener = multiChoiceListener;
        return this;
    }

    public AlertDialog.Builder setSingleChoiceItems(String[] items, int checkedItem, AdapterView.OnItemClickListener listener) {
        this.singleChoiceItems = items;
        this.singleChoiceCheckedItem = checkedItem;
        this.singleChoiceListener = listener;
        return this;
    }

    @Override
    public AlertDialog show() {
        LayoutInflater inflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.km_alert_dialog_default, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvMessage = view.findViewById(R.id.tvMessage);
        TextView tvDetail = view.findViewById(R.id.tvDetail);
        CheckBox cbDetail = view.findViewById(R.id.cbDetail);
        LinearLayout twoBtn = view.findViewById(R.id.twoBtn);
        Button btnPositive = view.findViewById(R.id.btnPositive);
        Button btnNegative = view.findViewById(R.id.btnNegative);
        LinearLayout singleBtn = view.findViewById(R.id.singleBtn);
        Button btnSingle = view.findViewById(R.id.btnSingle);

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }

        // ???????????? ????????? html ????????? ??????????????? ???.
        if (Utils.isHtml(message)) {
            if (!TextUtils.isEmpty(message)) {
                tvMessage.setText(Html.fromHtml(message.toString()));
            }

        } else {
            // ?????? ???????????? fromHtml??? ???????????? \n??? ????????? ?????????????????? ????????????.
            tvMessage.setText(message);
        }

        // ????????????
        tvDetail.setText(detailMessage);
        tvDetail.setVisibility(View.GONE);
        if (TextUtils.isEmpty(detailMessage)) {
            cbDetail.setVisibility(View.GONE);
        } else {
            cbDetail.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(positiveBtnStr)) {
            btnPositive.setText(positiveBtnStr);
        }

        if (!TextUtils.isEmpty(negativeBtnStr)) {
            btnNegative.setText(negativeBtnStr);
        }

        if (!TextUtils.isEmpty(positiveBtnStr) && !TextUtils.isEmpty(negativeBtnStr)) {
            singleBtn.setVisibility(View.GONE);
        } else {
            twoBtn.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(positiveBtnStr))
                btnSingle.setText(positiveBtnStr);
            else
                btnSingle.setText(negativeBtnStr);
        }

        super.setView(view);
        final AlertDialog dialog = super.create();

        // ??????????????????
        cbDetail.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tvDetail.setVisibility(View.VISIBLE);
                cbDetail.setText("??????????????????");
            } else {
                tvDetail.setVisibility(View.GONE);
                cbDetail.setText("??????????????????");
            }
        });

        btnPositive.setOnClickListener(v -> {
            // performClick()??? ???????????? ??????????????? ?????? ??????????????????, callOnClick()??? ????????????.
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).callOnClick();
        });

        btnNegative.setOnClickListener(v -> dialog.getButton(DialogInterface.BUTTON_NEGATIVE).callOnClick());

        btnSingle.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(positiveBtnStr))
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).callOnClick();
            else
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).callOnClick();
        });

        ScrollView llContents = view.findViewById(R.id.svContents);
        ListView listView = view.findViewById(R.id.listView);

        // ???????????? ????????? ??????????????? ????????????.
        if (adapter != null && listener != null) {
            llContents.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view1, position, id) -> {
                dialog.dismiss();
                listener.onItemClick(parent, view1, position, id);
            });
        }

        // ??????????????? ????????? ????????????.
        else if (multiChoiceItems != null && multiChoiceListener != null) {
            llContents.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.select_dialog_multichoice, android.R.id.text1, multiChoiceItems) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (multiChoiceCheckedItems != null) {
                        boolean isItemChecked = multiChoiceCheckedItems[position];
                        if (isItemChecked) {
                            listView.setItemChecked(position, true);
                        }
                    }
                    return view;
                }
            };
            listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view2, position, id) -> {
                multiChoiceListener.onItemClick(parent, view2, position, id);
            });
        }

        // ??????????????? ????????? ????????????.
        else if (singleChoiceItems != null && singleChoiceListener != null) {
            llContents.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);

            adapter = new ArrayAdapter<CharSequence>(context, android.R.layout.select_dialog_singlechoice, android.R.id.text1, singleChoiceItems) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    if (position == singleChoiceCheckedItem) {
                        listView.setItemChecked(position, true);
                    }
                    return view;
                }
            };
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view2, position, id) -> {
                singleChoiceListener.onItemClick(parent, view2, position, id);
            });
        }

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();

        // ?????? ??????
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthPixel = (int)(displayMetrics.widthPixels*0.85);
        int heightPixel = ViewGroup.LayoutParams.WRAP_CONTENT;

        dialog.getWindow().setLayout(widthPixel, heightPixel);

        return null;
    }
}
