package com.idonans.dynamic.page.uniontype;

import com.idonans.dynamic.page.uniontype.impl.UnionTypePageStatusLoadFailLargeViewHolder;
import com.idonans.dynamic.page.uniontype.impl.UnionTypePageStatusLoadFailSmallViewHolder;
import com.idonans.dynamic.page.uniontype.impl.UnionTypePageStatusLoadingLargeViewHolder;
import com.idonans.dynamic.page.uniontype.impl.UnionTypePageStatusLoadingSmallViewHolder;
import com.idonans.uniontype.SimpleUnionTypeMapper;

public class UnionTypePageStatus extends SimpleUnionTypeMapper {

    public static final int UNION_TYPE_PAGE_STATUS_LOADING_LARGE = -1000;
    public static final int UNION_TYPE_PAGE_STATUS_LOADING_SMALL = -1001;
    public static final int UNION_TYPE_PAGE_STATUS_LOAD_FAIL_LARGE = -1002;
    public static final int UNION_TYPE_PAGE_STATUS_LOAD_FAIL_SMALL = -1003;

    public UnionTypePageStatus() {
        put(UNION_TYPE_PAGE_STATUS_LOADING_LARGE, UnionTypePageStatusLoadingLargeViewHolder::new);
        put(UNION_TYPE_PAGE_STATUS_LOADING_SMALL, UnionTypePageStatusLoadingSmallViewHolder::new);
        put(UNION_TYPE_PAGE_STATUS_LOAD_FAIL_LARGE, UnionTypePageStatusLoadFailLargeViewHolder::new);
        put(UNION_TYPE_PAGE_STATUS_LOAD_FAIL_SMALL, UnionTypePageStatusLoadFailSmallViewHolder::new);
    }

}
