package com.jacobzp.tijinzhou.model.dto.appuser;

import com.jacobzp.tijinzhou.common.PageRequest;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询请求
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppUserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

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
