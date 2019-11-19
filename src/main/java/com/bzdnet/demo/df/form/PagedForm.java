package com.bzdnet.demo.df.form;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PagedForm extends BaseForm {

    private int pageNo;
    private int pageSize;

}
