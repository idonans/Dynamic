package io.github.idonans.dynamic;

public class RequestStatus {

    private boolean mLoading;
    private boolean mError;
    private boolean mEnd;

    public boolean isLoading() {
        return mLoading;
    }

    public boolean isError() {
        return mError;
    }

    public boolean isEnd() {
        return mEnd;
    }

    public void reset() {
        this.mLoading = false;
        this.mError = false;
        this.mEnd = false;
    }

    public boolean allowRequest() {
        return !this.mLoading && !this.mError && !this.mEnd;
    }

    public void setLoading() {
        mLoading = true;
        mError = false;
        mEnd = false;
    }

    public void setError() {
        mLoading = false;
        mError = true;
        mEnd = false;
    }

    public void setEnd(boolean end) {
        mLoading = false;
        mError = false;
        mEnd = end;
    }

}
