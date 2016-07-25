package com.google.face2face;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/**
 * How to use:
 *   In your fragment's onCreateView:
 *   <ul>
 *     <li>
 *       Call {@link #addItem} for every item you want. Each item
 *       must currently be a {@link ToggleButton}.
 *     </li>
 *     <li>
 *       Call {@link #setMaxItems} to set the maximum selected items (default is 1).
 *     </li>
 *     <li>
 *       Call {@link #checkItemByName} for every item you want to initially be checked.
 *     </li>
 *   </ul>
 *
 *   When you're done, call {@link #getCheckedNames()} to retrieve the checked items.
 */
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
        // Note: it seems the code assumed left is 0 (as childLeft doesn't include it).
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int lineHeight = 0;
        int myWidth = right - left;
        List<View> rowItems = new ArrayList<>();
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
                alignChildren(rowItems, right - left);
                rowItems.clear();
            }
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            rowItems.add(child);
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + paddingHorizontal;
        }
        if (rowItems.size() > 0) {
            alignChildren(rowItems, right - left);
        }
    }

    private void alignChildren(List<View> children, int width) {
        View lastChild = children.get(children.size() - 1);
        int paddingEnd = width - getPaddingRight() - lastChild.getRight();
        int shift = paddingEnd / 2;
        if (paddingEnd > 0) {
            for (View child : children) {
                child.layout(child.getLeft() + shift, child.getTop(), child.getRight() + shift,
                        child.getBottom());
            }
        }
    }

    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    public void addItemAndCheck(ToggleButton button, boolean isChecked) {
        if (isChecked) {
            button.setChecked(true);
            nChecked++;
        }
        addItem(button);
    }

    public void addItem(ToggleButton button) {
        this.addView(button,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(this);
    }

    public void checkItemByName(String name) {
        if (nChecked >= maxItems) {
            return;  // Fail safe - don't allow checking too name items.
        }
        int count = this.getChildCount();
        for (int i = 0; i < count; ++i) {
            ToggleButton button = (ToggleButton)this.getChildAt(i);
            if (button.getText().equals(name)) {
                button.setChecked(true);
                nChecked++;
                return;
            }
        }
    }

    @Override
    public void onClick(View view) {
        ToggleButton button = (ToggleButton) view;
        boolean isChecked = button.isChecked();
        if (!isChecked) {
            nChecked--;
        } else if (nChecked == maxItems) {
            button.setChecked(false);
            Toast.makeText(getContext(), String.format(getContext().getString(R.string.itemlimit), maxItems), Toast.LENGTH_SHORT).show();
        } else {
            nChecked++;
        }
    }

    public String[] getCheckedNames() {
        String[] result = new String [nChecked];
        int resultInd = 0;
        int count = this.getChildCount();
        for (int i = 0; i < count; ++i) {
            ToggleButton button = (ToggleButton)this.getChildAt(i);
            if (button.isChecked()) {
                result[resultInd++] = "" + button.getText();  // Note: make a copy of the string
            }
        }
        return result;
    }
}