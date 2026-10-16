package com.jacobzp.tijinzhou.model.vo;

import com.jacobzp.tijinzhou.model.entity.AppUser;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户管理视图，不含密码
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class AppUserAdminVO implements Serializable {

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 对象转包装类
     *
     * @param appUser
     * @return
     */
    public static AppUserAdminVO objToVo(AppUser appUser) {
        if (appUser == null) {
            return null;
        }
        AppUserAdminVO appUserAdminVO = new AppUserAdminVO();
        appUserAdminVO.setId(appUser.getId());
        appUserAdminVO.setUserAccount(appUser.getUser_account());
        appUserAdminVO.setUserName(appUser.getUser_name());
        appUserAdminVO.setUserAvatar(appUser.getUser_avatar());
        appUserAdminVO.setUserProfile(appUser.getUser_profile());
        appUserAdminVO.setUserRole(appUser.getUser_role());
        appUserAdminVO.setUserStatus(appUser.getUser_status());
        appUserAdminVO.setCreateTime(appUser.getCreate_time());
        return appUserAdminVO;
    }

    private static final long serialVersionUID = 1L;
}
