package com.jacobzp.tijinzhou.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jacobzp.tijinzhou.common.BaseResponse;
import com.jacobzp.tijinzhou.common.DeleteRequest;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.common.ResultUtils;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.exception.ThrowUtils;
import com.jacobzp.tijinzhou.model.dto.question.QuestionQueryRequest;
import com.jacobzp.tijinzhou.model.dto.questionbank.QuestionBankAddRequest;
import com.jacobzp.tijinzhou.model.dto.questionbank.QuestionBankEditRequest;
import com.jacobzp.tijinzhou.model.dto.questionbank.QuestionBankQueryRequest;
import com.jacobzp.tijinzhou.model.dto.questionbank.QuestionBankUpdateRequest;
import com.jacobzp.tijinzhou.model.entity.Question;
import com.jacobzp.tijinzhou.model.entity.QuestionBank;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.model.vo.QuestionBankVO;
import com.jacobzp.tijinzhou.service.QuestionBankService;
import com.jacobzp.tijinzhou.service.QuestionService;
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
 * 题库接口
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@RestController
@RequestMapping("/question_bank")
@Slf4j
public class QuestionBankController {

    /**
     * 已发布
     */
    private static final int PUBLISHED_STATUS = 1;

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private QuestionService questionService;

    @Resource
    private AppUserService appUserService;

    /**
     * 创建
     *
     * @param questionBankAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionBank(@RequestBody QuestionBankAddRequest questionBankAddRequest,
            HttpServletRequest request) {
        if (questionBankAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionBank questionBank = new QuestionBank();
        questionBank.setTitle(questionBankAddRequest.getTitle());
        questionBank.setDescription(questionBankAddRequest.getDescription());
        questionBank.setPicture(questionBankAddRequest.getPicture());
        AppUser loginUser = appUserService.getLoginUser(request);
        questionBank.setUser_id(loginUser.getId());
        questionBankService.validQuestionBank(questionBank, true);
        boolean result = questionBankService.save(questionBank);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(questionBank.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionBank(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = questionBankService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新
     *
     * @param questionBankUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateQuestionBank(@RequestBody QuestionBankUpdateRequest questionBankUpdateRequest) {
        if (questionBankUpdateRequest == null || questionBankUpdateRequest.getId() == null
                || questionBankUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionBank questionBank = new QuestionBank();
        questionBank.setId(questionBankUpdateRequest.getId());
        questionBank.setTitle(questionBankUpdateRequest.getTitle());
        questionBank.setDescription(questionBankUpdateRequest.getDescription());
        questionBank.setPicture(questionBankUpdateRequest.getPicture());
        questionBank.setUser_id(questionBankUpdateRequest.getUserId());
        questionBankService.validQuestionBank(questionBank, false);
        boolean result = questionBankService.updateById(questionBank);
        return ResultUtils.success(result);
    }

    /**
     * 编辑
     *
     * @param questionBankEditRequest
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestionBank(@RequestBody QuestionBankEditRequest questionBankEditRequest) {
        if (questionBankEditRequest == null || questionBankEditRequest.getId() == null
                || questionBankEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionBank questionBank = new QuestionBank();
        questionBank.setId(questionBankEditRequest.getId());
        questionBank.setTitle(questionBankEditRequest.getTitle());
        questionBank.setDescription(questionBankEditRequest.getDescription());
        questionBank.setPicture(questionBankEditRequest.getPicture());
        questionBankService.validQuestionBank(questionBank, false);
        boolean result = questionBankService.updateById(questionBank);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取。needQueryQuestionList 为 true 时带出已发布题目分页，题目不再嵌所属题库
     *
     * @param questionBankQueryRequest
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionBankVO> getQuestionBankVOById(QuestionBankQueryRequest questionBankQueryRequest) {
        if (questionBankQueryRequest == null || questionBankQueryRequest.getId() == null
                || questionBankQueryRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = questionBankQueryRequest.getId();
        QuestionBank questionBank = questionBankService.getById(id);
        if (questionBank == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QuestionBankVO questionBankVO = questionBankService.getQuestionBankVO(questionBank);
        if (questionBankQueryRequest.isNeedQueryQuestionList()) {
            QuestionQueryRequest questionQueryRequest = new QuestionQueryRequest();
            questionQueryRequest.setQuestionBankId(id);
            questionQueryRequest.setStatus(PUBLISHED_STATUS);
            questionQueryRequest.setCurrent(questionBankQueryRequest.getCurrent());
            questionQueryRequest.setPageSize(questionBankQueryRequest.getPageSize());
            Page<Question> questionPage = questionService.page(
                    new Page<>(questionQueryRequest.getCurrent(), questionQueryRequest.getPageSize()),
                    questionService.getQueryWrapper(questionQueryRequest));
            questionBankVO.setQuestionPage(questionService.getQuestionVOPage(questionPage, false, false));
        }
        return ResultUtils.success(questionBankVO);
    }

    /**
     * 分页获取列表
     *
     * @param questionBankQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionBankVO>> listQuestionBankVOByPage(
            @RequestBody QuestionBankQueryRequest questionBankQueryRequest) {
        if (questionBankQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionBankQueryRequest.getCurrent();
        long size = questionBankQueryRequest.getPageSize();
        Page<QuestionBank> questionBankPage = questionBankService.page(new Page<>(current, size),
                questionBankService.getQueryWrapper(questionBankQueryRequest));
        return ResultUtils.success(questionBankService.getQuestionBankVOPage(questionBankPage));
    }
}
