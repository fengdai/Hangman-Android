package com.github.fengdai.hangman.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.fengdai.hangman.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;

public class KeyBoardView extends LinearLayout {

    private static final String[] row0 = new String[]{"A", "B", "C", "D", "E", "F", "G"};
    private static final String[] row1 = new String[]{"H", "I", "J", "K", "L", "M"};
    private static final String[] row2 = new String[]{"N", "O", "P", "Q", "R", "S", "T"};
    private static final String[] row3 = new String[]{"U", "V", "W", "X", "Y", "Z"};

    private LayoutInflater layoutInflater;
    private OnKeyClickListener mListener;
    private List<TextView> disabled = new ArrayList<TextView>();

    private OnClickListener mOnClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView keyView = (TextView) v;
            if (!keyView.isEnabled()) return;
            if (mListener != null) {
                if (mListener.onKeyClicked(keyView)) {
                    disableKey(keyView);
                }
            }
        }
    };

    public KeyBoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        layoutInflater = LayoutInflater.from(getContext());
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        addView(createKeyBoardRow(row0), lp);
        addView(createKeyBoardRow(row1), lp);
        addView(createKeyBoardRow(row2), lp);
        addView(createKeyBoardRow(row3), lp);
    }

    private View createKeyBoardRow(String[] row) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(HORIZONTAL);
        for (String key : row) {
            TextView keyView = (TextView) layoutInflater.inflate(R.layout.key, layout, false);
            keyView.setText(key.toUpperCase(Locale.US));
            keyView.setOnClickListener(mOnClick);
            layout.addView(keyView);
        }
        return layout;
    }

    public void setOnKeyClickListener(OnKeyClickListener listener) {
        mListener = listener;
    }

    private void disableKey(TextView keyView) {
        keyView.setEnabled(false);
        disabled.add(keyView);
    }

    public void reset() {
        ButterKnife.apply(disabled, new ButterKnife.Action<TextView>() {
            @Override
            public void apply(TextView view, int index) {
                view.setEnabled(true);
            }
        });
    }

    public interface OnKeyClickListener {
        boolean onKeyClicked(TextView keyView);
    }
}
