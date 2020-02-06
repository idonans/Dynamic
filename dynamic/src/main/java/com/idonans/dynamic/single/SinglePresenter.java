package com.idonans.dynamic.single;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import com.idonans.dynamic.DynamicPresenter;
import com.idonans.lang.DisposableHolder;

import java.util.Collection;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public abstract class SinglePresenter<E, T extends SingleView<E>> extends DynamicPresenter<T> {

    protected final DisposableHolder mInitRequestHolder = new DisposableHolder();
    protected final DisposableHolder[] mRequestHolders = {mInitRequestHolder};

    @NonNull
    private final RequestStatus mInitRequestStatus;

    public SinglePresenter(T view) {
        super(view);

        mInitRequestStatus = new RequestStatus();
    }

    /**
     * 除了指定的请求外，清除其它请求。
     *
     * @param excepts
     */
    @UiThread
    protected void clearRequestExcept(DisposableHolder... excepts) {
        for (DisposableHolder target : mRequestHolders) {
            boolean matchExcept = false;
            if (excepts != null) {
                for (DisposableHolder except : excepts) {
                    if (target == except) {
                        matchExcept = true;
                        break;
                    }
                }
            }
            if (!matchExcept) {
                if (target != null) {
                    target.clear();
                }
            }
        }
    }

    /**
     * 清除指定请求
     *
     * @param targets
     */
    @UiThread
    protected void clearRequest(DisposableHolder... targets) {
        if (targets != null) {
            for (DisposableHolder target : targets) {
                if (target != null) {
                    target.clear();
                }
            }
        }
    }

    @UiThread
    public void requestInit() {
        requestInit(false);
    }

    /**
     * 请求初始数据。
     * <br/>当发起请求初始数据时，会中断旧的初始数据请求，上一页数据请求和下一页数据请求。
     */
    @UiThread
    public void requestInit(boolean force) {
        Timber.v("requestInit force:%s", force);

        SingleView<E> view = getView();
        if (view == null) {
            Timber.e("view is null");
            return;
        }

        if (!force && !mInitRequestStatus.allowRequest()) {
            return;
        }

        clearRequestExcept();

        mInitRequestStatus.setLoading();
        view.showInitLoading();

        mInitRequestHolder.set(Single.just(this)
                .flatMap((Function<Object, SingleSource<Collection<E>>>) object -> createInitRequest())
                .map(Objects::requireNonNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    SingleView<E> innerView = getView();
                    if (innerView == null) {
                        Timber.e("view is null");
                        return;
                    }

                    mInitRequestStatus.setEnd(items.isEmpty());
                    innerView.hideInitLoading();

                    onInitRequestResult(innerView, items);
                }, e -> {
                    SingleView<E> innerView = getView();
                    if (innerView == null) {
                        Timber.e("view is null");
                        return;
                    }

                    mInitRequestStatus.setError();
                    innerView.hideInitLoading();

                    onInitRequestError(innerView, e);
                }));
    }

    @WorkerThread
    @Nullable
    protected abstract SingleSource<Collection<E>> createInitRequest() throws Exception;

    @CallSuper
    @UiThread
    protected void onInitRequestResult(@NonNull SingleView<E> view, @NonNull Collection<E> items) {
        Timber.v("onInitRequestResult %s items", items.size());
        view.onInitDataLoad(items);
        if (items.isEmpty()) {
            view.onInitDataEmpty();
        }
    }

    @CallSuper
    @UiThread
    protected void onInitRequestError(@NonNull SingleView<E> view, @NonNull Throwable e) {
        Timber.e(e, "onInitRequestError");
        view.onInitDataLoadFail(e);
    }

    private static class RequestStatus {

        private boolean mLoading;
        private boolean mError;
        private boolean mEnd;

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
