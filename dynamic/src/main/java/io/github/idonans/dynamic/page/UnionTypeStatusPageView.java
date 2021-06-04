package io.github.idonans.dynamic.page;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import io.github.idonans.dynamic.DynamicLog;
import io.github.idonans.dynamic.DynamicResult;
import io.github.idonans.dynamic.LoadingStatusCallback;
import io.github.idonans.dynamic.single.UnionTypeStatusSingleView;
import io.github.idonans.dynamic.uniontype.loadingstatus.UnionTypeLoadingStatus;
import io.github.idonans.uniontype.UnionTypeAdapter;
import io.github.idonans.uniontype.UnionTypeItemObject;

public class UnionTypeStatusPageView<T> extends UnionTypeStatusSingleView<T> implements PageView<UnionTypeItemObject, T> {

    private boolean mAlwaysHidePrePageNoMoreData;
    private boolean mAlwaysHideNextPageNoMoreData;

    public UnionTypeStatusPageView(@NonNull UnionTypeAdapter adapter) {
        super(adapter);
        adapter.setOnLoadPrePageListener(() -> {
            final PagePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusPageView<T>> presenter = getPresenter();
            if (presenter == null) {
                DynamicLog.e("presenter is null");
                return;
            }
            presenter.requestPrePage();
        });
        adapter.setOnLoadNextPageListener(() -> {
            final PagePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusPageView<T>> presenter = getPresenter();
            if (presenter == null) {
                DynamicLog.e("presenter is null");
                return;
            }
            presenter.requestNextPage();
        });
    }

    public void setPresenter(@Nullable PagePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusPageView<T>> presenter) {
        super.setPresenter(presenter);
    }

    @Nullable
    public PagePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusPageView<T>> getPresenter() {
        //noinspection unchecked
        return (PagePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusPageView<T>>) super.getPresenter();
    }

    public void setAlwaysHidePrePageNoMoreData(boolean alwaysHidePrePageNoMoreData) {
        mAlwaysHidePrePageNoMoreData = alwaysHidePrePageNoMoreData;
    }

    public void setAlwaysHideNextPageNoMoreData(boolean alwaysHideNextPageNoMoreData) {
        mAlwaysHideNextPageNoMoreData = alwaysHideNextPageNoMoreData;
    }

    public void setAlwaysHideNoMoreData(boolean alwaysHideNoMoreData) {
        mAlwaysHidePrePageNoMoreData = alwaysHideNoMoreData;
        mAlwaysHideNextPageNoMoreData = alwaysHideNoMoreData;
    }

    @Override
    public void onPrePageRequest() {
        getAdapter().getData().beginTransaction()
                .add((transaction, groupArrayList) -> {
                    // 使用小的 loading
                    groupArrayList.setGroupItems(
                            getGroupHeader(),
                            Lists.newArrayList(
                                    new UnionTypeItemObject(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOADING_SMALL, new Object())
                            )
                    );
                })
                .commit();
    }

    @Override
    public void onPrePageRequestResult(@NonNull DynamicResult<UnionTypeItemObject, T> result) {
        getAdapter().getData().beginTransaction()
                .add((transaction, groupArrayList) -> {
                    // 移除 pre page loading
                    groupArrayList.clearGroupItems(getGroupHeader());

                    if (result.items != null) {
                        groupArrayList.insertGroupItems(
                                getGroupContent(),
                                0,
                                result.items
                        );
                    }
                    if (result.error != null) {
                        final Object itemObject = new DynamicResultWrapperImpl(result) {
                            @Nullable
                            @Override
                            public LoadingStatusCallback getLoadingStatusCallback() {
                                return () -> {
                                    final PagePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusPageView<T>> presenter = getPresenter();
                                    if (presenter == null) {
                                        DynamicLog.e("presenter is null");
                                        return;
                                    }
                                    presenter.requestPrePage(true);
                                };
                            }

                            @Nullable
                            @Override
                            public Throwable getCause() {
                                return result.error;
                            }
                        };

                        groupArrayList.setGroupItems(
                                getGroupHeader(),
                                Lists.newArrayList(
                                        new UnionTypeItemObject(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOAD_FAIL_SMALL, itemObject)
                                )
                        );
                    }
                })
                .commit();
    }

    @Override
    public void onNextPageRequest() {
        getAdapter().getData().beginTransaction()
                .add((transaction, groupArrayList) -> {
                    // 使用小的 loading
                    groupArrayList.setGroupItems(
                            getGroupFooter(),
                            Lists.newArrayList(
                                    new UnionTypeItemObject(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOADING_SMALL, new Object())
                            )
                    );
                })
                .commit();
    }

    @Override
    public void onNextPageRequestResult(@NonNull DynamicResult<UnionTypeItemObject, T> result) {
        getAdapter().getData().beginTransaction()
                .add((transaction, groupArrayList) -> {
                    // 移除 next page loading
                    groupArrayList.clearGroupItems(getGroupFooter());

                    if (result.items != null) {
                        groupArrayList.appendGroupItems(
                                getGroupContent(),
                                result.items
                        );
                    }
                    if (result.error != null) {
                        final Object itemObject = new DynamicResultWrapperImpl(result) {
                            @Nullable
                            @Override
                            public LoadingStatusCallback getLoadingStatusCallback() {
                                return () -> {
                                    final PagePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusPageView<T>> presenter = getPresenter();
                                    if (presenter == null) {
                                        DynamicLog.e("presenter is null");
                                        return;
                                    }
                                    presenter.requestNextPage(true);
                                };
                            }

                            @Nullable
                            @Override
                            public Throwable getCause() {
                                return result.error;
                            }
                        };

                        groupArrayList.setGroupItems(
                                getGroupFooter(),
                                Lists.newArrayList(
                                        new UnionTypeItemObject(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOAD_FAIL_SMALL, itemObject)
                                )
                        );
                    }
                })
                .commit();
    }

}
