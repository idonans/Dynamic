package io.github.idonans.dynamic.page;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import java.util.Collection;
import java.util.Objects;

import io.github.idonans.dynamic.DynamicLog;
import io.github.idonans.dynamic.DynamicPresenter;
import io.github.idonans.lang.DisposableHolder;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 分页数据请求由 请求初始数据+请求上一页数据+请求下一页数据 组成。
 */
public abstract class PagePresenter<E, T extends PageView<E>> extends DynamicPresenter<T> {

    protected final DisposableHolder mInitRequestHolder = new DisposableHolder();
    protected final DisposableHolder mPrePageRequestHolder = new DisposableHolder();
    protected final DisposableHolder mNextPageRequestHolder = new DisposableHolder();
    protected final DisposableHolder[] mRequestHolders = {mInitRequestHolder, mPrePageRequestHolder, mNextPageRequestHolder};

    @NonNull
    private final RequestStatus mInitRequestStatus;
    @Nullable
    private final RequestStatus mPrePageRequestStatus;
    @Nullable
    private final RequestStatus mNextPageRequestStatus;

    public PagePresenter(T view, boolean supportPrePageRequest, boolean supportNextPageRequest) {
        super(view);

        mInitRequestStatus = new RequestStatus();
        mPrePageRequestStatus = supportPrePageRequest ? new RequestStatus() : null;
        mNextPageRequestStatus = supportNextPageRequest ? new RequestStatus() : null;
    }

    @NonNull
    public RequestStatus getInitRequestStatus() {
        return mInitRequestStatus;
    }

    @Nullable
    public RequestStatus getPrePageRequestStatus() {
        return mPrePageRequestStatus;
    }

