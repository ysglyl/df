package com.bzdnet.demo.df.core.result;

import lombok.Data;

@Data
public class ResultValue {

    public ResultValue(String property, String column, Class javaType) {
        this.property = property;
        this.column = column;
        this.javaType=javaType;
    }

    private String property;
    private String column;
    private Class javaType;

}
