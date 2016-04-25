package io.imoji.sdk.widgets.searchwidgets;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import io.imoji.sdk.ui.R;
import io.imoji.sdk.widgets.searchwidgets.components.ImojiBaseSearchWidget;

/**
 * Created by engind on 4/24/16.
 */
public class ImojiFullScreenWidget extends ImojiBaseSearchWidget {

    private final static int SPAN_COUNT = 4;

    public ImojiFullScreenWidget(Context context) {
        super(context, SPAN_COUNT, VERTICAL, true);
        LinearLayout container = (LinearLayout) this.findViewById(R.id.widget_container);
        container.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        recyclerView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(context) {

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int resultSize = (int) getResources().getDimension(R.dimen.imoji_search_result_width);
                int padding = (parent.getWidth() - resultSize * SPAN_COUNT) / (SPAN_COUNT*2);
                outRect.left = padding;
                outRect.right = padding;
            }
        });
    }


}
