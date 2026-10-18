package com.jacobzp.tijinzhou.controller;

import com.jacobzp.tijinzhou.common.BaseResponse;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.common.ResultUtils;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.model.dto.practicerecord.PracticeRecordAddRequest;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.service.AppUserService;
import com.jacobzp.tijinzhou.service.PracticeRecordService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 刷题记录
 */
@RestController
@RequestMapping("/practice_record")
public class PracticeRecordController {

    @Resource
    private PracticeRecordService practiceRecordService;

    @Resource
    private AppUserService appUserService;

    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody PracticeRecordAddRequest practiceRecordAddRequest,
            HttpServletRequest request) {
        // 请求判断
        if (practiceRecordAddRequest == null || practiceRecordAddRequest.getQuestionId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 获取登录用户信息
        AppUser loginUser = appUserService.getLoginUser(request);
        // 添加刷题记录
        return ResultUtils.success(practiceRecordService.addRecord(loginUser.getId(),
                practiceRecordAddRequest.getQuestionId()));
    }
}
