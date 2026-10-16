package com.jacobzp.tijinzhou.aop;

import com.jacobzp.tijinzhou.annotation.AuthCheck;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.model.enums.UserRoleEnum;
import com.jacobzp.tijinzhou.service.AppUserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 权限校验 AOP
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Aspect
@Component
public class AuthInterceptor {

    /**
     * 封禁
     */
    private static final int USER_STATUS_BAN = 1;

    @Resource
    private AppUserService appUserService;

    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        AppUser loginUser = appUserService.getLoginUser(request);
        if (Integer.valueOf(USER_STATUS_BAN).equals(loginUser.getUser_status())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "用户已封禁");
        }
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUser_role());
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed();
    }
}
