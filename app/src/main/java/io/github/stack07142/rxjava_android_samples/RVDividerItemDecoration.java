package io.github.stack07142.rxjava_android_samples;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RVDividerItemDecoration extends RecyclerView.ItemDecoration {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({VERTICAL_ALL, VERTICAL_OUTBOUND_TOP, VERTICAL_OUTBOUND_BOTTOM, VERTICAL_OUTBOUNDS, VERTICAL_FIRST_AND_OUTBOUND_BOTTOM, VERTICAL_SKIP_FIRST, VERTICAL_SKIP_LAST})
    @interface Type {
    }

    public static final int VERTICAL_ALL = 0;
    public static final int VERTICAL_OUTBOUND_TOP = 1;
    public static final int VERTICAL_OUTBOUND_BOTTOM = 2;
    public static final int VERTICAL_OUTBOUNDS = 3;
    public static final int VERTICAL_FIRST_AND_OUTBOUND_BOTTOM = 4;
    public static final int VERTICAL_SKIP_FIRST = 5;
    public static final int VERTICAL_SKIP_LAST = 6;

    private Drawable divider;
    private DisplayMetrics displayMetrics;
    @Type
    private int type;

    private int outboundTopLeftMargin;
    private int outboundTopRightMargin;
    private int outboundBottomLeftMargin;
    private int outboundBottomRightMargin;
    private int innerLeftMargin;
    private int innerRightMargin;

    private RVDividerItemDecoration(Builder builder) {
        displayMetrics = builder.context.getResources().getDisplayMetrics();
        divider = ResourcesCompat.getDrawable(builder.context.getResources(), R.drawable.line_divider, builder.context.getTheme());
        type = builder.type;

        this.outboundTopLeftMargin = getPx(builder.outboundTopLeftMargin);
        this.outboundTopRightMargin = getPx(builder.outboundTopRightMargin);
        this.outboundBottomLeftMargin = getPx(builder.outboundBottomLeftMargin);
        this.outboundBottomRightMargin = getPx(builder.outboundBottomRightMargin);
        this.innerLeftMargin = getPx(builder.innerLeftMargin);
        this.innerRightMargin = getPx(builder.innerRightMargin);
    }

    public static class Builder {
        private Context context;
        @Type
        private int type;
        private int outboundTopLeftMargin = 0;
        private int outboundTopRightMargin = 0;
        private int outboundBottomLeftMargin = 0;
        private int outboundBottomRightMargin = 0;
        private int innerLeftMargin = 0;
        private int innerRightMargin = 0;

        public Builder(Context context, @Type int type) {
            this.context = context;
            this.type = type;
        }

        public Builder setOutboundTopLeftMargin(int margin) {
            this.outboundTopLeftMargin = margin;
            return this;
        }

        public Builder setOutboundTopRightMargin(int margin) {
            this.outboundTopRightMargin = margin;
            return this;
        }

        public Builder setOutboundBottomLeftMargin(int margin) {
            this.outboundBottomLeftMargin = margin;
            return this;
        }

        public Builder setOutboundBottomRightMargin(int margin) {
            this.outboundBottomRightMargin = margin;
            return this;
        }

        public Builder setInnerLeftMargin(int margin) {
            this.innerLeftMargin = margin;
            return this;
        }

        public Builder setInnerRightMargin(int margin) {
            this.innerRightMargin = margin;
            return this;
        }

        public RVDividerItemDecoration build() {
            return new RVDividerItemDecoration(this);
        }
    }

    // 1. Add space to the top of the Recycler View.
    // 2. Add space to the bottom of each item in the Recycler View.
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildLayoutPosition(view);
        int height = divider.getIntrinsicHeight();

        outRect.set(0, position == 0 ? height : 0, 0, height);
    }

    // 3. Draw the divider within the added space.
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        final int height = divider.getIntrinsicHeight();
        final int right = parent.getWidth() - parent.getPaddingRight();

        // 맨 위 아이템 윗 선
        if (type == VERTICAL_ALL || type == VERTICAL_OUTBOUNDS || type == VERTICAL_OUTBOUND_TOP || type == VERTICAL_SKIP_LAST) {
            divider.setBounds(outboundTopLeftMargin, 0, right - outboundTopRightMargin, height);
            divider.draw(c);
        }

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + height;

            if (type == VERTICAL_FIRST_AND_OUTBOUND_BOTTOM) {
                if (i == 0) {
                    divider.setBounds(innerLeftMargin, top, right - innerRightMargin, bottom);
                    divider.draw(c);
                } else if (i == childCount - 1) {
                    divider.setBounds(outboundBottomLeftMargin, top, right - outboundBottomRightMargin, bottom);
                    divider.draw(c);
                }
            } else if (type == VERTICAL_OUTBOUNDS || type == VERTICAL_OUTBOUND_BOTTOM) {
                if (i == childCount - 1) {
                    divider.setBounds(outboundBottomLeftMargin, top, right - outboundBottomRightMargin, bottom);
                    divider.draw(c);
                }
            } else if (type == VERTICAL_SKIP_LAST) {
                if (i != childCount - 1) {
                    divider.setBounds(innerLeftMargin, top, right - innerRightMargin, bottom);
                    divider.draw(c);
                }
            } else if (type == VERTICAL_ALL || type == VERTICAL_SKIP_FIRST) {
                if (i == childCount - 1) {
                    divider.setBounds(outboundBottomLeftMargin, top, right - outboundBottomRightMargin, bottom);
                    divider.draw(c);
                } else {
                    divider.setBounds(innerLeftMargin, top, right - innerRightMargin, bottom);
                    divider.draw(c);
                }
            }
        }
    }

    private int getPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
