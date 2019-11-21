package com.bzdnet.demo.df.core.result;

import com.bzdnet.demo.df.core.model.KeyValuePair;
import lombok.Data;

import java.util.List;

@Data
public class ResultModel {

    public ResultModel(String property, Class modelClass, String tableName, ResultValue id, List<ResultValue> columns, KeyValuePair relation) {
        this.property = property;
        this.modelClass = modelClass;
        this.tableName = tableName;
        this.id = id;
        this.columns = columns;
        this.relation = relation;
    }

    public ResultModel(String property, Class modelClass, String tableName, ResultValue id, List<ResultValue> columns, String refTableName, KeyValuePair refPrimary, KeyValuePair refSecondary) {
        this.property = property;
        this.modelClass = modelClass;
        this.tableName = tableName;
        this.id = id;
        this.columns = columns;
        this.refTableName = refTableName;
        this.refPrimary = refPrimary;
        this.refSecondary = refSecondary;
    }


    private String property;
    private Class modelClass;
    private String tableName;
    private ResultValue id;
    private List<ResultValue> columns;

    // 关联对象需要的额外属性
    private KeyValuePair relation; //key为主表列名，value为从表（当前表）列名

    //列表对象需要的额外属性
    private String refTableName; // 关系表名称
    private KeyValuePair refPrimary; // 主表关联条件，key为主表列名，value为关系表列名
    private KeyValuePair refSecondary; // 从表关联条件，key为从表（当前表）列名，value为关系表列名

}
