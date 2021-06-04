package io.github.idonans.dynamic.single;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;

import java.util.Objects;

import io.github.idonans.dynamic.DynamicLog;
import io.github.idonans.dynamic.DynamicPresenter;
import io.github.idonans.dynamic.DynamicResult;
import io.github.idonans.dynamic.RequestStatus;
import io.github.idonans.lang.DisposableHolder;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class SinglePresenter<A, B, T extends SingleView<A, B>> extends DynamicPresenter<T> {

    protected final DisposableHolder mInitRequestHolder = new DisposableHolder();

    @NonNull
    private final RequestStatus mInitRequestStatus;

    public SinglePresenter(T view) {
        super(view);

        mInitRequestStatus = new RequestStatus();
        mRequestHolderList.add(mInitRequestHolder);
    }

    @NonNull
    public RequestStatus getInitRequestStatus() {
        return mInitRequestStatus;
    }

    @UiThread
    public void requestInit() {
        requestInit(false);
    }

    /**
     * 请求初始数据
     */
    @UiThread
    public void requestInit(boolean force) {
        DynamicLog.v("requestInit force:%s", force);

        {
            final SingleView<A, B> view = getView();
            if (view == null) {
                DynamicLog.e("view is null");
                return;
            }

            if (!force && !mInitRequestStatus.allowRequest()) {
                return;
            }

            onInitRequest(view);
        }

        mInitRequestHolder.set(Single.just(this)
                .flatMap((Function<Object, SingleSource<DynamicResult<A, B>>>) object -> createInitRequest())
                .map(Objects::requireNonNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    final SingleView<A, B> innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    onInitRequestResult(innerView, result);
                }, e -> {
                    final SingleView<A, B> innerView = getView();
                    if (innerView == null) {
                        DynamicLog.e("view is null");
                        return;
                    }

                    onInitRequestResult(innerView, new DynamicResult<A, B>().setError(e));
                }));
    }

    @WorkerThread
    @Nullable
    protected abstract SingleSource<DynamicResult<A, B>> createInitRequest() throws Exception;

    @UiThread
    protected void onInitRequest(@NonNull SingleView<A, B> view) {
        DynamicLog.v("onInitRequest");

        clearRequestExcept();
        mInitRequestStatus.setLoading();
        view.onInitRequest();
    }

    @UiThread
    protected void onInitRequestResult(@NonNull SingleView<A, B> view, @NonNull DynamicResult<A, B> result) {
        DynamicLog.v("onInitRequestResult");

        if (result.error != null) {
            mInitRequestStatus.setError();
        } else {
            mInitRequestStatus.setEnd(true);
        }

        view.onInitRequestResult(result);
    }

}
