package com.bzdnet.demo.df.model;

import com.bzdnet.demo.df.annotation.DbColumn;
import com.bzdnet.demo.df.annotation.DbID;
import com.bzdnet.demo.df.annotation.DbRelation;
import com.bzdnet.demo.df.annotation.DbTable;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@DbTable(table = "t_user")
public class UserModel {

    @DbID(column = "id_")
    private Long id;
    @DbColumn(column = "account_")
    private String account;
    @DbColumn(column = "name_")
    private String name;
    @DbColumn(column = "age_")
    private int age;

    @DbRelation(type = DbRelation.Type.Many2Many,
            refTable = "t_ref_user_role",
            secondaryTable = "t_role",
            primary = "id_",
            refPrimary = "user_id_",
            refSecondary = "role_id_",
            secondary = "id_"
    )
    private List<RoleModel> roleList;

    @DbRelation(type = DbRelation.Type.One2Many,
            secondaryTable = "t_contact",
            primary = "id_",
            secondary = "user_id_"
    )
    private ContactModel contact;


}