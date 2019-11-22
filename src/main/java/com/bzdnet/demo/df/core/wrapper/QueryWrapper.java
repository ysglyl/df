package com.bzdnet.demo.df.core.wrapper;

public class QueryWrapper<T> extends Wrapper {

    private long page;
    private long size;

    public QueryWrapper(Class<T> t) {
        super(t);
    }

    public QueryWrapper paged(long page, long size) {
        this.page = page;
        this.size = size;
        return this;
    }


}
