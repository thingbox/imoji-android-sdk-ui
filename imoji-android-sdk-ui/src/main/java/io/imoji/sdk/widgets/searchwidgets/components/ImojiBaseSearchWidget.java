package io.imoji.sdk.widgets.searchwidgets.components;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import io.imoji.sdk.objects.RenderingOptions;
import io.imoji.sdk.ui.R;
import io.imoji.sdk.widgets.searchwidgets.components.ImojiSearchResultAdapter.ImojiSearchTapListener;
import io.imoji.sdk.widgets.searchwidgets.ui.ImojiResultView;
import io.imoji.sdk.widgets.searchwidgets.ui.ImojiSearchBarLayout;
import io.imoji.sdk.widgets.searchwidgets.ui.ImojiSearchBarLayout.ImojiSearchBarListener;

/**
 * Created by engind on 4/22/16.
 */
public class ImojiBaseSearchWidget extends LinearLayout implements ImojiSearchBarListener, ImojiSearchTapListener {

    protected RecyclerView recyclerView;
    protected ImojiSearchBarLayout searchBarLayout;
    protected ImojiSearchResultAdapter resultAdapter;
    protected ImojiSearchHandler searchHandler;
    protected Context context;

    private ImojiWidgetListener widgetListener;
    private GridLayoutManager gridLayoutManager;
    private View separator;
    private RenderingOptions.ImageFormat imageFormat = RenderingOptions.ImageFormat.WebP;


    public ImojiBaseSearchWidget(Context context, final int spanCount, int orientation,
                                 boolean searchOnTop, boolean autoSearchEnabled, @ImojiResultView.ResultViewSize int resultViewSize,
                                 RenderingOptions.ImageFormat imageFormat, ImojiSearchResultAdapter.ImojiImageLoader imageLoader) {
        super(context);
        inflate(getContext(), R.layout.imoji_base_widget, this);
        this.imageFormat = imageFormat;
        this.context = context;

        this.searchHandler = new ImojiSearchHandler(autoSearchEnabled) {

            @Override
            public void onSearchCompleted(List<SearchResult> newResults, int dividerPosition) {
                repopulateAdapter(newResults, dividerPosition);
            }

            @Override
            public void onFirstHistoryItemAdded() {
                onHistoryCreated();
            }

            @Override
            public void onLastHistoryItemRemoved() {
                onHistoryDestroyed();
            }

            @Override
            public void onHistoryChanged() {
                updateText();
            }
        };

        recyclerView = (RecyclerView) this.findViewById(R.id.widget_recycler);
        recyclerView.setItemAnimator(null);
        searchBarLayout = (ImojiSearchBarLayout) this.findViewById(R.id.widget_search);
        separator = this.findViewById(R.id.sticker_separator);
        searchBarLayout.setImojiSearchListener(this);

        if (searchOnTop) {
            LinearLayout container = (LinearLayout) this.findViewById(R.id.widget_container);
            container.removeAllViews();
            container.addView(searchBarLayout, 0);
            container.addView(separator, 1);
            container.addView(recyclerView, 2);
        }

        resultAdapter = new ImojiSearchResultAdapter(context, imageLoader, resultViewSize, orientation);
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
        recyclerView.setAdapter(resultAdapter);

        searchHandler.searchTrending(context);
    }

    private void repopulateAdapter(List<SearchResult> newResults, int dividerPosition) {
        gridLayoutManager.scrollToPositionWithOffset(0, 0);
        resultAdapter.repopulate(newResults, dividerPosition);
    }

    @Override
    public void onTextSubmit(String term) {
        searchHandler.searchTerm(context, term, null, true);
        if (this.widgetListener != null) {
            widgetListener.onTermSearched(term);
        }
    }

    @Override
    public void onTextCleared() {

    }

    @Override
    public void onBackButtonTapped() {
        searchHandler.searchPrevious(context);
        if (this.widgetListener != null) {
            widgetListener.onBackButtonTapped();
        }
    }

    @Override
    public void onCloseButtonTapped() {
        if (this.widgetListener != null) {
            widgetListener.onCloseButtonTapped();
        }
    }

    @Override
    public void onFocusChanged(boolean hasFocus) {

    }

    @Override
    public void onTextChanged(String term) {
        searchHandler.autoSearch(context,term);
    }

    public void setWidgetListener(ImojiWidgetListener widgetListener) {
        this.widgetListener = widgetListener;
    }

    @Override
    public void onTap(SearchResult searchResult) {
        if (searchResult.isCategory()) {
            searchHandler.searchTerm(context, searchResult.getCategory().getIdentifier(),
                    searchResult.getCategory().getTitle(), true);
            if (this.widgetListener != null) {
                this.widgetListener.onCategoryTapped(searchResult.getCategory());
            }
        } else {
            if (this.widgetListener != null) {
                this.widgetListener.onStickerTapped(searchResult.getUri(imageFormat));
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

    protected void setSeparatorVisibility(int visibility) {
        separator.setVisibility(visibility);
    }

    protected void onHistoryCreated() {

    }

    protected void onHistoryDestroyed() {

    }
}
