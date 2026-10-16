package com.jacobzp.tijinzhou.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.IService;
import com.jacobzp.tijinzhou.model.dto.questionbank.QuestionBankQueryRequest;
import com.jacobzp.tijinzhou.model.entity.QuestionBank;
import com.jacobzp.tijinzhou.model.vo.QuestionBankVO;

/**
 * 题库服务
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
public interface QuestionBankService extends IService<QuestionBank> {

    /**
     * 获取查询条件
     *
     * @param questionBankQueryRequest
     * @return
     */
    QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest);

    /**
     * 校验
     *
     * @param questionBank
     * @param add
     */
    void validQuestionBank(QuestionBank questionBank, boolean add);

    /**
     * 获取题库封装
     *
     * @param questionBank
     * @return
     */
    QuestionBankVO getQuestionBankVO(QuestionBank questionBank);

    /**
     * 分页获取题库封装
     *
     * @param questionBankPage
     * @return
     */
    Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionBankPage);
}
