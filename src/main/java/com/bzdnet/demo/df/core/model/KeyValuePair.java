package com.bzdnet.demo.df.core.model;

import lombok.Data;

@Data
public class KeyValuePair {


    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    private String key;
    private String value;

}
