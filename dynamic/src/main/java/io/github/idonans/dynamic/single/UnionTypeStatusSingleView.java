package io.github.idonans.dynamic.single;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import io.github.idonans.dynamic.DynamicLog;
import io.github.idonans.dynamic.DynamicResult;
import io.github.idonans.dynamic.DynamicResultWrapper;
import io.github.idonans.dynamic.LoadingStatusCallback;
import io.github.idonans.dynamic.LoadingStatusCallbackHost;
import io.github.idonans.dynamic.uniontype.DefaultUnionTypeGroupView;
import io.github.idonans.dynamic.uniontype.loadingstatus.UnionTypeLoadingStatus;
import io.github.idonans.uniontype.UnionTypeAdapter;
import io.github.idonans.uniontype.UnionTypeItemObject;

/**
 * @see io.github.idonans.dynamic.uniontype.loadingstatus.UnionTypeLoadingStatus
 */
public abstract class UnionTypeStatusSingleView<T> extends DefaultUnionTypeGroupView implements SingleView<UnionTypeItemObject, T> {

    @NonNull
    private final UnionTypeAdapter mAdapter;
    private boolean mClearContentWhenRequestInit;

    @Nullable
    private SinglePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusSingleView<T>> mPresenter;

    public UnionTypeStatusSingleView(@NonNull UnionTypeAdapter adapter) {
        mAdapter = adapter;
    }

    @NonNull
    public UnionTypeAdapter getAdapter() {
        return mAdapter;
    }

    public void setPresenter(@Nullable SinglePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusSingleView<T>> presenter) {
        mPresenter = presenter;
    }

    @Nullable
    public SinglePresenter<UnionTypeItemObject, T, ? extends UnionTypeStatusSingleView<T>> getPresenter() {
        return mPresenter;
    }

    public void setClearContentWhenRequestInit(boolean clearContentWhenRequestInit) {
        mClearContentWhenRequestInit = clearContentWhenRequestInit;
    }

    public boolean isClearContentWhenRequestInit() {
        return mClearContentWhenRequestInit;
    }

    @Override
    public void onInitRequest() {
        mAdapter.getData().beginTransaction()
                .add((transaction, groupArrayList) -> {
                    // 判断当前是否有内容正在显示
                    final boolean hasContent = groupArrayList.getGroupItemsSize(getGroupContent()) > 0;
                    if (mClearContentWhenRequestInit || !hasContent) {
                        // 清除当前页面内容
                        groupArrayList.removeAll();
                        // 显示一个全屏的 loading
                        groupArrayList.setGroupItems(
                                getGroupHeader(),
                                Lists.newArrayList(
                                        new UnionTypeItemObject(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOADING_LARGE, new Object())
                                )
                        );
                    } else {
                        // 使用小的 loading
                        groupArrayList.setGroupItems(
                                getGroupHeader(),
                                Lists.newArrayList(
                                        new UnionTypeItemObject(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOADING_SMALL, new Object())
                                )
                        );
                    }
                })
                .commit();
    }

    @Override
    public void onInitRequestResult(@NonNull DynamicResult<UnionTypeItemObject, T> result) {
        mAdapter.getData().beginTransaction()
                .add((transaction, groupArrayList) -> {
                    // 清除当前页面内容
                    groupArrayList.removeAll();
                    if (result.items != null) {
                        groupArrayList.setGroupItems(
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
                                    if (mPresenter == null) {
                                        DynamicLog.e("presenter is null");
                                        return;
                                    }
                                    mPresenter.requestInit(true);
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

    protected abstract class DynamicResultWrapperImpl extends DynamicResultWrapper<UnionTypeItemObject, T> implements LoadingStatusCallbackHost {

        public DynamicResultWrapperImpl(@NonNull DynamicResult<UnionTypeItemObject, T> target) {
            super(target);
        }

    }

}
