package com.google.face2face;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

public class FlowLayout extends ViewGroup implements View.OnClickListener {

    private int paddingHorizontal;
    private int paddingVertical;

    private int maxItems = 1;
    private int nChecked = 0;

    public FlowLayout(Context context) {
        super(context);
        init();
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paddingHorizontal = getResources().getDimensionPixelSize(R.dimen.flowlayout_horizontal_padding);
        paddingVertical = getResources().getDimensionPixelSize(R.dimen.flowlayout_vertical_padding);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int lineHeight = 0;
        // 100 is a dummy number, widthMeasureSpec should always be EXACTLY for FlowLayout
        int myWidth = resolveSize(100, widthMeasureSpec);
        int wantedHeight = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            // let the child measure itself
            child.measure(
                    getChildMeasureSpec(widthMeasureSpec, 0, child.getLayoutParams().width),
                    getChildMeasureSpec(heightMeasureSpec, 0, child.getLayoutParams().height));
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            // lineheight is the height of current line, should be the height of the heightest view
            lineHeight = Math.max(childHeight, lineHeight);
            if (childWidth + childLeft + getPaddingRight() > myWidth) {
                // wrap this line
                childLeft = getPaddingLeft();
                childTop += paddingVertical + lineHeight;
                lineHeight = childHeight;
            }
            childLeft += childWidth + paddingHorizontal;
        }
        wantedHeight += childTop + lineHeight + getPaddingBottom();
        setMeasuredDimension(myWidth, resolveSize(wantedHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int lineHeight = 0;
        int myWidth = right - left;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            lineHeight = Math.max(childHeight, lineHeight);
            if (childWidth + childLeft + getPaddingRight() > myWidth) {
                childLeft = getPaddingLeft();
                childTop += paddingVertical + lineHeight;
                lineHeight = childHeight;
            }
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + paddingHorizontal;
        }
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public void addItem(ToggleButton button) {
        this.addView(button,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        ToggleButton button = (ToggleButton) view;
        boolean isChecked = button.isChecked();
        if (!isChecked) {
            nChecked--;
        } else if (nChecked == maxItems) {
            button.setChecked(false);
        } else {
            nChecked++;
        }

    }
}