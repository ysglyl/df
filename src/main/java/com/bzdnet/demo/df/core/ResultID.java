package com.bzdnet.demo.df.core;

import lombok.Data;

@Data
public class ResultID {

    public ResultID() {
    }

    public ResultID(String property, String column,Class javaType) {
        this.property = property;
        this.column = column;
        this.javaType=javaType;
    }

    private String property;
    private String column;
    private Class javaType;

}
