package com.jacobzp.tijinzhou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jacobzp.tijinzhou.model.dto.question.QuestionQueryRequest;
import com.jacobzp.tijinzhou.model.entity.Question;
import com.jacobzp.tijinzhou.model.vo.QuestionVO;

/**
 * 题目服务
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
public interface QuestionService extends IService<Question> {

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取题目封装
     *
     * @param question
     * @param includeAnswer
     * @param includeQuestionBank
     * @return
     */
    QuestionVO getQuestionVO(Question question, boolean includeAnswer, boolean includeQuestionBank);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param includeAnswer
     * @param includeQuestionBank
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, boolean includeAnswer, boolean includeQuestionBank);
}
