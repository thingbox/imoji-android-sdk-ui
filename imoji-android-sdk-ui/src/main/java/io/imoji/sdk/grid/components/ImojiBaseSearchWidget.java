/*
 * Imoji Android SDK UI
 * Created by engind
 *
 * Copyright (C) 2016 Imoji
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KID, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 *
 */

package io.imoji.sdk.grid.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import java.util.List;

import io.imoji.sdk.editor.ImojiEditorActivity;
import io.imoji.sdk.ui.R;
import io.imoji.sdk.grid.components.ImojiSearchResultAdapter.ImojiSearchTapListener;
import io.imoji.sdk.grid.ui.ImojiResultView;
import io.imoji.sdk.grid.ui.ImojiSearchBarLayout;
import io.imoji.sdk.grid.ui.ImojiSearchBarLayout.ImojiSearchBarListener;

/**
 * Created by engind on 4/22/16.
 */
public abstract class ImojiBaseSearchWidget extends LinearLayout implements ImojiSearchBarListener, ImojiSearchTapListener {

    protected ViewSwitcher switcher;
    protected RecyclerView recyclerView;
    protected ImojiSearchBarLayout searchBarLayout;
    protected ImojiSearchResultAdapter resultAdapter;
    protected ImojiSearchHandler searchHandler;
    protected Context context;

    private ImojiWidgetListener widgetListener;
    private GridLayoutManager gridLayoutManager;
    protected ImojiUISDKOptions options;

    private BroadcastReceiver imojiCreatedReceiver;

    public ImojiBaseSearchWidget(Context context, final int spanCount, int orientation, boolean autoSearchEnabled, @ImojiResultView.ResultViewSize int resultViewSize,
                                 ImojiUISDKOptions options, ImojiSearchResultAdapter.ImojiImageLoader imageLoader) {
        super(context);
        inflate(getContext(), R.layout.imoji_base_widget, this);
        this.context = context;
        this.options = options;

        this.searchHandler = new ImojiSearchHandler(autoSearchEnabled) {

            @Override
            public void onSearchCompleted(List<SearchResult> newResults, int dividerPosition, boolean isRecents) {
                repopulateAdapter(newResults, dividerPosition, isRecents);
            }

            @Override
            public void beforeSearchStarted() {
                switcher.setDisplayedChild(0);
            }

            @Override
            public void onHistoryChanged() {
                updateText();
            }
        };

        switcher = (ViewSwitcher) this.findViewById(R.id.widget_switcher);
        recyclerView = (RecyclerView) this.findViewById(R.id.widget_recycler);
        searchBarLayout = (ImojiSearchBarLayout) this.findViewById(R.id.widget_search);
        searchBarLayout.setImojiSearchListener(this);
        searchBarLayout.setActionButtonsVisibility(options.isIncludeRecentsAndCreate());

        resultAdapter = new ImojiSearchResultAdapter(context, imageLoader, resultViewSize, orientation, options);
        resultAdapter.setSearchTapListener(this);
        gridLayoutManager = new GridLayoutManager(context, spanCount, orientation, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (resultAdapter.getItemViewType(position)) {
                    case ImojiSearchResultAdapter.DIVIDER_VIEW_TYPE:
                        return spanCount;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(resultAdapter);

        searchHandler.searchTrending(context);

        imojiCreatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchHandler.searchRecents(context);
                searchBarLayout.showRecentsView();
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
            }
        };
    }

    private void repopulateAdapter(List<SearchResult> newResults, int dividerPosition, boolean isRecents) {
        updateRecyclerView(newResults.size(), isRecents);
        gridLayoutManager.scrollToPositionWithOffset(0, 0);
        resultAdapter.repopulate(newResults, dividerPosition);
    }


    private void updateRecyclerView(int newSize, boolean isRecents) {
        if (newSize == 0) {
            if (switcher.getChildAt(1) != null) {
                switcher.removeViewAt(1);
            }
            getNoStickerView(isRecents);
            switcher.setDisplayedChild(1);
        }
    }

    @Override
    public void onTextSubmit(String term) {
        searchHandler.searchTerm(context, term, null, true);
    }

    @Override
    public void onBackButtonTapped() {
        searchHandler.clearHistory();
        searchHandler.searchTrending(context);
    }

    @Override
    public void onCloseButtonTapped() {
        if (this.widgetListener != null) {
            widgetListener.onCloseButtonTapped();
        }
    }

    @Override
    public void onTextChanged(String term, boolean shouldTriggerAutoSearch) {
        if (shouldTriggerAutoSearch) {
            searchHandler.autoSearch(context, term);
        }
    }

    public void setWidgetListener(ImojiWidgetListener widgetListener) {
        this.widgetListener = widgetListener;
    }

    @Override
    public void onRecentsButtonTapped() {
        searchHandler.searchRecents(context);
    }

    @Override
    public void onCreateButtonTapped() {
        startImojiEditorActivity(context);
    }

    @Override
    public void onTap(SearchResult searchResult) {
        if (searchResult.isCategory()) {
            searchHandler.searchTerm(context, searchResult.getCategory().getIdentifier(),
                    searchResult.getCategory().getTitle(), true);
        } else {
            if (this.widgetListener != null) {
                this.widgetListener.onStickerTapped(searchResult.getImoji());
                searchHandler.addToRecents(context, searchResult.getImoji());
            }
        }
    }

    private void updateText() {
        Pair<String, String> pair = searchHandler.getFirstElement();
        if (pair != null) {
            String text = pair.first;
            if (pair.second != null) {
                text = pair.second;
            }
            searchBarLayout.setText(text);
        } else {
            searchBarLayout.setText("");
        }
    }

    private void startImojiEditorActivity(Context context) {
        Intent intent = new Intent(context, ImojiEditorActivity.class);
        intent.putExtra(ImojiEditorActivity.RETURN_IMMEDIATELY_BUNDLE_ARG_KEY, false);
        intent.putExtra(ImojiEditorActivity.TAG_IMOJI_BUNDLE_ARG_KEY, true);
        LocalBroadcastManager.getInstance(context).registerReceiver(imojiCreatedReceiver,
                new IntentFilter(ImojiEditorActivity.IMOJI_CREATION_FINISHED_BROADCAST_ACTION));
        context.startActivity(intent);
    }

    protected abstract View getNoStickerView(boolean isRecents);
}
