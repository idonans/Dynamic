package com.idonans.dynamic.single;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.idonans.dynamic.LibLog;
import com.idonans.dynamic.LoadingStatusCallback;
import com.idonans.dynamic.LoadingStatusCallbackHost;

import java.util.Collection;

public class StatusSingleView<T> implements SingleView<T> {

    @NonNull
    private StatusSingleViewAdapter<T> mAdapter;
    private boolean mClearContentWhenRequestInit;

    @Nullable
    private SinglePresenter<T, StatusSingleView<T>> mPresenter;

    public StatusSingleView(@NonNull StatusSingleViewAdapter<T> adapter) {
        this(adapter, false);
    }

    public StatusSingleView(@NonNull StatusSingleViewAdapter<T> adapter, boolean clearContentWhenRequestInit) {
        mAdapter = adapter;
        mClearContentWhenRequestInit = clearContentWhenRequestInit;
    }

    public void setPresenter(@Nullable SinglePresenter<T, StatusSingleView<T>> presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean isClearContentWhenRequestInit() {
        return mClearContentWhenRequestInit;
    }

    @Override
    public boolean hasContent() {
        return mAdapter.hasContent();
    }

    @NonNull
    public StatusSingleViewAdapter<T> getAdapter() {
        return mAdapter;
    }

    protected void onClearContent() {
        mAdapter.clearContent();
    }

    @Override
    public void showInitLoading() {
        if (isClearContentWhenRequestInit()) {
            onClearContent();
            mAdapter.showLargeLoading(new Object());
            return;
        }
        boolean hasContent = hasContent();
        if (!hasContent) {
            mAdapter.showLargeLoading(new Object());
        } else {
            mAdapter.showSmallLoading(new Object());
        }
    }

    @Override
    public void hideInitLoading() {
        mAdapter.hideLoading();
    }

    @Override
    public void onInitDataLoad(@NonNull Collection<T> items) {
        mAdapter.showData(items);
    }

    @Override
    public void onInitDataEmpty() {
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        LibLog.e("presenter is null");
                        return;
                    }
                    mPresenter.requestInit(true);
                };
            }

            @Nullable
            @Override
            public Throwable getCause() {
                return null;
            }
        };

        onClearContent();
        mAdapter.showEmptyData(itemObject);
    }

    @Override
    public void onInitDataLoadFail(@NonNull Throwable e) {
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        LibLog.e("presenter is null");
                        return;
                    }
                    mPresenter.requestInit(true);
                };
            }

            @Nullable
            @Override
            public Throwable getCause() {
                return e;
            }
        };

        boolean hasContent = hasContent();
        if (!hasContent) {
            mAdapter.showLargeError(itemObject);
        } else {
            mAdapter.showSmallError(itemObject);
        }
    }

}
