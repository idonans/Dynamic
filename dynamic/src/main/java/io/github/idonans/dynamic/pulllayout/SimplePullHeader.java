package io.github.idonans.dynamic.pulllayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;

public class SimplePullHeader extends FrameLayout implements PullLayout.Header {

    public SimplePullHeader(@NonNull Context context) {
        this(context, null);
    }

    public SimplePullHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimplePullHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SimplePullHeader(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    }

    @Override
    public void updateOffset(@NonNull PullLayout.OffsetHelper offsetHelper, boolean animating, int windowOffsetX, int windowOffsetY, @NonNull PullLayout pullLayout) {
        setTranslationX(windowOffsetX);
        setTranslationY(windowOffsetY);
    }

}
