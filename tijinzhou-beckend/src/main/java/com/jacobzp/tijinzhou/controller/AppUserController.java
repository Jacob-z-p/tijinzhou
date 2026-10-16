package com.jacobzp.tijinzhou.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jacobzp.tijinzhou.annotation.AuthCheck;
import com.jacobzp.tijinzhou.common.BaseResponse;
import com.jacobzp.tijinzhou.common.DeleteRequest;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.common.ResultUtils;
import com.jacobzp.tijinzhou.constant.UserConstant;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.exception.ThrowUtils;
import com.jacobzp.tijinzhou.model.dto.appuser.AppUserAddRequest;
import com.jacobzp.tijinzhou.model.dto.appuser.AppUserEditRequest;
import com.jacobzp.tijinzhou.model.dto.appuser.AppUserQueryRequest;
import com.jacobzp.tijinzhou.model.dto.appuser.AppUserUpdateRequest;
import com.jacobzp.tijinzhou.model.dto.user.UserLoginRequest;
import com.jacobzp.tijinzhou.model.dto.user.UserRegisterRequest;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.model.vo.AppUserAdminVO;
import com.jacobzp.tijinzhou.model.vo.AppUserVO;
import com.jacobzp.tijinzhou.service.AppUserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@RestController
@RequestMapping("/app_user")
@Slf4j
public class AppUserController {

    @Resource
    private AppUserService appUserService;

    /**
     * 注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<AppUserAdminVO> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = appUserService.userRegister(userRegisterRequest.getUserAccount(),
                userRegisterRequest.getUserPassword(), userRegisterRequest.getCheckPassword());
        AppUser appUser = appUserService.getById(userId);
        return ResultUtils.success(appUserService.getAppUserAdminVO(appUser));
    }

    /**
     * 登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<AppUserAdminVO> userLogin(@RequestBody UserLoginRequest userLoginRequest,
            HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AppUser appUser = appUserService.userLogin(userLoginRequest.getUserAccount(),
                userLoginRequest.getUserPassword(), request);
        return ResultUtils.success(appUserService.getAppUserAdminVO(appUser));
    }

    /**
     * 退出登录
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(!appUserService.userLogout(request), ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<AppUserAdminVO> getLoginUser(HttpServletRequest request) {
        AppUser appUser = appUserService.getLoginUser(request);
        return ResultUtils.success(appUserService.getAppUserAdminVO(appUser));
    }

    /**
     * 创建
     *
     * @param appUserAddRequest
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addAppUser(@RequestBody AppUserAddRequest appUserAddRequest) {
        if (appUserAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AppUser appUser = new AppUser();
        appUser.setUser_account(appUserAddRequest.getUserAccount());
        appUser.setUser_name(appUserAddRequest.getUserName());
        appUser.setUser_avatar(appUserAddRequest.getUserAvatar());
        appUser.setUser_profile(appUserAddRequest.getUserProfile());
        appUser.setUser_role(appUserAddRequest.getUserRole());
        appUser.setUser_status(appUserAddRequest.getUserStatus());
        appUserService.validAppUser(appUser, true);
        boolean result = appUserService.save(appUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(appUser.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAppUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = appUserService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新
     *
     * @param appUserUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateAppUser(@RequestBody AppUserUpdateRequest appUserUpdateRequest) {
        if (appUserUpdateRequest == null || appUserUpdateRequest.getId() == null || appUserUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AppUser appUser = new AppUser();
        appUser.setId(appUserUpdateRequest.getId());
        appUser.setUser_account(appUserUpdateRequest.getUserAccount());
        appUser.setUser_name(appUserUpdateRequest.getUserName());
        appUser.setUser_avatar(appUserUpdateRequest.getUserAvatar());
        appUser.setUser_profile(appUserUpdateRequest.getUserProfile());
        appUser.setUser_role(appUserUpdateRequest.getUserRole());
        appUser.setUser_status(appUserUpdateRequest.getUserStatus());
        appUserService.validAppUser(appUser, false);
        boolean result = appUserService.updateById(appUser);
        return ResultUtils.success(result);
    }

    /**
     * 编辑
     *
     * @param appUserEditRequest
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editAppUser(@RequestBody AppUserEditRequest appUserEditRequest) {
        if (appUserEditRequest == null || appUserEditRequest.getId() == null || appUserEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AppUser appUser = new AppUser();
        appUser.setId(appUserEditRequest.getId());
        appUser.setUser_name(appUserEditRequest.getUserName());
        appUser.setUser_avatar(appUserEditRequest.getUserAvatar());
        appUser.setUser_profile(appUserEditRequest.getUserProfile());
        appUserService.validAppUser(appUser, false);
        boolean result = appUserService.updateById(appUser);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取（仅管理员，含账号和状态，不含密码）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<AppUserAdminVO> getAppUserById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AppUser appUser = appUserService.getById(id);
        if (appUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(appUserService.getAppUserAdminVO(appUser));
    }

    /**
     * 分页获取列表（仅管理员，含账号和状态，不含密码）
     *
     * @param appUserQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AppUserAdminVO>> listAppUserByPage(@RequestBody AppUserQueryRequest appUserQueryRequest) {
        if (appUserQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = appUserQueryRequest.getCurrent();
        long size = appUserQueryRequest.getPageSize();
        Page<AppUser> appUserPage = appUserService.page(new Page<>(current, size),
                appUserService.getQueryWrapper(appUserQueryRequest));
        return ResultUtils.success(appUserService.getAppUserAdminVOPage(appUserPage));
    }

    /**
     * 根据 id 获取公开视图
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppUserVO> getAppUserVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AppUser appUser = appUserService.getById(id);
        if (appUser == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(appUserService.getAppUserVO(appUser));
    }

    /**
     * 分页获取公开列表
     *
     * @param appUserQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AppUserVO>> listAppUserVOByPage(@RequestBody AppUserQueryRequest appUserQueryRequest) {
        if (appUserQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = appUserQueryRequest.getCurrent();
        long size = appUserQueryRequest.getPageSize();
        Page<AppUser> appUserPage = appUserService.page(new Page<>(current, size),
                appUserService.getQueryWrapper(appUserQueryRequest));
        return ResultUtils.success(appUserService.getAppUserVOPage(appUserPage));
    }
}
