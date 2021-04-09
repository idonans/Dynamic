package io.github.idonans.dynamic.uniontype.loadingstatus;

import io.github.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusEmptyDataViewHolder;
import io.github.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusLoadFailLargeViewHolder;
import io.github.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusLoadFailSmallViewHolder;
import io.github.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusLoadingLargeViewHolder;
import io.github.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusLoadingSmallViewHolder;
import io.github.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusManualToLoadMoreViewHolder;
import io.github.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusNoMoreDataViewHolder;
import io.github.idonans.uniontype.SimpleUnionTypeMapper;

public class UnionTypeLoadingStatus extends SimpleUnionTypeMapper {

    public static final int UNION_TYPE_LOADING_STATUS_LOADING_LARGE = -1000;
    public static final int UNION_TYPE_LOADING_STATUS_LOADING_SMALL = -1001;
    public static final int UNION_TYPE_LOADING_STATUS_LOAD_FAIL_LARGE = -1002;
    public static final int UNION_TYPE_LOADING_STATUS_LOAD_FAIL_SMALL = -1003;
    public static final int UNION_TYPE_LOADING_STATUS_NO_MORE_DATA = -1004;
    public static final int UNION_TYPE_LOADING_STATUS_EMPTY_DATA = -1005;
    public static final int UNION_TYPE_LOADING_STATUS_MANUAL_TO_LOAD_MORE = -1006;

    public UnionTypeLoadingStatus() {
        put(UNION_TYPE_LOADING_STATUS_LOADING_LARGE, UnionTypeLoadingStatusLoadingLargeViewHolder::new);
        put(UNION_TYPE_LOADING_STATUS_LOADING_SMALL, UnionTypeLoadingStatusLoadingSmallViewHolder::new);
        put(UNION_TYPE_LOADING_STATUS_LOAD_FAIL_LARGE, UnionTypeLoadingStatusLoadFailLargeViewHolder::new);
        put(UNION_TYPE_LOADING_STATUS_LOAD_FAIL_SMALL, UnionTypeLoadingStatusLoadFailSmallViewHolder::new);
        put(UNION_TYPE_LOADING_STATUS_NO_MORE_DATA, UnionTypeLoadingStatusNoMoreDataViewHolder::new);
        put(UNION_TYPE_LOADING_STATUS_EMPTY_DATA, UnionTypeLoadingStatusEmptyDataViewHolder::new);
        put(UNION_TYPE_LOADING_STATUS_MANUAL_TO_LOAD_MORE, UnionTypeLoadingStatusManualToLoadMoreViewHolder::new);
    }

}
