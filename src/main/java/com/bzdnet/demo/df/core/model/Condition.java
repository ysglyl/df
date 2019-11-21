package com.bzdnet.demo.df.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Condition {

    List<KeyValuePair> eqs = new ArrayList<>();
    List<KeyValuePair> lts = new ArrayList<>();
    List<KeyValuePair> ltEqs = new ArrayList<>();
    List<KeyValuePair> gts = new ArrayList<>();
    List<KeyValuePair> gtEqs = new ArrayList<>();
    List<KeyValuePair> likes = new ArrayList<>();
    List<KeyValuePair> leftLikes = new ArrayList<>();
    List<KeyValuePair> rightLikes = new ArrayList<>();
    List<KeyValuePair> ins = new ArrayList<>();
    List<KeyValuePair> notEqs = new ArrayList<>();
    List<KeyValuePair> notIns = new ArrayList<>();

    public void eq(String key, String value) {
        this.eqs.add(new KeyValuePair(key, value));
    }

    public void lt(String key, String value) {
        this.lts.add(new KeyValuePair(key, value));
    }

    public void ltEq(String key, String value) {
        this.ltEqs.add(new KeyValuePair(key, value));
    }

    public void gt(String key, String value) {
        this.gts.add(new KeyValuePair(key, value));
    }

    public void gtEq(String key, String value) {
        this.gtEqs.add(new KeyValuePair(key, value));
    }

    public void like(String key, String value) {
        this.likes.add(new KeyValuePair(key, value));
    }

    public void leftLike(String key, String value) {
        this.leftLikes.add(new KeyValuePair(key, value));
    }

    public void rightLike(String key, String value) {
        this.rightLikes.add(new KeyValuePair(key, value));
    }

    public void in(String key, String value) {
        this.ins.add(new KeyValuePair(key, value));
    }

    public void notEq(String key, String value) {
        this.notEqs.add(new KeyValuePair(key, value));
    }

    public void notIn(String key, String value) {
        this.notIns.add(new KeyValuePair(key, value));
    }

}
