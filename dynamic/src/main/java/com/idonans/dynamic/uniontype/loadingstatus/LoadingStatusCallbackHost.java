package com.idonans.dynamic.uniontype.loadingstatus;

import androidx.annotation.Nullable;

public interface LoadingStatusCallbackHost {

    @Nullable
    LoadingStatusCallback getLoadingStatusCallback();

    @Nullable
    Throwable getCause();

}
