package com.ilp.ilpschedule.item_layouts;

/**
 * Created by 1007546 on 18-10-2016.
 */

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ScheduleItemLayout extends LinearLayout
        implements WearableListView.OnCenterProximityListener {

    private static final float NO_ALPHA = 1f, PARTIAL_ALPHA = 0.40f;
    private static final float NO_X_TRANSLATION = 0f, X_TRANSLATION = 20f;


    public ScheduleItemLayout(Context context) {
        this(context, null);
    }

    public ScheduleItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleItemLayout(Context context, AttributeSet attrs,
                              int defStyle) {
        super(context, attrs, defStyle);


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    public void onCenterPosition(boolean animate) {
        if (animate) {
            animate().alpha(NO_ALPHA).translationX(X_TRANSLATION).start();
        }

    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        if (animate) {
            animate().alpha(PARTIAL_ALPHA).translationX(NO_X_TRANSLATION).start();
        }

    }
}