package io.github.idonans.dynamic.page;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import java.util.Objects;

import io.github.idonans.dynamic.DynamicLog;
import io.github.idonans.dynamic.DynamicResult;
import io.github.idonans.dynamic.RequestStatus;
import io.github.idonans.dynamic.single.SinglePresenter;
import io.github.idonans.lang.DisposableHolder;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * 分页数据请求由 请求初始数据+请求上一页数据+请求下一页数据 组成。
 */
public abstract class PagePresenter<A, B, T extends PageView<A, B>> extends SinglePresenter<A, B, T> {

    protected final DisposableHolder mPrePageRequestHolder = new DisposableHolder();
    protected final DisposableHolder mNextPageRequestHolder = new DisposableHolder();

    @NonNull
    private final RequestStatus mPrePageRequestStatus;
    private boolean mPrePageRequestEnable;

    @NonNull
    private final RequestStatus mNextPageRequestStatus;
    private boolean mNextPageRequestEnable;

    public PagePresenter(T view) {
        super(view);

        mPrePageRequestStatus = new RequestStatus();
        mNextPageRequestStatus = new RequestStatus();
        mRequestHolderList.add(mPrePageRequestHolder);
        mRequestHolderList.add(mNextPageRequestHolder);
    }

    @NonNull
    public RequestStatus getPrePageRequestStatus() {
        return mPrePageRequestStatus;
    }

    public void setPrePageRequestEnable(boolean prePageRequestEnable) {
        mPrePageRequestEnable = prePageRequestEnable;
    }

    public boolean isPrePageRequestEnable() {
        return mPrePageRequestEnable;
    }

    @NonNull
    public RequestStatus getNextPageRequestStatus() {
        return mNextPageRequestStatus;
    }

    public void setNextPageRequestEnable(boolean nextPageRequestEnable) {
        mNextPageRequestEnable = nextPageRequestEnable;
    }

    public boolean isNextPageRequestEnable() {
        return mNextPageRequestEnable;
    }

    /**
     * 当发起请求初始数据时，会中断旧的初始数据请求，上一页数据请求和下一页数据请求。
     */
    @Override
    protected void onInitRequest(@NonNull T view) {
        mPrePageRequestStatus.reset();
        mNextPageRequestStatus.reset();

        super.onInitRequest(view);
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

        {
            final T view = getView();
            if (view == null) {
                DynamicLog.e("view is null");
                return;
            }

            if (!mPrePageRequestEnable) {
                DynamicLog.v("mPrePageRequestEnable is false");
                return;
            }

            if (!force && !mPrePageRequestStatus.allowRequest()) {
                return;
            }

            onPrePageRequest(view);
        }

        mPrePageRequestHolder.set(Single.just(this)
                .flatMap((Function<Object, SingleSource<DynamicResult<A, B>>>) object -> createPrePageRequest())
                .map(Objects::requireNonNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    final T innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    onPrePageRequestResult(innerView, result);
                }, e -> {
                    final T innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    onPrePageRequestResult(innerView, new DynamicResult<A, B>().setError(e));
                }));
    }

    @UiThread
    protected void onPrePageRequest(@NonNull T view) {
        DynamicLog.v("onPrePageRequest");

        clearRequestExcept(mNextPageRequestHolder);
        mPrePageRequestStatus.setLoading();
        getInitRequestStatus().reset();
        view.onPrePageRequest();
    }

    @WorkerThread
    @Nullable
    protected SingleSource<DynamicResult<A, B>> createPrePageRequest() throws Exception {
        throw new RuntimeException("not implements");
    }

    @CallSuper
    @UiThread
    protected void onPrePageRequestResult(@NonNull T view, @NonNull DynamicResult<A, B> result) {
        DynamicLog.v("onPrePageRequestResult");

        if (result.isError()) {
            mPrePageRequestStatus.setError();
        } else {
            mPrePageRequestStatus.setEnd(result.isEnd());
        }
        view.onPrePageRequestResult(result);
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

        {
            final T view = getView();
            if (view == null) {
                DynamicLog.e("view is null");
                return;
            }

            if (!mNextPageRequestEnable) {
                DynamicLog.v("mNextPageRequestEnable is false");
                return;
            }

            if (!force && !mNextPageRequestStatus.allowRequest()) {
                return;
            }

            onNextPageRequest(view);
        }

        mNextPageRequestHolder.set(Single.just(this)
                .flatMap((Function<Object, SingleSource<DynamicResult<A, B>>>) object -> createNextPageRequest())
                .map(Objects::requireNonNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    final T innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    onNextPageRequestResult(innerView, result);
                }, e -> {
                    final T innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    onNextPageRequestResult(innerView, new DynamicResult<A, B>().setError(e));
                }));
    }

    @UiThread
    protected void onNextPageRequest(@NonNull T view) {
        DynamicLog.v("onNextPageRequest");

        clearRequestExcept(mPrePageRequestHolder);
        mNextPageRequestStatus.setLoading();
        getInitRequestStatus().reset();
        view.onNextPageRequest();
    }

    @WorkerThread
    @Nullable
    protected SingleSource<DynamicResult<A, B>> createNextPageRequest() throws Exception {
        throw new RuntimeException("not implements");
    }

    @CallSuper
    @UiThread
    protected void onNextPageRequestResult(@NonNull T view, @NonNull DynamicResult<A, B> result) {
        DynamicLog.v("onNextPageRequestResult");

        if (result.isError()) {
            mNextPageRequestStatus.setError();
        } else {
            mNextPageRequestStatus.setEnd(result.isEnd());
        }
        view.onNextPageRequestResult(result);
    }

}
