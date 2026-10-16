package com.jacobzp.tijinzhou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jacobzp.tijinzhou.model.dto.appuser.AppUserQueryRequest;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.model.vo.AppUserAdminVO;
import com.jacobzp.tijinzhou.model.vo.AppUserVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
public interface AppUserService extends IService<AppUser> {

    /**
     * 获取查询条件
     *
     * @param appUserQueryRequest
     * @return
     */
    QueryWrapper<AppUser> getQueryWrapper(AppUserQueryRequest appUserQueryRequest);

    /**
     * 校验。创建时账号不能为空，密码为空则写入占位值
     *
     * @param appUser
     * @param add
     */
    void validAppUser(AppUser appUser, boolean add);

    /**
     * 获取公开视图
     *
     * @param appUser
     * @return
     */
    AppUserVO getAppUserVO(AppUser appUser);

    /**
     * 分页获取公开视图
     *
     * @param appUserPage
     * @return
     */
    Page<AppUserVO> getAppUserVOPage(Page<AppUser> appUserPage);

    /**
     * 获取管理视图
     *
     * @param appUser
     * @return
     */
    AppUserAdminVO getAppUserAdminVO(AppUser appUser);

    /**
     * 分页获取管理视图
     *
     * @param appUserPage
     * @return
     */
    Page<AppUserAdminVO> getAppUserAdminVOPage(Page<AppUser> appUserPage);

    /**
     * 注册
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 登录
     *
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    AppUser userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    AppUser getLoginUser(HttpServletRequest request);

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);
}
