package com.jacobzp.tijinzhou.model.dto.appuser;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户创建请求
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class AppUserAddRequest implements Serializable {

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 角色：user/admin
     */
    private String userRole;

    /**
     * 状态：0正常 1封禁
     */
    private Integer userStatus;

    private static final long serialVersionUID = 1L;
}