    @Nullable
    public RequestStatus getNextPageRequestStatus() {
        return mNextPageRequestStatus;
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
        DynamicLog.v("requestInit force:%s", force);

        PageView<E> view = getView();
        if (view == null) {
            DynamicLog.e("view is null");
            return;
        }

        if (!force && !mInitRequestStatus.allowRequest()) {
            return;
        }

        clearRequestExcept();

        final boolean clearPageContent = view.isClearPageContentWhenRequestInit();
        final boolean hasPageContent = view.hasPageContent();

        if (clearPageContent) {
            if (mPrePageRequestStatus != null) {
                mPrePageRequestStatus.reset();
                view.hidePrePageLoading();
            }
            if (mNextPageRequestStatus != null) {
                mNextPageRequestStatus.reset();
                view.hideNextPageLoading();
            }
        } else {
            if (mPrePageRequestStatus != null) {
                if (mPrePageRequestStatus.mLoading
                        || (hasPageContent && mPrePageRequestStatus.allowRequest())) {
                    mPrePageRequestStatus.setManualToLoad();
                    view.showPrePageManualToLoadMore();
                }
            }
            if (mNextPageRequestStatus != null) {
                if (mNextPageRequestStatus.mLoading
                        || (hasPageContent && mNextPageRequestStatus.allowRequest())) {
                    mNextPageRequestStatus.setManualToLoad();
                    view.showNextPageManualToLoadMore();
                }
            }
        }

        mInitRequestStatus.setLoading();
        view.showInitLoading();

        mInitRequestHolder.set(Single.just(this)
                .flatMap((Function<Object, SingleSource<Collection<E>>>) object -> createInitRequest())
                .map(Objects::requireNonNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    if (mPrePageRequestStatus != null) {
                        mPrePageRequestStatus.reset();
                    }

                    if (mNextPageRequestStatus != null) {
                        mNextPageRequestStatus.reset();
                    }

                    mInitRequestStatus.setEnd(items.isEmpty());
                    innerView.hideInitLoading();

                    onInitRequestResult(innerView, items);
                }, e -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    if (mPrePageRequestStatus != null) {
                        mPrePageRequestStatus.reset();
                    }

                    if (mNextPageRequestStatus != null) {
                        mNextPageRequestStatus.reset();
                    }

                    mInitRequestStatus.setError();
                    innerView.hideInitLoading();

                    onInitRequestError(innerView, e);
                }));
    }

    @UiThread
    public void requestPrePage() {
        requestPrePage(false);
    }

    /**
     * 请求上一页数据。
     * <br/>当发起请求上一页始数据时，会中断旧的初始数据请求和上一页数据请求，但是会保留下一页数据请求。
     * <br/>上一页数据请求和下一页数据请求可以同时进行。
     */
    @UiThread
    public void requestPrePage(boolean force) {
        DynamicLog.v("requestPrePage force:%s", force);

        PageView<E> view = getView();
        if (view == null) {
            DynamicLog.e("view is null");
            return;
        }

        if (mPrePageRequestStatus == null) {
            DynamicLog.v("mPrePageRequestStatus is null");
            return;
        }

        if (!force && !view.hasPageContent()) {
            DynamicLog.v("has no page content");
            return;
        }

        if (!force && !mPrePageRequestStatus.allowRequest()) {
            return;
        }

        clearRequestExcept(mNextPageRequestHolder);

        mPrePageRequestStatus.setLoading();
        mInitRequestStatus.reset();
        view.hideInitLoading();
        view.showPrePageLoading();

        mPrePageRequestHolder.set(Single.just(this)
                .flatMap((Function<Object, SingleSource<Collection<E>>>) object -> createPrePageRequest())
                .map(Objects::requireNonNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    mPrePageRequestStatus.setEnd(items.isEmpty());
                    innerView.hidePrePageLoading();

                    onPrePageRequestResult(innerView, items);
                }, e -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    mPrePageRequestStatus.setError();
                    innerView.hidePrePageLoading();

                    onPrePageRequestError(innerView, e);
                }));
    }

    @UiThread
    public void requestNextPage() {
        requestNextPage(false);
    }

    /**
     * 请求下一页数据。
     * <br/>当发起请求下一页始数据时，会中断旧的初始数据请求和下一页数据请求，但是会保留上一页数据请求。
     * <br/>下一页数据请求和上一页数据请求可以同时进行。
     */
    @UiThread
    public void requestNextPage(boolean force) {
        DynamicLog.v("requestNextPage force:%s", force);

        PageView<E> view = getView();
        if (view == null) {
            DynamicLog.e("view is null");
            return;
        }

        if (mNextPageRequestStatus == null) {
            DynamicLog.v("mNextPageRequestStatus is null");
            return;
        }

        if (!force && !view.hasPageContent()) {
            DynamicLog.v("has no page content");
            return;
        }

        if (!force && !mNextPageRequestStatus.allowRequest()) {
            return;
        }

        clearRequestExcept(mPrePageRequestHolder);

        mNextPageRequestStatus.setLoading();
        mInitRequestStatus.reset();
        view.hideInitLoading();
        view.showNextPageLoading();

        mNextPageRequestHolder.set(Single.just(this)
                .flatMap((Function<Object, SingleSource<Collection<E>>>) object -> createNextPageRequest())
                .map(Objects::requireNonNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    mNextPageRequestStatus.setEnd(items.isEmpty());
                    innerView.hideNextPageLoading();

                    onNextPageRequestResult(innerView, items);
                }, e -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    mNextPageRequestStatus.setError();
                    innerView.hideNextPageLoading();

                    onNextPageRequestError(innerView, e);
                }));
    }

    @WorkerThread
    @Nullable
    protected abstract SingleSource<Collection<E>> createInitRequest() throws Exception;

    @CallSuper
    @UiThread
    protected void onInitRequestResult(@NonNull PageView<E> view, @NonNull Collection<E> items) {
        DynamicLog.v("onInitRequestResult %s items", items.size());
        view.onInitDataLoad(items);
        if (items.isEmpty()) {
            view.onInitDataEmpty();
        }
    }

    @CallSuper
    @UiThread
    protected void onInitRequestError(@NonNull PageView<E> view, @NonNull Throwable e) {
        DynamicLog.e(e, "onInitRequestError");
        view.onInitDataLoadFail(e);
    }

    @WorkerThread
    @Nullable
    protected abstract SingleSource<Collection<E>> createPrePageRequest() throws Exception;

    @CallSuper
    @UiThread
    protected void onPrePageRequestResult(@NonNull PageView<E> view, @NonNull Collection<E> items) {
        DynamicLog.v("onPrePageRequestResult %s items", items.size());
        view.onPrePageDataLoad(items);
        if (items.isEmpty()) {
            view.onPrePageDataEmpty();
        }
    }

    @CallSuper
    @UiThread
    protected void onPrePageRequestError(@NonNull PageView<E> view, @NonNull Throwable e) {
        DynamicLog.e(e, "onPrePageRequestError");
        view.onPrePageDataLoadFail(e);
    }

    @WorkerThread
    @Nullable
    protected abstract SingleSource<Collection<E>> createNextPageRequest() throws Exception;

    @CallSuper
    @UiThread
    protected void onNextPageRequestResult(@NonNull PageView<E> view, @NonNull Collection<E> items) {
        DynamicLog.v("onNextPageRequestResult %s items", items.size());
        view.onNextPageDataLoad(items);
        if (items.isEmpty()) {
            view.onNextPageDataEmpty();
        }
    }

    @CallSuper
    @UiThread
    protected void onNextPageRequestError(@NonNull PageView<E> view, @NonNull Throwable e) {
        DynamicLog.e(e, "onNextPageRequestError");
        view.onNextPageDataLoadFail(e);
    }

    public static class RequestStatus {
        private boolean mLoading;
        private boolean mError;
        private boolean mEnd;
        private boolean mManualToLoad; // 手动触发模式，如点击加载

        public boolean isLoading() {
            return mLoading;
        }

        public boolean isError() {
            return mError;
        }

        public boolean isEnd() {
            return mEnd;
        }

        public boolean isManualToLoad() {
            return mManualToLoad;
        }

        void reset() {
            this.mLoading = false;
            this.mError = false;
            this.mEnd = false;
            this.mManualToLoad = false;
        }

        boolean allowRequest() {
            return !this.mLoading && !this.mError && !this.mEnd && !this.mManualToLoad;
        }

        void setLoading() {
            mLoading = true;
            mError = false;
            mEnd = false;
            mManualToLoad = false;
        }

        void setError() {
            mLoading = false;
            mError = true;
            mEnd = false;
            mManualToLoad = false;
        }

        void setEnd(boolean end) {
            mLoading = false;
            mError = false;
            mEnd = end;
            mManualToLoad = false;
        }

        public void setManualToLoad() {
            mLoading = false;
            mError = false;
            mEnd = false;
            mManualToLoad = true;
        }
    }

}