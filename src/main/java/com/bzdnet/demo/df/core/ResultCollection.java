package com.bzdnet.demo.df.core;

import lombok.Data;

import java.util.List;

@Data
public class ResultCollection {

    public ResultCollection() {
    }

    public ResultCollection(String property, Class modelClass, ResultID resultID, List<ResultSimple> simpleList) {
        this.property = property;
        this.modelClass = modelClass;
        this.resultID = resultID;
        this.simpleList = simpleList;
    }

    private String property;
    private Class modelClass;
    private ResultID resultID;
    private List<ResultSimple> simpleList;

}
