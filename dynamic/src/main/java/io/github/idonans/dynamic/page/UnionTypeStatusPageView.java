package io.github.idonans.dynamic.page;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.Lists;

import java.util.Collection;

import io.github.idonans.core.thread.Threads;
import io.github.idonans.dynamic.DynamicLog;
import io.github.idonans.dynamic.LoadingStatusCallback;
import io.github.idonans.dynamic.LoadingStatusCallbackHost;
import io.github.idonans.dynamic.uniontype.loadingstatus.UnionTypeLoadingStatus;
import io.github.idonans.uniontype.UnionTypeAdapter;
import io.github.idonans.uniontype.UnionTypeItemObject;

public class UnionTypeStatusPageView implements PageView<UnionTypeItemObject> {

    public static final int GROUP_HEADER_STATUS = -10000;
    public static final int GROUP_INIT_STATUS = -9999;
    public static final int GROUP_DEFAULT = 1;
    public static final int GROUP_FOOTER_STATUS = 10000;

    private final String mDebugTag = getClass().getSimpleName() + "@" + System.identityHashCode(this);

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
                DynamicLog.e("%s, presenter is null", mDebugTag);
                return;
            }
            mPresenter.requestPrePage();
        });
        mAdapter.setOnLoadNextPageListener(() -> {
            if (mPresenter == null) {
                DynamicLog.e("%s, presenter is null", mDebugTag);
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
        DynamicLog.v("%s, showInitLoading", mDebugTag);
        Threads.postUi(() -> {
            DynamicLog.v("%s, showInitLoading post run", mDebugTag);
            final boolean clearPageContentWhenRequestInit = isClearPageContentWhenRequestInit();
            DynamicLog.v("%s, showInitLoading post run clearPageContentWhenRequestInit:%s", mDebugTag, clearPageContentWhenRequestInit);
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
            DynamicLog.v("%s, showInitLoading post run hasPageContent:%s", mDebugTag, hasPageContent);
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
        DynamicLog.v("%s, hideInitLoading", mDebugTag);
        Threads.postUi(() -> {
            DynamicLog.v("%s, hideInitLoading post run", mDebugTag);
            mAdapter.clearGroupItems(GROUP_INIT_STATUS);
        });
    }


    @Override
    public void onInitDataLoad(@NonNull Collection<UnionTypeItemObject> items) {
        DynamicLog.v("%s, onInitDataLoad items size:%s", mDebugTag, items.size());
        Threads.postUi(() -> {
            DynamicLog.v("%s, onInitDataLoad post run items size:%s", mDebugTag, items.size());
            mAdapter.setGroupItems(GROUP_DEFAULT, items);
        });
    }

    @Override
    public void onInitDataEmpty() {
        DynamicLog.v("%s, onInitDataEmpty", mDebugTag);
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("%s, presenter is null", mDebugTag);
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

        Threads.postUi(() -> {
            DynamicLog.v("%s, onInitDataEmpty post run", mDebugTag);
            onClearPageContent();
            mAdapter.setGroupItems(
                    GROUP_INIT_STATUS,
                    Lists.newArrayList(UnionTypeItemObject.valueOf(UnionTypeLoadingStatus.UNION_TYPE_LOADING_STATUS_EMPTY_DATA, itemObject))
            );
        });
    }

    @Override
    public void onInitDataLoadFail(@NonNull Throwable e) {
        DynamicLog.v("%s, onInitDataLoadFail", mDebugTag);
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("%s, presenter is null", mDebugTag);
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

        Threads.postUi(() -> {
            DynamicLog.v("%s, onInitDataLoadFail post run", mDebugTag);
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
        DynamicLog.v("%s, showPrePageLoading", mDebugTag);

        Threads.postUi(() -> {
                    DynamicLog.v("%s, showPrePageLoading post run", mDebugTag);
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
        DynamicLog.v("%s, hidePrePageLoading", mDebugTag);

        Threads.postUi(() -> {
            DynamicLog.v("%s, hidePrePageLoading post run", mDebugTag);
            mAdapter.clearGroupItems(GROUP_HEADER_STATUS);
        });
    }

    @Override
    public void onPrePageDataLoad(@NonNull Collection<UnionTypeItemObject> items) {
        DynamicLog.v("%s, onPrePageDataLoad items size:%s", mDebugTag, items.size());

        Threads.postUi(() -> {
            DynamicLog.v("%s, onPrePageDataLoad post run items size:%s", mDebugTag, items.size());
            mAdapter.insertGroupItems(GROUP_DEFAULT, 0, items);
        });
    }

    @Override
    public void onPrePageDataEmpty() {
        DynamicLog.v("%s, onPrePageDataEmpty", mDebugTag);
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("%s, presenter is null", mDebugTag);
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

        Threads.postUi(() -> {
            DynamicLog.v("%s, onPrePageDataEmpty post run", mDebugTag);
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
        DynamicLog.v("%s, onPrePageDataLoadFail", mDebugTag);
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("%s, presenter is null", mDebugTag);
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

        Threads.postUi(() -> {
                    DynamicLog.v("%s, onPrePageDataLoadFail post run", mDebugTag);
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
        DynamicLog.v("%s, showPrePageManualToLoadMore", mDebugTag);
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("%s, presenter is null", mDebugTag);
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

        Threads.postUi(() -> {
                    DynamicLog.v("%s, showPrePageManualToLoadMore post run", mDebugTag);
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
        DynamicLog.v("%s, showNextPageLoading", mDebugTag);

        Threads.postUi(() -> {
                    DynamicLog.v("%s, showNextPageLoading post run", mDebugTag);
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
        DynamicLog.v("%s, hideNextPageLoading", mDebugTag);

        Threads.postUi(() -> {
            DynamicLog.v("%s, hideNextPageLoading post run", mDebugTag);
            mAdapter.clearGroupItems(GROUP_FOOTER_STATUS);
        });
    }

    @Override
    public void onNextPageDataLoad(@NonNull Collection<UnionTypeItemObject> items) {
        DynamicLog.v("%s, onNextPageDataLoad items size:%s", mDebugTag, items.size());

        Threads.postUi(() -> {
            DynamicLog.v("%s, onNextPageDataLoad post run items size:%s", mDebugTag, items.size());
            mAdapter.appendGroupItems(GROUP_DEFAULT, items);
        });
    }

    @Override
    public void onNextPageDataEmpty() {
        DynamicLog.v("%s, onNextPageDataEmpty", mDebugTag);
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("%s, presenter is null", mDebugTag);
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

        Threads.postUi(() -> {
            DynamicLog.v("%s, onNextPageDataEmpty post run", mDebugTag);
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
        DynamicLog.v("%s, onNextPageDataLoadFail", mDebugTag);
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("%s, presenter is null", mDebugTag);
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

        Threads.postUi(() -> {
                    DynamicLog.v("%s, onNextPageDataLoadFail post run", mDebugTag);
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
        DynamicLog.v("%s, showNextPageManualToLoadMore", mDebugTag);
        final Object itemObject = new LoadingStatusCallbackHost() {
            @Nullable
            @Override
            public LoadingStatusCallback getLoadingStatusCallback() {
                return () -> {
                    if (mPresenter == null) {
                        DynamicLog.e("%s, presenter is null", mDebugTag);
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

        Threads.postUi(() -> {
                    DynamicLog.v("%s, showNextPageManualToLoadMore post run", mDebugTag);
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
