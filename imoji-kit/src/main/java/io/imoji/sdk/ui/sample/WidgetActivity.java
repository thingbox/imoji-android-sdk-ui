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

package io.imoji.sdk.ui.sample;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import io.imoji.sdk.objects.Imoji;
import io.imoji.sdk.objects.RenderingOptions;
import io.imoji.sdk.grid.FullScreenWidget;
import io.imoji.sdk.grid.HalfScreenWidget;
import io.imoji.sdk.grid.QuarterScreenWidget;
import io.imoji.sdk.grid.components.BaseSearchWidget;
import io.imoji.sdk.grid.components.SearchResultAdapter;
import io.imoji.sdk.grid.components.WidgetDisplayOptions;
import io.imoji.sdk.grid.components.WidgetListener;


public class WidgetActivity extends AppCompatActivity {

    public static final String WIDGET_IDENTIFIER = "widget_identifier";

    private BaseSearchWidget widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                getResources().getColor(R.color.widget_activity_action_bar_color)
        ));
        setContentView(R.layout.activity_widget);

        RelativeLayout container = (RelativeLayout) findViewById(R.id.widget_main_view);

        int identifier = getIntent().getIntExtra(WIDGET_IDENTIFIER, 1);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        SearchResultAdapter.ImageLoader imageLoader = new SearchResultAdapter.ImageLoader() {
            @Override
            public void loadImage(@NonNull ImageView target, @NonNull Uri uri, @NonNull final SearchResultAdapter.ImageLoaderCallback callback) {
                Ion.with(target)
                        .load(uri.toString())
                        .setCallback(new FutureCallback<ImageView>() {
                            @Override
                            public void onCompleted(Exception e, ImageView result) {
                                callback.updateImageView();
                            }
                        });
            }
        };


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        RenderingOptions renderingOptions = new RenderingOptions(
                preferences.getBoolean(getString(R.string.pref_key_sticker_borders_enabled), true) ? RenderingOptions.BorderStyle.Sticker : RenderingOptions.BorderStyle.None,
                RenderingOptions.ImageFormat.Png,
                RenderingOptions.Size.Thumbnail
        );
        WidgetDisplayOptions options = new WidgetDisplayOptions(renderingOptions);
        options.setIncludeRecentsAndCreate(preferences.getBoolean(getString(R.string.pref_key_recents_create_enabled), true));

        switch (identifier) {
            case 0:
                widget = new QuarterScreenWidget(this, options, imageLoader);
                setTitle(R.string.activity_title_quarter_screen);
                container.addView(widget, params);
                break;
            case 1:
                widget = new HalfScreenWidget(this, options, imageLoader);
                setTitle(R.string.activity_title_half_screen);
                container.addView(widget, params);
                break;
            case 2:
                widget = new FullScreenWidget(this, options, imageLoader);
                getSupportActionBar().hide();
                container.addView(widget);
                widget.setWidgetListener(new WidgetListener() {
                    @Override
                    public void onCloseButtonTapped() {
                        NavUtils.navigateUpFromSameTask(WidgetActivity.this);
                    }

                    @Override
                    public void onStickerTapped(Imoji imoji) {

                    }
                });
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
