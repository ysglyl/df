package com.bzdnet.demo.df.core;

import lombok.Data;

import java.util.List;

@Data
public class ResultMap {

    private ResultID resultID;
    private List<ResultSimple> simpleList;
    private List<ResultAssociation> associationList;
    private List<ResultCollection> collectionList;

}
