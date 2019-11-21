package com.bzdnet.demo.df.core.wrapper;

import com.bzdnet.demo.df.core.model.Condition;
import com.sun.istack.internal.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class QueryWrapper {

    private boolean typeIndicator = true;
    private boolean newIndicator = true;
    List<Condition> ands = new ArrayList<>();
    List<Condition> ors = new ArrayList<>();

    protected QueryWrapper() {
    }

    public QueryWrapper and() {
        this.newIndicator = true;
        this.typeIndicator = true;
        return this;
    }

    public QueryWrapper or() {
        this.newIndicator = true;
        this.typeIndicator = false;
        return this;
    }

    public QueryWrapper eq(String key, @NotNull Object value) {
        Condition condition = getOrAddCondition();
        condition.eq(key, String.valueOf(value));
        return this;
    }

    public QueryWrapper lt(String key, @NotNull Object value) {
        Condition condition = getOrAddCondition();
        condition.lt(key, String.valueOf(value));
        return this;
    }

    public QueryWrapper ltEq(String key, @NotNull Object value) {
        Condition condition = getOrAddCondition();
        condition.ltEq(key, String.valueOf(value));
        return this;
    }

    public QueryWrapper gt(String key, @NotNull Object value) {
        Condition condition = getOrAddCondition();
        condition.gt(key, String.valueOf(value));
        return this;
    }

    public QueryWrapper gtEq(String key, @NotNull Object value) {
        Condition condition = getOrAddCondition();
        condition.gtEq(key, String.valueOf(value));
        return this;
    }

    public QueryWrapper like(String key, @NotNull Object value) {
        Condition condition = getOrAddCondition();
        condition.like(key, String.valueOf(value));
        return this;
    }

    public QueryWrapper leftLike(String key, @NotNull Object value) {
        Condition condition = getOrAddCondition();
        condition.leftLike(key, String.valueOf(value));
        return this;
    }

    public QueryWrapper rightLike(String key, @NotNull Object value) {
        Condition condition = getOrAddCondition();
        condition.rightLike(key, String.valueOf(value));
        return this;
    }

    public QueryWrapper in(String key, String... value) {
        Condition condition = getOrAddCondition();
        condition.in(key, String.join(",",  value) );
        return this;
    }

    public QueryWrapper notEq(String key, @NotNull Object value) {
        Condition condition = getOrAddCondition();
        condition.notEq(key, String.valueOf(value));
        return this;
    }

    public QueryWrapper notIn(String key, @NotNull String... value) {
        Condition condition = getOrAddCondition();
        condition.notIn(key, String.join(",", value));
        return this;
    }

    private Condition getOrAddCondition() {
        Condition condition;
        if (this.typeIndicator) {
            if (this.newIndicator) {
                this.newIndicator = false;
                condition = new Condition();
                this.ands.add(condition);
            } else {
                condition = this.ands.get(this.ands.size() - 1);
            }
        } else {
            if (this.newIndicator) {
                this.newIndicator = false;
                condition = new Condition();
                this.ors.add(condition);
            } else {
                condition = this.ors.get(this.ors.size() - 1);
            }
        }
        return condition;
    }

}
