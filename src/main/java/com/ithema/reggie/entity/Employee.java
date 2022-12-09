package com.ithema.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;    //身份证号码

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) //修改时间，在新增数据和修改数据的时候需要插入对应的数据
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)    //填充的时候插入字段
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE) //填充的时候插入字段
    private Long updateUser;

}
