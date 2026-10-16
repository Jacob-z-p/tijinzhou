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
import com.jacobzp.tijinzhou.model.dto.questionbankquestion.QuestionBankQuestionAddRequest;
import com.jacobzp.tijinzhou.model.dto.questionbankquestion.QuestionBankQuestionBatchAddRequest;
import com.jacobzp.tijinzhou.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.jacobzp.tijinzhou.model.dto.questionbankquestion.QuestionBankQuestionUpdateRequest;
import com.jacobzp.tijinzhou.model.entity.QuestionBankQuestion;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.service.QuestionBankQuestionService;
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
 * 题库题目接口
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@RestController
@RequestMapping("/question_bank_question")
@Slf4j
public class QuestionBankQuestionController {

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Resource
    private AppUserService appUserService;

    /**
     * 创建
     *
     * @param questionBankQuestionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionBankQuestion(
            @RequestBody QuestionBankQuestionAddRequest questionBankQuestionAddRequest, HttpServletRequest request) {
        if (questionBankQuestionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
        questionBankQuestion.setQuestion_bank_id(questionBankQuestionAddRequest.getQuestionBankId());
        questionBankQuestion.setQuestion_id(questionBankQuestionAddRequest.getQuestionId());
        questionBankQuestionService.validQuestionBankQuestion(questionBankQuestion);
        AppUser loginUser = appUserService.getLoginUser(request);
        questionBankQuestion.setUser_id(loginUser.getId());
        boolean result = questionBankQuestionService.save(questionBankQuestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(questionBankQuestion.getId());
    }

    /**
     * 批量创建
     *
     * @param questionBankQuestionBatchAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add/batch")
    public BaseResponse<Boolean> batchAddQuestionBankQuestion(
            @RequestBody QuestionBankQuestionBatchAddRequest questionBankQuestionBatchAddRequest,
            HttpServletRequest request) {
        if (questionBankQuestionBatchAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        AppUser loginUser = appUserService.getLoginUser(request);
        boolean result = questionBankQuestionService.saveBatch(questionBankQuestionBatchAddRequest.getQuestionBankId(),
                questionBankQuestionBatchAddRequest.getQuestionIdList(), loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionBankQuestion(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = questionBankQuestionService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新
     *
     * @param questionBankQuestionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateQuestionBankQuestion(
            @RequestBody QuestionBankQuestionUpdateRequest questionBankQuestionUpdateRequest) {
        if (questionBankQuestionUpdateRequest == null || questionBankQuestionUpdateRequest.getId() == null
                || questionBankQuestionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
        questionBankQuestion.setId(questionBankQuestionUpdateRequest.getId());
        questionBankQuestion.setQuestion_bank_id(questionBankQuestionUpdateRequest.getQuestionBankId());
        questionBankQuestion.setQuestion_id(questionBankQuestionUpdateRequest.getQuestionId());
        boolean result = questionBankQuestionService.updateById(questionBankQuestion);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取（仅管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<QuestionBankQuestion> getQuestionBankQuestionById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionBankQuestion questionBankQuestion = questionBankQuestionService.getById(id);
        if (questionBankQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionBankQuestion);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionBankQuestion>> listQuestionBankQuestionByPage(
            @RequestBody QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        if (questionBankQuestionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionBankQuestionQueryRequest.getCurrent();
        long size = questionBankQuestionQueryRequest.getPageSize();
        Page<QuestionBankQuestion> page = questionBankQuestionService.page(new Page<>(current, size),
                questionBankQuestionService.getQueryWrapper(questionBankQuestionQueryRequest));
        return ResultUtils.success(page);
    }
}
