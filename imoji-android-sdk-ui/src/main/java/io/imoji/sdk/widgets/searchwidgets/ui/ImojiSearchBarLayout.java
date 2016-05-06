package io.imoji.sdk.widgets.searchwidgets.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.imoji.sdk.ui.R;

/**
 * Created by engind on 4/21/16.
 */
public class ImojiSearchBarLayout extends RelativeLayout {

    private View firstLeftIcon;
    private View rightIcon;
    private ImojiEditText textBox;

    private ImojiSearchBarListener imojiSearchBarListener;
    private boolean shouldTriggerAutoSearch = true;

    public ImojiSearchBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.imoji_search_bar, this);

        firstLeftIcon = this.findViewById(R.id.search_bar_first_left_icon);
        textBox = (ImojiEditText) this.findViewById(R.id.search_bar_text_box);
        rightIcon = this.findViewById(R.id.search_bar_right_icon);

        textBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && s.length() > 0) {
                    rightIcon.setVisibility(VISIBLE);
                } else if (before > 0 && s.length() == 0) {
                    rightIcon.setVisibility(GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                imojiSearchBarListener.onTextChanged(s.toString(),shouldTriggerAutoSearch);
            }
        });

        textBox.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                imojiSearchBarListener.onFocusChanged(hasFocus);
            }
        });

        textBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE ||
                        (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN)) {
                    if (imojiSearchBarListener != null) {
                        imojiSearchBarListener.onTextSubmit(textBox.getText().toString());
                    }
                    textBox.clearFocus();
                }
                return true;
            }
        });

        rightIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                textBox.setText("");
                textBox.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(textBox, InputMethodManager.SHOW_IMPLICIT);
                imojiSearchBarListener.onTextCleared();
            }
        });
        setupBackButton();
    }

    public void requestTextFocus() {
        textBox.requestFocus();
    }

    public void setImojiSearchListener(ImojiSearchBarListener searchListener) {
        this.imojiSearchBarListener = searchListener;
    }

    public void setupCloseButton() {
        firstLeftIcon.setBackgroundResource(R.drawable.imoji_close);
        firstLeftIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imojiSearchBarListener != null) {
                    imojiSearchBarListener.onCloseButtonTapped();
                }
            }
        });
    }

    public void setupBackButton() {
        firstLeftIcon.setBackgroundResource(R.drawable.imoji_back);
        firstLeftIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imojiSearchBarListener != null) {
                    imojiSearchBarListener.onBackButtonTapped();
                }
            }
        });
    }

    public void setLeftButtonVisibility(int visibility) {
        firstLeftIcon.setVisibility(visibility);
    }

    public void setText(String text) {
        shouldTriggerAutoSearch = false;
        textBox.setText(text);
        shouldTriggerAutoSearch = true;
        textBox.clearFocus();
    }

    public interface ImojiSearchBarListener {

        void onTextSubmit(String term);

        void onTextCleared();

        void onBackButtonTapped();

        void onCloseButtonTapped();

        void onFocusChanged(boolean hasFocus);

        void onTextChanged(String term, boolean shouldTriggerAutoSearch);
    }

}
