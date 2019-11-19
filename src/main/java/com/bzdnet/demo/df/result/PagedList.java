package com.bzdnet.demo.df.result;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class PagedList<T> {

    private int pageNo;
    private int pageSize;
    private int pageCount;
    private long total;
    private List<T> list;

}
