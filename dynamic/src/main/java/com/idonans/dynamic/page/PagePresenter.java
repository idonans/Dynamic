package com.idonans.dynamic.page;

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

/**
 * 分页数据请求由 请求初始数据+请求上一页数据+请求下一页数据 组成。
 */
public abstract class PagePresenter<E, T extends PageView<E>> extends DynamicPresenter<T> {

    protected final DisposableHolder mInitRequestHolder = new DisposableHolder();
    protected final DisposableHolder mPrePageRequestHolder = new DisposableHolder();
    protected final DisposableHolder mNextPageRequestHolder = new DisposableHolder();
    protected final DisposableHolder[] mRequestHolders = {mInitRequestHolder, mPrePageRequestHolder, mNextPageRequestHolder};

    public PagePresenter(T view) {
        super(view);
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

    /**
     * 请求初始数据。
     * <br/>当发起请求初始数据时，会中断旧的初始数据请求，上一页数据请求和下一页数据请求。
     */
    @UiThread
    public void requestInit() {
        clearRequestExcept();

        PageView<E> view = getView();
        if (view == null) {
            Timber.e("view is null");
            return;
        }
        view.hidePrePageLoading();
        view.hideNextPageLoading();
        view.showInitLoading();

        mInitRequestHolder.set(Single.just(this)
                .flatMap((Function<Object, SingleSource<Collection<E>>>) object -> createInitRequest())
                .map(Objects::requireNonNull)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(items -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        Timber.e("view is null");
                        return;
                    }
                    innerView.hideInitLoading();
                    onInitRequestResult(innerView, items);
                }, e -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        Timber.e("view is null");
                        return;
                    }
                    innerView.hideInitLoading();
                    onInitRequestError(innerView, e);
                }));
    }

    /**
     * 请求上一页数据。
     * <br/>当发起请求上一页始数据时，会中断旧的初始数据请求和上一页数据请求，但是会保留下一页数据请求。
     * <br/>上一页数据请求和下一页数据请求可以同时进行。
     */
    @UiThread
    public void requestPrePage() {
        clearRequestExcept(mNextPageRequestHolder);

        PageView<E> view = getView();
        if (view == null) {
            Timber.e("view is null");
            return;
        }
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
                        Timber.e("view is null");
                        return;
                    }
                    innerView.hidePrePageLoading();
                    onPrePageRequestResult(innerView, items);
                }, e -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        Timber.e("view is null");
                        return;
                    }
                    innerView.hidePrePageLoading();
                    onPrePageRequestError(innerView, e);
                }));
    }

    /**
     * 请求下一页数据。
     * <br/>当发起请求下一页始数据时，会中断旧的初始数据请求和下一页数据请求，但是会保留上一页数据请求。
     * <br/>下一页数据请求和上一页数据请求可以同时进行。
     */
    @UiThread
    public void requestNextPage() {
        clearRequestExcept(mPrePageRequestHolder);

        PageView<E> view = getView();
        if (view == null) {
            Timber.e("view is null");
            return;
        }
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
                        Timber.e("view is null");
                        return;
                    }
                    innerView.hideNextPageLoading();
                    onNextPageRequestResult(innerView, items);
                }, e -> {
                    PageView<E> innerView = getView();
                    if (innerView == null) {
                        Timber.e("view is null");
                        return;
                    }
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
        view.onInitDataLoad(items);
        if (items.isEmpty()) {
            view.onInitDataEmpty();
        }
    }

    @CallSuper
    @UiThread
    protected void onInitRequestError(@NonNull PageView<E> view, @NonNull Throwable e) {
        view.onInitDataLoadFail(e);
    }

    @WorkerThread
    @Nullable
    protected abstract SingleSource<Collection<E>> createPrePageRequest() throws Exception;

    @CallSuper
    @UiThread
    protected void onPrePageRequestResult(@NonNull PageView<E> view, @NonNull Collection<E> items) {
        view.onPrePageDataLoad(items);
        if (items.isEmpty()) {
            view.onPrePageDataEmpty();
        }
    }

    @CallSuper
    @UiThread
    protected void onPrePageRequestError(@NonNull PageView<E> view, @NonNull Throwable e) {
        view.onPrePageDataLoadFail(e);
    }

    @WorkerThread
    @Nullable
    protected abstract SingleSource<Collection<E>> createNextPageRequest() throws Exception;

    @CallSuper
    @UiThread
    protected void onNextPageRequestResult(@NonNull PageView<E> view, @NonNull Collection<E> items) {
        view.onNextPageDataLoad(items);
        if (items.isEmpty()) {
            view.onNextPageDataEmpty();
        }
    }

    @CallSuper
    @UiThread
    protected void onNextPageRequestError(@NonNull PageView<E> view, @NonNull Throwable e) {
        view.onNextPageDataLoadFail(e);
    }

}
