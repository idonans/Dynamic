package com.idonans.dynamic.uniontype.loadingstatus;

import com.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusLoadFailLargeViewHolder;
import com.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusLoadFailSmallViewHolder;
import com.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusLoadingLargeViewHolder;
import com.idonans.dynamic.uniontype.loadingstatus.impl.UnionTypeLoadingStatusLoadingSmallViewHolder;
import com.idonans.uniontype.SimpleUnionTypeMapper;

public class UnionTypeLoadingStatus extends SimpleUnionTypeMapper {

    public static final int UNION_TYPE_LOADING_STATUS_LOADING_LARGE = -1000;
    public static final int UNION_TYPE_LOADING_STATUS_LOADING_SMALL = -1001;
    public static final int UNION_TYPE_LOADING_STATUS_LOAD_FAIL_LARGE = -1002;
    public static final int UNION_TYPE_LOADING_STATUS_LOAD_FAIL_SMALL = -1003;

    public UnionTypeLoadingStatus() {
        put(UNION_TYPE_LOADING_STATUS_LOADING_LARGE, UnionTypeLoadingStatusLoadingLargeViewHolder::new);
        put(UNION_TYPE_LOADING_STATUS_LOADING_SMALL, UnionTypeLoadingStatusLoadingSmallViewHolder::new);
        put(UNION_TYPE_LOADING_STATUS_LOAD_FAIL_LARGE, UnionTypeLoadingStatusLoadFailLargeViewHolder::new);
        put(UNION_TYPE_LOADING_STATUS_LOAD_FAIL_SMALL, UnionTypeLoadingStatusLoadFailSmallViewHolder::new);
    }

}
