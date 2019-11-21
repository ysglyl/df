package com.bzdnet.demo.df.core.result;

import lombok.Data;

import java.util.List;

@Data
public class ResultMap {

    private Class type;
    private String tableName;
    private ResultValue id;
    private List<ResultValue> columns;
    private List<ResultModel> associations;
    private List<ResultModel> collections;

}
