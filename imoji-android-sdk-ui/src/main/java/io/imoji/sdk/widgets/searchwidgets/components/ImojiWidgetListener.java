package io.imoji.sdk.widgets.searchwidgets.components;

import android.net.Uri;

import io.imoji.sdk.objects.Category;

/**
 * Created by engind on 4/27/16.
 */
public interface ImojiWidgetListener {

    void onBackButtonTapped();

    void onCloseButtonTapped();

    void onCategoryTapped(Category category);

    void onStickerTapped(Uri uri);

    void onTermSearched(String term);
}