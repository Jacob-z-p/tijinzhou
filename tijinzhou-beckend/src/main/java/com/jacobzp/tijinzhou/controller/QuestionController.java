package com.jacobzp.tijinzhou.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jacobzp.tijinzhou.annotation.AuthCheck;
import com.jacobzp.tijinzhou.common.BaseResponse;
import com.jacobzp.tijinzhou.common.DeleteRequest;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.common.ResultUtils;
import com.jacobzp.tijinzhou.constant.UserConstant;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.exception.ThrowUtils;
import com.jacobzp.tijinzhou.model.dto.question.QuestionAddRequest;
import com.jacobzp.tijinzhou.model.dto.question.QuestionEditRequest;
import com.jacobzp.tijinzhou.model.dto.question.QuestionQueryRequest;
import com.jacobzp.tijinzhou.model.dto.question.QuestionUpdateRequest;
import com.jacobzp.tijinzhou.model.entity.Question;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.model.vo.QuestionVO;
import com.jacobzp.tijinzhou.service.QuestionService;
import com.jacobzp.tijinzhou.service.AppUserService;
import java.util.List;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 题目接口
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionController {

    /**
     * 已发布
     */
    private static final int PUBLISHED_STATUS = 1;

    @Resource
    private QuestionService questionService;

    @Resource
    private AppUserService appUserService;

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest,
            HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        question.setTitle(questionAddRequest.getTitle());
        question.setContent(questionAddRequest.getContent());
        question.setAnswer(questionAddRequest.getAnswer());
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        AppUser loginUser = appUserService.getLoginUser(request);
        question.setUser_id(loginUser.getId());
        questionService.validQuestion(question, true);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(question.getId());
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = questionService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() == null
                || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        question.setId(questionUpdateRequest.getId());
        question.setTitle(questionUpdateRequest.getTitle());
        question.setContent(questionUpdateRequest.getContent());
        question.setAnswer(questionUpdateRequest.getAnswer());
        question.setStatus(questionUpdateRequest.getStatus());
        question.setUser_id(questionUpdateRequest.getUserId());
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        questionService.validQuestion(question, false);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 编辑
     *
     * @param questionEditRequest
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest) {
        if (questionEditRequest == null || questionEditRequest.getId() == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        question.setId(questionEditRequest.getId());
        question.setTitle(questionEditRequest.getTitle());
        question.setContent(questionEditRequest.getContent());
        question.setAnswer(questionEditRequest.getAnswer());
        question.setStatus(questionEditRequest.getStatus());
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        questionService.validQuestion(question, false);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取（仅管理员，含推荐答案和草稿）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<QuestionVO> getQuestionById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, true, true));
    }

    /**
     * 分页获取列表（仅管理员，含推荐答案和草稿）
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionVO>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, true, true));
    }

    /**
     * 根据 id 获取公开视图
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null || question.getStatus() == null || question.getStatus() != PUBLISHED_STATUS) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QuestionVO questionVO = questionService.getQuestionVO(question, false, true);
        return ResultUtils.success(questionVO);
    }

    /**
     * 分页获取公开列表，只含已发布题目
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        questionQueryRequest.setStatus(PUBLISHED_STATUS);
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, false, true));
    }
}
