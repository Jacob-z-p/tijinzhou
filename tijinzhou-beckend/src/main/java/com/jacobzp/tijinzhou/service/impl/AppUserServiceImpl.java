package com.jacobzp.tijinzhou.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.constant.CommonConstant;
import com.jacobzp.tijinzhou.constant.UserConstant;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.exception.ThrowUtils;
import com.jacobzp.tijinzhou.mapper.AppUserMapper;
import com.jacobzp.tijinzhou.model.dto.appuser.AppUserQueryRequest;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.model.vo.AppUserAdminVO;
import com.jacobzp.tijinzhou.model.vo.AppUserVO;
import com.jacobzp.tijinzhou.service.AppUserService;
import com.jacobzp.tijinzhou.service.impl.UserServiceImpl;
import com.jacobzp.tijinzhou.utils.SqlUtils;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

/**
 * 用户服务实现
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Service
@Slf4j
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser>
        implements AppUserService {

    /**
     * 创建用户时写入的占位密码，正式注册和加密以后替换
     */
    private static final String PLACEHOLDER_PASSWORD = "placeholder";

    /**
     * 封禁
     */
    private static final int USER_STATUS_BAN = 1;

    @Override
    public void validAppUser(AppUser appUser, boolean add) {
        if (appUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(appUser.getUser_account()), ErrorCode.PARAMS_ERROR);
            if (StringUtils.isBlank(appUser.getUser_password())) {
                appUser.setUser_password(PLACEHOLDER_PASSWORD);
            }
        }
    }

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            QueryWrapper<AppUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_account", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            String encryptPassword = DigestUtils.md5DigestAsHex((UserServiceImpl.SALT + userPassword).getBytes());
            AppUser appUser = new AppUser();
            appUser.setUser_account(userAccount);
            appUser.setUser_password(encryptPassword);
            appUser.setUser_role(UserConstant.DEFAULT_ROLE);
            appUser.setUser_status(0);
            boolean saveResult = this.save(appUser);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return appUser.getId();
        }
    }

    @Override
    public AppUser userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4 || userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        String encryptPassword = DigestUtils.md5DigestAsHex((UserServiceImpl.SALT + userPassword).getBytes());
        QueryWrapper<AppUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encryptPassword);
        AppUser appUser = this.baseMapper.selectOne(queryWrapper);
        if (appUser == null) {
            log.info("app user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        if (Integer.valueOf(USER_STATUS_BAN).equals(appUser.getUser_status())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户已封禁");
        }
        request.getSession().setAttribute(UserConstant.APP_USER_LOGIN_STATE, appUser);
        return appUser;
    }

    @Override
    public AppUser getLoginUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(UserConstant.APP_USER_LOGIN_STATE);
        if (!(userObj instanceof AppUser)) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        AppUser currentUser = (AppUser) userObj;
        if (currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        currentUser = this.getById(currentUser.getId());
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        if (Integer.valueOf(USER_STATUS_BAN).equals(currentUser.getUser_status())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户已封禁");
        }
        return currentUser;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(UserConstant.APP_USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        request.getSession().removeAttribute(UserConstant.APP_USER_LOGIN_STATE);
        return true;
    }

    @Override
    public AppUserVO getAppUserVO(AppUser appUser) {
        return AppUserVO.objToVo(appUser);
    }

    @Override
    public Page<AppUserVO> getAppUserVOPage(Page<AppUser> appUserPage) {
        Page<AppUserVO> voPage = new Page<>(appUserPage.getCurrent(), appUserPage.getSize(), appUserPage.getTotal());
        List<AppUser> appUserList = appUserPage.getRecords();
        if (CollUtil.isEmpty(appUserList)) {
            return voPage;
        }
        voPage.setRecords(appUserList.stream().map(AppUserVO::objToVo).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public AppUserAdminVO getAppUserAdminVO(AppUser appUser) {
        return AppUserAdminVO.objToVo(appUser);
    }

    @Override
    public Page<AppUserAdminVO> getAppUserAdminVOPage(Page<AppUser> appUserPage) {
        Page<AppUserAdminVO> voPage = new Page<>(appUserPage.getCurrent(), appUserPage.getSize(),
                appUserPage.getTotal());
        List<AppUser> appUserList = appUserPage.getRecords();
        if (CollUtil.isEmpty(appUserList)) {
            return voPage;
        }
        voPage.setRecords(appUserList.stream().map(AppUserAdminVO::objToVo).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public QueryWrapper<AppUser> getQueryWrapper(AppUserQueryRequest appUserQueryRequest) {
        QueryWrapper<AppUser> queryWrapper = new QueryWrapper<>();
        if (appUserQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = appUserQueryRequest.getSortField();
        String sortOrder = appUserQueryRequest.getSortOrder();
        Long id = appUserQueryRequest.getId();
        String userAccount = appUserQueryRequest.getUserAccount();
        String userName = appUserQueryRequest.getUserName();
        String userProfile = appUserQueryRequest.getUserProfile();
        String userRole = appUserQueryRequest.getUserRole();
        Integer userStatus = appUserQueryRequest.getUserStatus();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "user_account", userAccount);
        queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "user_profile", userProfile);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "user_role", userRole);
        queryWrapper.eq(userStatus != null, "user_status", userStatus);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), CommonConstant.SORT_ORDER_ASC.equals(sortOrder),
                sortField);
        return queryWrapper;
    }
}
