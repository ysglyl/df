package com.bzdnet.demo.df.model;

import com.bzdnet.demo.df.core.annotation.DbColumn;
import com.bzdnet.demo.df.core.annotation.DbID;
import com.bzdnet.demo.df.core.annotation.DbRelation;
import com.bzdnet.demo.df.core.annotation.DbTable;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@DbTable(table = "t_contact")
public class ContactModel {

    @DbID(column = "id_")
    private Long id;
    @DbColumn(column = "phone_")
    private String phone;
    @DbColumn(column = "email_")
    private String email;

    @DbRelation(type = DbRelation.Type.One2One,
            primary = "user_id_",
            secondary = "id_"
    )
    private UserModel user;

}
