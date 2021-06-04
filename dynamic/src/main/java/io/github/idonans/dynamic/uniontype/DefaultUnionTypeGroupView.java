package io.github.idonans.dynamic.uniontype;

public abstract class DefaultUnionTypeGroupView implements UnionTypeGroupView {

    @Override
    public int getGroupHeader() {
        return -1;
    }

    @Override
    public int getGroupContent() {
        return 0;
    }

    @Override
    public int getGroupFooter() {
        return 1;
    }

}
