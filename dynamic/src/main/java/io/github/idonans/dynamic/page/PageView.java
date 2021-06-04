package io.github.idonans.dynamic.page;

import androidx.annotation.NonNull;

import io.github.idonans.dynamic.DynamicResult;
import io.github.idonans.dynamic.single.SingleView;

public interface PageView<A, B> extends SingleView<A, B> {

    void onPrePageRequest();

    void onPrePageRequestResult(@NonNull DynamicResult<A, B> result);

    void onNextPageRequest();

    void onNextPageRequestResult(@NonNull DynamicResult<A, B> result);

}
