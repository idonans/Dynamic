package io.github.idonans.dynamic.single.loadingstatus;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Preconditions;
import io.github.idonans.core.thread.Threads;
import io.github.idonans.dynamic.R;
import io.github.idonans.dynamic.single.StatusSingleViewAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SingleLoadingStatusView<T> extends FrameLayout implements StatusSingleViewAdapter<T> {

    @LayoutRes
    private int mLayoutResId = R.layout.dynamic_single_loading_status_view;

    @NonNull
    private ViewGroup mDefaultViewParent;

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

        mDefaultViewParent = findViewById(R.id.default_view_parent);
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

    @Nullable
    public View getLoadingLargeView() {
        return mLoadingLargeView;
    }

    @Nullable
    public View getLoadingSmallView() {
        return mLoadingSmallView;
    }

    @Nullable
    public View getLoadFailLargeView() {
        return mLoadFailLargeView;
    }

    @Nullable
    public View getLoadFailSmallView() {
        return mLoadFailSmallView;
    }

    @Nullable
    public View getEmptyDataView() {
        return mEmptyDataView;
    }

    @Nullable
    public View getDataView() {
        return mDataView;
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
            mDefaultViewParent.addView(mLoadingLargeView);
            startStatusViewShowAnimation(mLoadingLargeView, null);
        }
    }

    @Override
    public void showSmallLoading(Object object) {
        clearView(false);

        mLoadingSmallView = mSingleLoadingStatus.createLoadingSmallView(this, object);
        if (mLoadingSmallView != null) {
            mSmallViewParent.addView(mLoadingSmallView);
            startStatusViewShowAnimation(mLoadingSmallView, null);
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
                startStatusViewShowAnimation(mDataView, null);
            }
        }
    }

    @Override
    public void showEmptyData(Object object) {
        clearView(true);

        mEmptyDataView = mSingleLoadingStatus.createEmptyDataView(this, object);
        if (mEmptyDataView != null) {
            mDefaultViewParent.addView(mEmptyDataView);
            startStatusViewShowAnimation(mEmptyDataView, null);
        }
    }

    @Override
    public void showLargeError(Object object) {
        clearView(true);

        mLoadFailLargeView = mSingleLoadingStatus.createLoadFailLargeView(this, object);
        if (mLoadFailLargeView != null) {
            mDefaultViewParent.addView(mLoadFailLargeView);
            startStatusViewShowAnimation(mLoadFailLargeView, null);
        }
    }

    @Override
    public void showSmallError(Object object) {
        clearView(false);

        mLoadFailSmallView = mSingleLoadingStatus.createLoadFailSmallView(this, object);
        if (mLoadFailSmallView != null) {
            mSmallViewParent.addView(mLoadFailSmallView);
            startStatusViewShowAnimation(mLoadFailSmallView, null);
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

    private void removeViewFromParent(@Nullable final View view) {
        if (view != null) {
            final boolean isLoadingLargeView = view == mLoadingLargeView;
            final boolean isLoadingSmallView = view == mLoadingSmallView;
            final boolean isEmptyDataView = view == mEmptyDataView;
            final boolean isLoadFailLargeView = view == mLoadFailLargeView;
            final boolean isLoadFailSmallView = view == mLoadFailSmallView;
            final boolean isDataView = view == mDataView;

            startStatusViewHideAnimation(view, (statusView, end) -> {
                ViewParent parent = view.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(view);

                    if (isLoadingLargeView) {
                        mSingleLoadingStatus.onLoadingLargeViewRemoved(view);
                    } else if (isLoadingSmallView) {
                        mSingleLoadingStatus.onLoadingSmallViewRemoved(view);
                    } else if (isEmptyDataView) {
                        mSingleLoadingStatus.onEmptyDataViewRemoved(view);
                    } else if (isLoadFailLargeView) {
                        mSingleLoadingStatus.onLoadFailLargeViewRemoved(view);
                    } else if (isLoadFailSmallView) {
                        mSingleLoadingStatus.onLoadFailSmallViewRemoved(view);
                    } else if (isDataView) {
                        mSingleLoadingStatus.onDataViewRemoved(view);
                    }

                    mSingleLoadingStatus.onViewRemoved(view);
                }
            });
        }
    }

    /**
     * 监听动画执行结束或者动画不存在
     */
    public interface OnAnimationEndOrNoneListener {
        /**
         * @param end true 表示动画执行结束，false 表示动画不存在
         */
        void onAnimationEndOrNone(@NonNull View statusView, boolean end);
    }

    protected void startStatusViewShowAnimation(@NonNull final View statusView, @Nullable final OnAnimationEndOrNoneListener listener) {
        statusView.clearAnimation();
        final Animation animation = mSingleLoadingStatus.getStatusViewShowAnimation(statusView);
        if (animation != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // ignore
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Threads.postUi(() -> {
                        if (listener != null) {
                            listener.onAnimationEndOrNone(statusView, true);
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // ignore
                }
            });
            statusView.startAnimation(animation);
        } else {
            if (listener != null) {
                listener.onAnimationEndOrNone(statusView, false);
            }
        }
    }

    protected void startStatusViewHideAnimation(@NonNull final View statusView, @Nullable final OnAnimationEndOrNoneListener listener) {
        statusView.clearAnimation();
        final Animation animation = mSingleLoadingStatus.getStatusViewHideAnimation(statusView);
        if (animation != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // ignore
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    Threads.postUi(() -> {
                        if (listener != null) {
                            listener.onAnimationEndOrNone(statusView, true);
                        }
                    });
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // ignore
                }
            });
            statusView.startAnimation(animation);
        } else {
            if (listener != null) {
                listener.onAnimationEndOrNone(statusView, false);
            }
        }
    }

}
