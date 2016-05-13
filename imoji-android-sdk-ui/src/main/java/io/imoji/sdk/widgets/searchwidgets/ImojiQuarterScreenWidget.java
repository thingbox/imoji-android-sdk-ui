package io.imoji.sdk.widgets.searchwidgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.imoji.sdk.ui.R;
import io.imoji.sdk.widgets.searchwidgets.components.ImojiBaseSearchWidget;
import io.imoji.sdk.widgets.searchwidgets.components.ImojiSearchResultAdapter;
import io.imoji.sdk.widgets.searchwidgets.components.ImojiUISDKOptions;
import io.imoji.sdk.widgets.searchwidgets.components.SearchResult;
import io.imoji.sdk.widgets.searchwidgets.ui.ImojiResultView;

/**
 * Created by engind on 4/24/16.
 */
public class ImojiQuarterScreenWidget extends ImojiBaseSearchWidget {

    public final static int SPAN_COUNT = 1;

    public ImojiQuarterScreenWidget(Context context, ImojiUISDKOptions options, ImojiSearchResultAdapter.ImojiImageLoader imageLoader) {
        super(context, SPAN_COUNT, HORIZONTAL, true, ImojiResultView.SMALL, options, imageLoader);
        setBackgroundDrawable(getResources().getDrawable(R.drawable.base_widget_separator));

        LinearLayout container = (LinearLayout) this.findViewById(R.id.widget_container);
        container.removeAllViews();
        container.addView(switcher);
        container.addView(searchBarLayout);


        int height = (int) getResources().getDimension(R.dimen.imoji_search_result_row_height);
        switcher.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            private int horizontalPadd = (int) getContext().getResources().getDimension(R.dimen.imoji_search_recycler_horizontal_padding);

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = horizontalPadd;
                outRect.right = horizontalPadd;

                int position = parent.getChildLayoutPosition(view);
                if (position == resultAdapter.getDividerPosition()) {
                    outRect.right = 0;
                } else if (position == 0) {
                    outRect.left = horizontalPadd * 2;
                } else if (position >= state.getItemCount() - SPAN_COUNT) {
                    outRect.right = horizontalPadd * 2;
                }
            }
        });

        searchBarLayout.setRecentsLayout(R.layout.imoji_recents_bar_small);
    }

    @Override
    public void onFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchBarLayout.getFocusedChild(), InputMethodManager.SHOW_IMPLICIT);
            setBarState(true);
        }
    }

    @Override
    public void onTextCleared() {
        Pair pair = searchHandler.getFirstElement();
        if (pair != null && pair.second != null) {
            onBackButtonTapped();
            setBarState(false);
        }
    }

    @Override
    public void onTap(SearchResult searchResult) {
        super.onTap(searchResult);
        if(searchResult.isCategory()){
            setBarState(true);
        }
    }

    private void setBarState(boolean active) {
        searchBarLayout.setActionButtonsVisibility(options.isIncludeRecentsAndCreate() && !active);
    }

    @Override
    protected View getNoStickerView(boolean isRecents) {
        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.imoji_quarter_search_widget_no_result, switcher);

        TextView textView = (TextView) view.findViewById(R.id.replacement_view_text);
        if (isRecents) {
            textView.setText(getContext().getString(R.string.imoji_search_widget_no_recent_hint));
        }
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Montserrat-Regular.otf"));

        return view;
    }
}
