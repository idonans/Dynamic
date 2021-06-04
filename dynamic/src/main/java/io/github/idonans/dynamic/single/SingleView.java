package io.github.idonans.dynamic.single;

import androidx.annotation.NonNull;

import io.github.idonans.dynamic.DynamicResult;
import io.github.idonans.dynamic.DynamicView;

public interface SingleView<A, B> extends DynamicView {

    void onInitRequest();

    void onInitRequestResult(@NonNull DynamicResult<A, B> result);

}
