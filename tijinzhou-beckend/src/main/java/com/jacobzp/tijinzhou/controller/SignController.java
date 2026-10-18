package com.jacobzp.tijinzhou.controller;

import com.jacobzp.tijinzhou.common.BaseResponse;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.common.ResultUtils;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.model.vo.SignCalendarVO;
import com.jacobzp.tijinzhou.model.vo.SignDayVO;
import com.jacobzp.tijinzhou.model.vo.SignResultVO;
import com.jacobzp.tijinzhou.service.AppUserService;
import com.jacobzp.tijinzhou.service.SignService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 签到
 */
@RestController
@RequestMapping("/sign")
public class SignController {

    @Resource
    private SignService signService;

    @Resource
    private AppUserService appUserService;

    @PostMapping("/do")
    public BaseResponse<SignResultVO> doSign(HttpServletRequest request) {
        // 获取登录用户信息
        AppUser loginUser = appUserService.getLoginUser(request);
        // 签到
        return ResultUtils.success(signService.sign(loginUser.getId()));
    }

    /**
     * 查询日历
     * @param year
     * @param month
     * @param request
     * @return
     */
    @GetMapping("/calendar")
    public BaseResponse<SignCalendarVO> calendar(@RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month, HttpServletRequest request) {
        if (year == null || month == null || month < 1 || month > 12 || year < 1 || year > 9999) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        try {
            AppUser loginUser = appUserService.getLoginUser(request);
            return ResultUtils.success(signService.calendar(loginUser.getId(), year, month));
        } catch (DateTimeException exception) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    @GetMapping("/day")
    public BaseResponse<SignDayVO> day(@RequestParam(required = false) String date, HttpServletRequest request) {
        if (date == null || date.isBlank()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LocalDate parsed;
        try {
            parsed = LocalDate.parse(date);
        } catch (DateTimeParseException exception) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AppUser loginUser = appUserService.getLoginUser(request);
        return ResultUtils.success(signService.day(loginUser.getId(), parsed));
    }
}
