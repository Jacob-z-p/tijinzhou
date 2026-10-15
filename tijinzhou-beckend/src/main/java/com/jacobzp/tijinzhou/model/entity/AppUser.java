package com.jacobzp.tijinzhou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户
 * @TableName app_user
 */
@TableName(value ="app_user")
@Data
public class AppUser {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 账号
     */
    private String user_account;

    /**
     * 密码哈希，不存明文
     */
    private String user_password;

    /**
     * 用户昵称
     */
    private String user_name;

    /**
     * 用户头像
     */
    private String user_avatar;

    /**
     * 用户简介
     */
    private String user_profile;

    /**
     * 角色：user/admin
     */
    private String user_role;

    /**
     * 状态：0正常 1封禁
     */
    private Integer user_status;

    /**
     * 创建时间
     */
    private Date create_time;

    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 0未删除，删除后写入本行id
     */
    @TableLogic(value = "0", delval = "id")
    private Long is_delete;
}