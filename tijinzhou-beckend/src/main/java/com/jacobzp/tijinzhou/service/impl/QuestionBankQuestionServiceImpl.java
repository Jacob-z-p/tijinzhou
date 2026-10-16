package com.jacobzp.tijinzhou.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.constant.CommonConstant;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.exception.ThrowUtils;
import com.jacobzp.tijinzhou.mapper.QuestionBankQuestionMapper;
import com.jacobzp.tijinzhou.model.dto.questionbankquestion.QuestionBankQuestionQueryRequest;
import com.jacobzp.tijinzhou.model.entity.QuestionBankQuestion;
import com.jacobzp.tijinzhou.service.QuestionBankQuestionService;
import com.jacobzp.tijinzhou.utils.SqlUtils;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 题库题目服务实现
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Service
public class QuestionBankQuestionServiceImpl extends ServiceImpl<QuestionBankQuestionMapper, QuestionBankQuestion>
        implements QuestionBankQuestionService {

    @Override
    public void validQuestionBankQuestion(QuestionBankQuestion questionBankQuestion) {
        if (questionBankQuestion == null || questionBankQuestion.getQuestion_bank_id() == null
                || questionBankQuestion.getQuestion_bank_id() <= 0 || questionBankQuestion.getQuestion_id() == null
                || questionBankQuestion.getQuestion_id() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public boolean saveBatch(Long questionBankId, List<Long> questionIdList, Long userId) {
        if (questionBankId == null || questionBankId <= 0 || CollUtil.isEmpty(questionIdList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        for (Long questionId : questionIdList) {
            QuestionBankQuestion questionBankQuestion = new QuestionBankQuestion();
            questionBankQuestion.setQuestion_bank_id(questionBankId);
            questionBankQuestion.setQuestion_id(questionId);
            questionBankQuestion.setUser_id(userId);
            validQuestionBankQuestion(questionBankQuestion);
            boolean result = this.save(questionBankQuestion);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        }
        return true;
    }

    @Override
    public QueryWrapper<QuestionBankQuestion> getQueryWrapper(
            QuestionBankQuestionQueryRequest questionBankQuestionQueryRequest) {
        QueryWrapper<QuestionBankQuestion> queryWrapper = new QueryWrapper<>();
        if (questionBankQuestionQueryRequest == null) {
            return queryWrapper;
        }
        String sortField = questionBankQuestionQueryRequest.getSortField();
        String sortOrder = questionBankQuestionQueryRequest.getSortOrder();
        Long id = questionBankQuestionQueryRequest.getId();
        Long questionBankId = questionBankQuestionQueryRequest.getQuestionBankId();
        Long questionId = questionBankQuestionQueryRequest.getQuestionId();
        Long userId = questionBankQuestionQueryRequest.getUserId();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(questionBankId != null, "question_bank_id", questionBankId);
        queryWrapper.eq(questionId != null, "question_id", questionId);
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), CommonConstant.SORT_ORDER_ASC.equals(sortOrder),
                sortField);
        return queryWrapper;
    }
}
