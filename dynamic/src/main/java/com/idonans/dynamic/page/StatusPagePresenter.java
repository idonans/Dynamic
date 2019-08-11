package com.idonans.dynamic.page;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;

import java.util.Collection;

import timber.log.Timber;

public abstract class StatusPagePresenter<E, T extends PageView<E>> extends PagePresenter<E, T> {

    @NonNull
    private final RequestStatus mInitRequestStatus;
    @Nullable
    private final RequestStatus mPrePageRequestStatus;
    @Nullable
    private final RequestStatus mNextPageRequestStatus;

    public StatusPagePresenter(T view, boolean supportPrePageRequest, boolean supportNextPageRequest) {
        super(view);
        mInitRequestStatus = new RequestStatus();
        mPrePageRequestStatus = supportPrePageRequest ? new RequestStatus() : null;
        mNextPageRequestStatus = supportNextPageRequest ? new RequestStatus() : null;
    }

    @Override
    @UiThread
    public void requestInit() {
        requestInit(false);
    }

    @UiThread
    public void requestInit(boolean force) {
        PageView<E> view = getView();
        if (view == null) {
            Timber.e("view is null");
            return;
        }

        if (force || mInitRequestStatus.allowRequest()) {
            mInitRequestStatus.setLoading();
            if (mPrePageRequestStatus != null) {
                mPrePageRequestStatus.reset();
            }
            if (mNextPageRequestStatus != null) {
                mNextPageRequestStatus.reset();
            }
            super.requestInit();
        }
    }

    @CallSuper
    @Override
    @UiThread
    protected void onInitRequestResult(@NonNull PageView<E> view, @NonNull Collection<E> items) {
        mInitRequestStatus.setEnd(items.isEmpty());
        super.onInitRequestResult(view, items);
    }

    @CallSuper
    @Override
    @UiThread
    protected void onInitRequestError(@NonNull PageView<E> view, @NonNull Throwable e) {
        mInitRequestStatus.setError();
        super.onInitRequestError(view, e);
    }

    @Override
    @UiThread
    public void requestPrePage() {
        requestPrePage(false);
    }

    @UiThread
    public void requestPrePage(boolean force) {
        PageView<E> view = getView();
        if (view == null) {
            Timber.e("view is null");
            return;
        }

        if (mPrePageRequestStatus == null) {
            return;
        }

        if (!view.hasPageContent()) {
            return;
        }

        if (force || mPrePageRequestStatus.allowRequest()) {
            mPrePageRequestStatus.setLoading();
            mInitRequestStatus.reset();
            super.requestPrePage();
        }
    }

    @CallSuper
    @Override
    @UiThread
    protected void onPrePageRequestResult(@NonNull PageView<E> view, @NonNull Collection<E> items) {
        if (mPrePageRequestStatus != null) {
            mPrePageRequestStatus.setEnd(items.isEmpty());
        }
        super.onPrePageRequestResult(view, items);
    }

    @CallSuper
    @Override
    @UiThread
    protected void onPrePageRequestError(@NonNull PageView<E> view, @NonNull Throwable e) {
        if (mPrePageRequestStatus != null) {
            mPrePageRequestStatus.setError();
        }
        super.onPrePageRequestError(view, e);
    }

    @Override
    @UiThread
    public void requestNextPage() {
        requestNextPage(false);
    }

    @UiThread
    public void requestNextPage(boolean force) {
        PageView<E> view = getView();
        if (view == null) {
            Timber.e("view is null");
            return;
        }

        if (mNextPageRequestStatus == null) {
            return;
        }

        if (!view.hasPageContent()) {
            return;
        }

        if (force || mNextPageRequestStatus.allowRequest()) {
            mNextPageRequestStatus.setLoading();
            mInitRequestStatus.reset();
            super.requestNextPage();
        }
    }

    @CallSuper
    @Override
    @UiThread
    protected void onNextPageRequestResult(@NonNull PageView<E> view, @NonNull Collection<E> items) {
        if (mNextPageRequestStatus != null) {
            mNextPageRequestStatus.setEnd(items.isEmpty());
        }
        super.onNextPageRequestResult(view, items);
    }

    @CallSuper
    @Override
    @UiThread
    protected void onNextPageRequestError(@NonNull PageView<E> view, @NonNull Throwable e) {
        if (mNextPageRequestStatus != null) {
            mNextPageRequestStatus.setError();
        }
        super.onNextPageRequestError(view, e);
    }

    private static class RequestStatus {
        private boolean mLoading;
        private boolean mError;
        private boolean mEnd;

        void reset() {
            this.mLoading = false;
            this.mError = false;
            this.mEnd = false;
        }

        boolean allowRequest() {
            return !this.mLoading && !this.mError && !this.mEnd;
        }

        void setLoading() {
            mLoading = true;
            mError = false;
            mEnd = false;
        }

        void setError() {
            mLoading = false;
            mError = true;
            mEnd = false;
        }

        void setEnd(boolean end) {
            mLoading = false;
            mError = false;
            mEnd = end;
        }
    }

}
