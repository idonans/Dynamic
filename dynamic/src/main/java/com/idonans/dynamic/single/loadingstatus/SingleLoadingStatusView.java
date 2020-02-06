package com.idonans.dynamic.single.loadingstatus;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.idonans.dynamic.R;
import com.idonans.dynamic.single.StatusSingleViewAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SingleLoadingStatusView<T> extends FrameLayout implements StatusSingleViewAdapter<T> {

    @LayoutRes
    private int mLayoutResId = R.layout.dynamic_single_loading_status_view;

    @NonNull
    private ViewGroup mSmallViewParent;
    @NonNull
    private ViewGroup mContentViewParent;

    @Nullable
    private View mLoadingLargeView;
    @Nullable
    private View mLoadingSmallView;
    @Nullable
    private View mLoadFailLargeView;
    @Nullable
    private View mLoadFailSmallView;
    @Nullable
    private View mEmptyDataView;
    @Nullable
    private View mDataView;

    @NonNull
    private SingleLoadingStatus<T> mSingleLoadingStatus;

    public SingleLoadingStatusView(@NonNull Context context) {
        this(context, null);
    }

    public SingleLoadingStatusView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleLoadingStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SingleLoadingStatusView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initFromAttributes(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SingleLoadingStatusView, defStyleAttr,
                defStyleRes);
        mLayoutResId = a.getResourceId(R.styleable.SingleLoadingStatusView_custom_layout, mLayoutResId);
        a.recycle();

        LayoutInflater.from(context).inflate(mLayoutResId, this, true);

        mSmallViewParent = findViewById(R.id.small_view_parent);
        mContentViewParent = findViewById(R.id.content_view_parent);

        Preconditions.checkNotNull(mSmallViewParent);
        Preconditions.checkNotNull(mContentViewParent);

        mSingleLoadingStatus = new SingleLoadingStatus<T>() {
            @Override
            public View createDataView(ViewGroup parent, T data) {
                return null;
            }
        };
    }

    public void setSingleLoadingStatus(@NonNull SingleLoadingStatus<T> singleLoadingStatus) {
        mSingleLoadingStatus = singleLoadingStatus;
    }

    @Override
    public boolean hasContent() {
        return mDataView != null && mDataView.getVisibility() == View.VISIBLE;
    }

    @Override
    public void showLargeLoading(Object object) {
        clearView(true);

        mLoadingLargeView = mSingleLoadingStatus.createLoadingLargeView(this, object);
        if (mLoadingLargeView != null) {
            addView(mLoadingLargeView);
        }
    }

    @Override
    public void showSmallLoading(Object object) {
        clearView(false);

        mLoadingSmallView = mSingleLoadingStatus.createLoadingSmallView(this, object);
        if (mLoadingSmallView != null) {
            mSmallViewParent.addView(mLoadingSmallView);
        }
    }

    @Override
    public void hideLoading() {
        removeViewFromParent(mLoadingLargeView);
        mLoadingLargeView = null;

        removeViewFromParent(mLoadingSmallView);
        mLoadingSmallView = null;
    }

    @Override
    public void showData(@NonNull Collection<T> items) {
        clearView(true);

        List<T> array = new ArrayList<>(items);
        if (array.size() > 0) {
            T object = array.get(0);
            Preconditions.checkNotNull(object);
            mDataView = mSingleLoadingStatus.createDataView(this, object);
            if (mDataView != null) {
                mContentViewParent.addView(mDataView);
            }
        }
    }

    @Override
    public void showEmptyData(Object object) {
        clearView(true);

        mEmptyDataView = mSingleLoadingStatus.createEmptyDataView(this, object);
        addView(mEmptyDataView);
    }

    @Override
    public void showLargeError(Object object) {
        clearView(true);

        mLoadFailLargeView = mSingleLoadingStatus.createLoadFailLargeView(this, object);
        if (mLoadFailLargeView != null) {
            addView(mLoadFailLargeView);
        }
    }

    @Override
    public void showSmallError(Object object) {
        clearView(false);

        mLoadFailSmallView = mSingleLoadingStatus.createLoadFailSmallView(this, object);
        if (mLoadFailSmallView != null) {
            mSmallViewParent.addView(mLoadFailSmallView);
        }
    }

    @Override
    public void clearContent() {
        removeViewFromParent(mDataView);
        mDataView = null;
    }

    private void clearView(boolean includeDataView) {
        removeViewFromParent(mLoadingLargeView);
        mLoadingLargeView = null;

        removeViewFromParent(mLoadingSmallView);
        mLoadingSmallView = null;

        removeViewFromParent(mLoadFailLargeView);
        mLoadFailLargeView = null;

        removeViewFromParent(mLoadFailSmallView);
        mLoadFailSmallView = null;

        removeViewFromParent(mEmptyDataView);
        mEmptyDataView = null;

        if (includeDataView) {
            removeViewFromParent(mDataView);
            mDataView = null;
        }
    }

    private void removeViewFromParent(View view) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(view);

                if (view == mLoadingLargeView) {
                    mSingleLoadingStatus.onLoadingLargeViewRemoved(view);
                } else if (view == mLoadingSmallView) {
                    mSingleLoadingStatus.onLoadingSmallViewRemoved(view);
                } else if (view == mEmptyDataView) {
                    mSingleLoadingStatus.onEmptyDataViewRemoved(view);
                } else if (view == mLoadFailLargeView) {
                    mSingleLoadingStatus.onLoadFailLargeViewRemoved(view);
                } else if (view == mLoadFailSmallView) {
                    mSingleLoadingStatus.onLoadFailSmallViewRemoved(view);
                } else if (view == mDataView) {
                    mSingleLoadingStatus.onDataViewRemoved(view);
                }

                mSingleLoadingStatus.onViewRemoved(view);
            }
        }
    }

}
