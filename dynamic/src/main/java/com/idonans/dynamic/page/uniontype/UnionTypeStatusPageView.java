package com.idonans.dynamic.page.uniontype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.collect.Lists;
import com.idonans.dynamic.page.PageView;
import com.idonans.dynamic.page.StatusPagePresenter;
import com.idonans.uniontype.GroupArrayList;
import com.idonans.uniontype.Host;
import com.idonans.uniontype.UnionTypeAdapter;
import com.idonans.uniontype.UnionTypeItemObject;

import java.util.Collection;

import timber.log.Timber;

public class UnionTypeStatusPageView implements PageView<UnionTypeItemObject> {

    public static final int GROUP_HEADER_STATUS = -10000;
    public static final int GROUP_DEFAULT = 1;
    public static final int GROUP_FOOTER_STATUS = 10000;

    @NonNull
    private final UnionTypeAdapter mAdapter;
    @Nullable
    private StatusPagePresenter<UnionTypeItemObject, UnionTypeStatusPageView> mPresenter;

    public UnionTypeStatusPageView(@NonNull UnionTypeAdapter adapter) {
        mAdapter = adapter;
        mAdapter.setOnLoadPrePageListener(() -> {
            if (mPresenter == null) {
                Timber.e("presenter is null");
                return;
            }
            mPresenter.requestPrePage();
        });
        mAdapter.setOnLoadNextPageListener(() -> {
            if (mPresenter == null) {
                Timber.e("presenter is null");
                return;
            }
            mPresenter.requestNextPage();
        });
    }

    public void setPresenter(StatusPagePresenter<UnionTypeItemObject, UnionTypeStatusPageView> presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showInitLoading() {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> {
            mAdapter.getData().clearAll();
            mAdapter.getData().setGroupItems(
                    GROUP_DEFAULT,
                    Lists.newArrayList(
                            new UnionTypeItemObject<>(UnionTypePageStatus.UNION_TYPE_PAGE_STATUS_LOADING_LARGE, new Object())
                    )
            );
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void hideInitLoading() {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> {
            mAdapter.getData().clearAll();
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void showPrePageLoading() {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> {
            mAdapter.clearGroupItems(GROUP_HEADER_STATUS);
            mAdapter.appendGroupItems(
                    GROUP_HEADER_STATUS,
                    Lists.newArrayList(
                            new UnionTypeItemObject<>(UnionTypePageStatus.UNION_TYPE_PAGE_STATUS_LOADING_SMALL, new Object())
                    )
            );
        });
    }

    @Override
    public void hidePrePageLoading() {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> mAdapter.clearGroupItems(GROUP_HEADER_STATUS));
    }

    @Override
    public void showNextPageLoading() {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> {
            mAdapter.clearGroupItems(GROUP_FOOTER_STATUS);
            mAdapter.appendGroupItems(
                    GROUP_FOOTER_STATUS,
                    Lists.newArrayList(
                            new UnionTypeItemObject<>(UnionTypePageStatus.UNION_TYPE_PAGE_STATUS_LOADING_SMALL, new Object())
                    )
            );
        });
    }

    @Override
    public void hideNextPageLoading() {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> mAdapter.clearGroupItems(GROUP_FOOTER_STATUS));
    }

    @Override
    public void onInitDataLoad(Collection<UnionTypeItemObject> items) {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> {
            GroupArrayList data = new GroupArrayList();
            data.setGroupItems(GROUP_DEFAULT, items);
            mAdapter.setData(data);
        });
    }

    @Override
    public void onInitDataLoadFail(Throwable e) {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> {
            mAdapter.getData().clearAll();
            mAdapter.getData().setGroupItems(
                    GROUP_DEFAULT,
                    Lists.newArrayList(
                            new UnionTypeItemObject<>(UnionTypePageStatus.UNION_TYPE_PAGE_STATUS_LOAD_FAIL_LARGE, (OnRetryActionListener) () -> {
                                if (mPresenter == null) {
                                    Timber.e("presenter is null");
                                    return;
                                }
                                mPresenter.requestInit(true);
                            })
                    )
            );
            mAdapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onPrePageDataLoad(Collection<UnionTypeItemObject> items) {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> mAdapter.insertGroupItems(GROUP_DEFAULT, 0, items));
    }

    @Override
    public void onPrePageDataLoadFail(Throwable e) {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> {
            mAdapter.clearGroupItems(GROUP_HEADER_STATUS);
            mAdapter.appendGroupItems(
                    GROUP_HEADER_STATUS,
                    Lists.newArrayList(
                            new UnionTypeItemObject<>(UnionTypePageStatus.UNION_TYPE_PAGE_STATUS_LOAD_FAIL_SMALL, (OnRetryActionListener) () -> {
                                if (mPresenter == null) {
                                    Timber.e("presenter is null");
                                    return;
                                }
                                mPresenter.requestPrePage(true);
                            })
                    )
            );
        });
    }

    @Override
    public void onNextPageDataLoad(Collection<UnionTypeItemObject> items) {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> mAdapter.appendGroupItems(GROUP_DEFAULT, items));
    }

    @Override
    public void onNextPageDataLoadFail(Throwable e) {
        Host host = mAdapter.getHost();
        if (host == null) {
            Timber.e("host is null");
            return;
        }
        host.getRecyclerView().postOnAnimation(() -> {
            mAdapter.clearGroupItems(GROUP_FOOTER_STATUS);
            mAdapter.appendGroupItems(
                    GROUP_FOOTER_STATUS,
                    Lists.newArrayList(
                            new UnionTypeItemObject<>(UnionTypePageStatus.UNION_TYPE_PAGE_STATUS_LOAD_FAIL_SMALL, (OnRetryActionListener) () -> {
                                if (mPresenter == null) {
                                    Timber.e("presenter is null");
                                    return;
                                }
                                mPresenter.requestNextPage(true);
                            })
                    )
            );
        });
    }
}
