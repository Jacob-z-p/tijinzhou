package com.jacobzp.tijinzhou.model.vo;

import com.jacobzp.tijinzhou.model.entity.AppUser;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户视图（脱敏）
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class AppUserVO implements Serializable {

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
    public static AppUserVO objToVo(AppUser appUser) {
        if (appUser == null) {
            return null;
        }
        AppUserVO appUserVO = new AppUserVO();
        appUserVO.setId(appUser.getId());
        appUserVO.setUserAccount(appUser.getUser_account());
        appUserVO.setUserName(appUser.getUser_name());
        appUserVO.setUserAvatar(appUser.getUser_avatar());
        appUserVO.setUserProfile(appUser.getUser_profile());
        appUserVO.setUserRole(appUser.getUser_role());
        appUserVO.setUserStatus(appUser.getUser_status());
        appUserVO.setCreateTime(appUser.getCreate_time());
        return appUserVO;
    }

    /**
     * 包装类转对象
     *
     * @param appUserVO
     * @return
     */
    public static AppUser voToObj(AppUserVO appUserVO) {
        if (appUserVO == null) {
            return null;
        }
        AppUser appUser = new AppUser();
        appUser.setId(appUserVO.getId());
        appUser.setUser_account(appUserVO.getUserAccount());
        appUser.setUser_name(appUserVO.getUserName());
        appUser.setUser_avatar(appUserVO.getUserAvatar());
        appUser.setUser_profile(appUserVO.getUserProfile());
        appUser.setUser_role(appUserVO.getUserRole());
        appUser.setUser_status(appUserVO.getUserStatus());
        appUser.setCreate_time(appUserVO.getCreateTime());
        return appUser;
    }

    private static final long serialVersionUID = 1L;
}
