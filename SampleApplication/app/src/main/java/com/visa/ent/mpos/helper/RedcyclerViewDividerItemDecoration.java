package com.visa.ent.mpos.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.visa.ent.mpos.R;

/*
 * Copyright © 2016 CyberSource. All rights reserved.
 */

/**
 * Created by CyberSource on 5/4/2015.
 */
public class RedcyclerViewDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public RedcyclerViewDividerItemDecoration(Context context) {
        mDivider = context.getResources().getDrawable(R.drawable.recycler_view_line_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
