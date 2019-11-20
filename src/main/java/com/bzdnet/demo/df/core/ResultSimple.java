package com.bzdnet.demo.df.core;

import lombok.Data;

@Data
public class ResultSimple {

    public ResultSimple() {
    }

    public ResultSimple(String property, String column, Class javaType) {
        this.property = property;
        this.column = column;
        this.javaType = javaType;
    }

    private String property;
    private String column;
    private Class javaType;

}
