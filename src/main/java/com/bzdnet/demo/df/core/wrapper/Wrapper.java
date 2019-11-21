package com.bzdnet.demo.df.core.wrapper;

public class Wrapper {

    private Wrapper() {
    }

    public static QueryWrapper query() {
        return new QueryWrapper();
    }

    public static InsertWrapper insert() {
        return new InsertWrapper();
    }

    public static UpdateWrapper update() {
        return new UpdateWrapper();
    }

    public static DeleteWrapper delete() {
        return new DeleteWrapper();
    }

}
