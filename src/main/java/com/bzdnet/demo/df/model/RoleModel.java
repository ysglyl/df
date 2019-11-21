package com.bzdnet.demo.df.model;

import com.bzdnet.demo.df.core.annotation.DbColumn;
import com.bzdnet.demo.df.core.annotation.DbID;
import com.bzdnet.demo.df.core.annotation.DbRelation;
import com.bzdnet.demo.df.core.annotation.DbTable;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@DbTable(table = "t_role")
public class RoleModel {

    @DbID(column = "id_")
    private Long id;
    @DbColumn(column = "name_")
    private String name;

    @DbRelation(type = DbRelation.Type.Many2Many,
            refTable = "t_ref_user_role",
            primary = "id_",
            refPrimary = "role_id_",
            refSecondary = "user_id_",
            secondary = "id_"
    )
    private List<UserModel> userList;

}
