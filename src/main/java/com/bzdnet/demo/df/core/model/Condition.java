package com.bzdnet.demo.df.core.model;

import lombok.Data;

@Data
public class Condition {

    public enum Operator {
        EQ("='{}'"), LT("<'{}'"), LT_EQ("<='{}'"), GT(">'{}'"), GT_EQ(">='{}'"), LIKE("like '%{}%'"), LEFT_LIKE("like '%{}'"), RIGHT_LIKE("like '{}%'"), IN("in ({})"), NOT_EQ("!='{}'"), NOT_IN("not in ({})");

        private String operator;

        Operator(String operator) {
            this.operator = operator;
        }

        public String getOperator() {
            return this.operator;
        }
    }

    private String table;
    private String column;
    private Object value;
    private Operator operator;

    public Condition(String table, String column, Object value, Operator operator) {
        this.table = table;
        this.column = column;
        this.value = value;
        this.operator = operator;
    }
}
