package com.jacobzp.tijinzhou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jacobzp.tijinzhou.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.jacobzp.tijinzhou.model.entity.QuestionBankQuestion;
import java.util.List;

/**
 * 题库题目服务
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
public interface QuestionBankQuestionService extends IService<QuestionBankQuestion> {

    /**
     * 获取查询条件
     *
     * @param questionBankQuestionQueryRequest
     * @return
     */
    QueryWrapper<QuestionBankQuestion> getQueryWrapper(QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest);

    /**
     * 校验
     *
     * @param questionBankQuestion
     */
    void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion);

    /**
     * 批量创建
     *
     * @param questionBankId
     * @param questionIdList
     * @param userId
     * @return
     */
    boolean saveBatch(Long questionBankId, List<Long> questionIdList, Long userId);
}
