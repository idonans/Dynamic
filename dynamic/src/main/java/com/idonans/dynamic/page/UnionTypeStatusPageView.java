package com.idonans.dynamic.page;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.Lists;
import com.idonans.dynamic.DynamicLog;
import com.idonans.dynamic.LoadingStatusCallback;
import com.idonans.dynamic.LoadingStatusCallbackHost;
import com.idonans.dynamic.uniontype.loadingstatus.UnionTypeLoadingStatus;
import com.idonans.uniontype.Host;
import com.idonans.uniontype.UnionTypeAdapter;
import com.idonans.uniontype.UnionTypeItemObject;

import java.util.Collection;

public class UnionTypeStatusPageView implements PageView<UnionTypeItemObject> {

    public static final int GROUP_HEADER_STATUS = -10000;
    public static final int GROUP_INIT_STATUS = -9999;
    public static final int GROUP_DEFAULT = 1;
    public static final int GROUP_FOOTER_STATUS = 10000;

    @NonNull
    private final UnionTypeAdapter mAdapter;
    private final boolean mClearPageContentWhenRequestInit;
    @Nullable
    private PagePresenter<UnionTypeItemObject, UnionTypeStatusPageView> mPresenter;

    private boolean mAlwaysHidePrePageNoMoreData;
    private boolean mAlwaysHideNextPageNoMoreData;

    public UnionTypeStatusPageView(@NonNull UnionTypeAdapter adapter) {
        this(adapter, false);
    }

    public UnionTypeStatusPageView(@NonNull UnionTypeAdapter adapter, boolean clearPageContentWhenRequestInit) {
        mAdapter = adapter;
        mClearPageContentWhenRequestInit = clearPageContentWhenRequestInit;
        mAdapter.setOnLoadPrePageListener(() -> {
            if (mPresenter == null) {
                DynamicLog.e("presenter is null");
                return;
            }
            mPresenter.requestPrePage();
        });
        mAdapter.setOnLoadNextPageListener(() -> {
            if (mPresenter == null) {
                DynamicLog.e("presenter is null");
                return;
            }
            mPresenter.requestNextPage();
        });
    }

