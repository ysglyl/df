package com.bzdnet.demo.df.model;

import com.bzdnet.demo.df.core.annotation.DbColumn;
import com.bzdnet.demo.df.core.annotation.DbID;
import com.bzdnet.demo.df.core.annotation.DbRelation;
import com.bzdnet.demo.df.core.annotation.DbTable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@DbTable(table = "t_staff")
public class StaffModel {

    @DbID(column = "id_")
    private Long id;
    @DbColumn(column = "staff_no_")
    private String staffNo;

    @DbRelation(type = DbRelation.Type.One2One,
            primary = "user_id_",
            secondary = "id_"
    )
    private UserModel user;

}
