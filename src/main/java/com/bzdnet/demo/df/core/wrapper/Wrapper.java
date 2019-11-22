package com.bzdnet.demo.df.core.wrapper;

import com.bzdnet.demo.df.core.annotation.DbTable;
import com.bzdnet.demo.df.core.model.Condition;
import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Wrapper<T> {

    protected Class<T> modelClass;
    private String tableName;
    private List<Condition> conditions = new ArrayList<>();
    private List<Wrapper> ands = new ArrayList<>();
    private List<Wrapper> ors = new ArrayList<>();

    private Wrapper currentWrapper = null;

    protected Wrapper(Class<T> t) {
        this.modelClass = t;
        this.tableName = this.modelClass.getAnnotation(DbTable.class).table();
        this.currentWrapper = this;
    }

    public Wrapper and(Wrapper wrapper){
        this.ands.add(wrapper);
        return this;
    }

    public Wrapper and() {
        currentWrapper = new Wrapper(this.modelClass);
        this.ands.add(currentWrapper);
        return this;
    }

    public Wrapper or() {
        currentWrapper = new Wrapper(this.modelClass);
        this.ors.add(currentWrapper);
        return this;
    }

    public Wrapper eq(String key, @NotNull Object value) {
        Condition condition = new Condition(this.tableName, key, value, Condition.Operator.EQ);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper lt(String key, @NotNull Object value) {
        Condition condition = new Condition(this.tableName, key, value, Condition.Operator.LT);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper ltEq(String key, @NotNull Object value) {
        Condition condition = new Condition(this.tableName, key, value, Condition.Operator.LT_EQ);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper gt(String key, @NotNull Object value) {
        Condition condition = new Condition(this.tableName, key, value, Condition.Operator.GT);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper gtEq(String key, @NotNull Object value) {
        Condition condition = new Condition(this.tableName, key, value, Condition.Operator.GT_EQ);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper like(String key, @NotNull Object value) {
        Condition condition = new Condition(this.tableName, key, value, Condition.Operator.LIKE);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper leftLike(String key, @NotNull Object value) {
        Condition condition = new Condition(this.tableName, key, value, Condition.Operator.LEFT_LIKE);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper rightLike(String key, @NotNull Object value) {
        Condition condition = new Condition(this.tableName, key, value, Condition.Operator.RIGHT_LIKE);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper in(String key, Object... value) {
        Condition condition = new Condition(this.tableName, key, join(value), Condition.Operator.IN);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper notEq(String key, @NotNull Object value) {
        Condition condition = new Condition(this.tableName, key, value, Condition.Operator.NOT_EQ);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public Wrapper notIn(String key, @NotNull Object... value) {
        Condition condition = new Condition(this.tableName, key, join(value), Condition.Operator.NOT_IN);
        currentWrapper.conditions.add(condition);
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public List<Wrapper> getAnds() {
        return ands;
    }

    public List<Wrapper> getOrs() {
        return ors;
    }

    private String join(Object... args) {
        StringBuilder sb = new StringBuilder();
        for (Object obj : args) {
            sb.append(",");
            sb.append("'" + obj + "'");
        }
        return sb.toString().substring(1);
    }
}