    public void setPresenter(@Nullable PagePresenter<UnionTypeItemObject, UnionTypeStatusPageView> presenter) {
        mPresenter = presenter;
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
    public boolean isClearPageContentWhenRequestInit() {
        return mClearPageContentWhenRequestInit;
    }

    @Override
    public boolean hasPageContent() {
        return mAdapter.getData().getGroupItemCount(GROUP_DEFAULT) > 0;
    }

    protected void onClearPageContent() {
        mAdapter.clearGroupItems(GROUP_DEFAULT);
    }

    @NonNull
    public UnionTypeAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void showInitLoading() {
        DynamicLog.v("showInitLoading");
        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("showInitLoading post run");
            final boolean clearPageContentWhenRequestInit = isClearPageContentWhenRequestInit();
            DynamicLog.v("showInitLoading post run clearPageContentWhenRequestInit:%s", clearPageContentWhenRequestInit);
            if (clearPageContentWhenRequestInit) {
                onClearPageContent();
                mAdapter.setGroupItems(
                        GROUP_INIT_STATUS,
                        Lists.newArrayList(
                                new UnionTypeItemObject(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOADING_LARGE, new Object())
                        )
                );
                return;
            }
            boolean hasPageContent = hasPageContent();
            DynamicLog.v("showInitLoading post run hasPageContent:%s", hasPageContent);
            if (!hasPageContent) {
                mAdapter.setGroupItems(
                        GROUP_INIT_STATUS,
                        Lists.newArrayList(
                                new UnionTypeItemObject(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOADING_LARGE, new Object())
                        )
                );
            } else {
                mAdapter.setGroupItems(
                        GROUP_INIT_STATUS,
                        Lists.newArrayList(
                                new UnionTypeItemObject(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOADING_SMALL, new Object())
                        )
                );
            }
        });
    }

    @Override
    public void hideInitLoading() {
        DynamicLog.v("hideInitLoading");
        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("hideInitLoading post run");
            mAdapter.clearGroupItems(GROUP_INIT_STATUS);
        });
    }


    @Override
    public void onInitDataLoad(@NonNull Collection<UnionTypeItemObject> items) {
        DynamicLog.v("onInitDataLoad items size:%s", items.size());
        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("onInitDataLoad post run items size:%s", items.size());
            mAdapter.setGroupItems(GROUP_DEFAULT, items);
        });
    }

    @Override
    public void onInitDataEmpty() {
        DynamicLog.v("onInitDataEmpty");
        final Object itemObject = new LoadingStatusCallbackHost() {
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
                return null;
            }
        };

        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("onInitDataEmpty post run");
            onClearPageContent();
            mAdapter.setGroupItems(
                    GROUP_INIT_STATUS,
                    Lists.newArrayList(UnionTypeItemObject.valueOf(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_EMPTY_DATA, itemObject))
            );
        });
    }

    @Override
    public void onInitDataLoadFail(@NonNull Throwable e) {
        DynamicLog.v("onInitDataLoadFail");
        final Object itemObject = new LoadingStatusCallbackHost() {
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
                return e;
            }
        };

        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("onInitDataLoadFail post run");
            boolean hasPageContent = hasPageContent();
            if (!hasPageContent) {
                mAdapter.setGroupItems(
                        GROUP_INIT_STATUS,
                        Lists.newArrayList(
                                UnionTypeItemObject.valueOf(
                                        UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOAD_FAIL_LARGE,
                                        itemObject
                                )
                        )
                );
            } else {
                mAdapter.setGroupItems(
                        GROUP_INIT_STATUS,
                        Lists.newArrayList(
                                UnionTypeItemObject.valueOf(
                                        UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOAD_FAIL_SMALL,
                                        itemObject
                                )
                        )
                );
            }
        });
    }

    @Override
    public void showPrePageLoading() {
        DynamicLog.v("showPrePageLoading");
        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
                    DynamicLog.v("showPrePageLoading post run");
                    mAdapter.setGroupItems(
                            GROUP_HEADER_STATUS,
                            Lists.newArrayList(
                                    UnionTypeItemObject.valueOf(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOADING_SMALL, new Object())
                            )
                    );
                }
        );
    }

    @Override
    public void hidePrePageLoading() {
        DynamicLog.v("hidePrePageLoading");
        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("hidePrePageLoading post run");
            mAdapter.clearGroupItems(GROUP_HEADER_STATUS);
        });
    }

    @Override
    public void onPrePageDataLoad(@NonNull Collection<UnionTypeItemObject> items) {
        DynamicLog.v("onPrePageDataLoad items size:%s", items.size());
        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("onPrePageDataLoad post run items size:%s", items.size());
            mAdapter.insertGroupItems(GROUP_DEFAULT, 0, items);
        });
    }

    @Override
    public void onPrePageDataEmpty() {
        DynamicLog.v("onPrePageDataEmpty");
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("presenter is null");
                        return;
                    }
                    mPresenter.requestPrePage(true);
                };
            }

            @Nullable
            @Override
            public Throwable getCause() {
                return null;
            }
        };

        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("onPrePageDataEmpty post run");
            if (mAlwaysHidePrePageNoMoreData) {
                mAdapter.clearGroupItems(GROUP_HEADER_STATUS);
            } else {
                mAdapter.setGroupItems(
                        GROUP_HEADER_STATUS,
                        Lists.newArrayList(UnionTypeItemObject.valueOf(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_NO_MORE_DATA, itemObject))
                );
            }
        });
    }

    @Override
    public void onPrePageDataLoadFail(@NonNull Throwable e) {
        DynamicLog.v("onPrePageDataLoadFail");
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("presenter is null");
                        return;
                    }
                    mPresenter.requestPrePage(true);
                };
            }

            @Nullable
            @Override
            public Throwable getCause() {
                return e;
            }
        };

        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
                    DynamicLog.v("onPrePageDataLoadFail post run");
                    mAdapter.setGroupItems(
                            GROUP_HEADER_STATUS,
                            Lists.newArrayList(
                                    UnionTypeItemObject.valueOf(
                                            UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOAD_FAIL_SMALL,
                                            itemObject
                                    )
                            )
                    );
                }
        );
    }

    @Override
    public void showPrePageManualToLoadMore() {
        DynamicLog.v("showPrePageManualToLoadMore");
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("presenter is null");
                        return;
                    }
                    mPresenter.requestPrePage(true);
                };
            }

            @Nullable
            @Override
            public Throwable getCause() {
                return null;
            }
        };

        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
                    DynamicLog.v("showPrePageManualToLoadMore post run");
                    mAdapter.setGroupItems(
                            GROUP_HEADER_STATUS,
                            Lists.newArrayList(
                                    UnionTypeItemObject.valueOf(
                                            UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_MANUAL_TO_LOAD_MORE,
                                            itemObject
                                    )
                            )
                    );
                }
        );
    }

    @Override
    public void showNextPageLoading() {
        DynamicLog.v("showNextPageLoading");
        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
                    DynamicLog.v("showNextPageLoading post run");
                    mAdapter.setGroupItems(
                            GROUP_FOOTER_STATUS,
                            Lists.newArrayList(
                                    UnionTypeItemObject.valueOf(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOADING_SMALL, new Object())
                            )
                    );
                }
        );
    }

    @Override
    public void hideNextPageLoading() {
        DynamicLog.v("hideNextPageLoading");
        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("hideNextPageLoading post run");
            mAdapter.clearGroupItems(GROUP_FOOTER_STATUS);
        });
    }

    @Override
    public void onNextPageDataLoad(@NonNull Collection<UnionTypeItemObject> items) {
        DynamicLog.v("onNextPageDataLoad items size:%s", items.size());
        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("onNextPageDataLoad post run items size:%s", items.size());
            mAdapter.appendGroupItems(GROUP_DEFAULT, items);
        });
    }

    @Override
    public void onNextPageDataEmpty() {
        DynamicLog.v("onNextPageDataEmpty");
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("presenter is null");
                        return;
                    }
                    mPresenter.requestNextPage(true);
                };
            }

            @Nullable
            @Override
            public Throwable getCause() {
                return null;
            }
        };

        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
            DynamicLog.v("onNextPageDataEmpty post run");
            if (mAlwaysHideNextPageNoMoreData) {
                mAdapter.clearGroupItems(GROUP_FOOTER_STATUS);
            } else {
                mAdapter.setGroupItems(
                        GROUP_FOOTER_STATUS,
                        Lists.newArrayList(UnionTypeItemObject.valueOf(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_NO_MORE_DATA, itemObject))
                );
            }
        });
    }

    @Override
    public void onNextPageDataLoadFail(@NonNull Throwable e) {
        DynamicLog.v("onNextPageDataLoadFail");
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("presenter is null");
                        return;
                    }
                    mPresenter.requestNextPage(true);
                };
            }

            @Nullable
            @Override
            public Throwable getCause() {
                return e;
            }
        };

        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
                    DynamicLog.v("onNextPageDataLoadFail post run");
                    mAdapter.setGroupItems(
                            GROUP_FOOTER_STATUS,
                            Lists.newArrayList(
                                    UnionTypeItemObject.valueOf(
                                            UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_LOAD_FAIL_SMALL,
                                            itemObject
                                    )
                            )
                    );
                }
        );
    }

    @Override
    public void showNextPageManualToLoadMore() {
        DynamicLog.v("showNextPageManualToLoadMore");
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("presenter is null");
                        return;
                    }
                    mPresenter.requestNextPage(true);
                };
            }

            @Nullable
            @Override
            public Throwable getCause() {
                return null;
            }
        };

        Host host = mAdapter.getHost();
        host.getRecyclerView().post(() -> {
                    DynamicLog.v("showNextPageManualToLoadMore post run");
                    mAdapter.setGroupItems(
                            GROUP_FOOTER_STATUS,
                            Lists.newArrayList(
                                    UnionTypeItemObject.valueOf(
                                            UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_MANUAL_TO_LOAD_MORE,
                                            itemObject
                                    )
                            )
                    );
                }
        );
    }

}
