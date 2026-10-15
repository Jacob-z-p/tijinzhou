package com.jacobzp.tijinzhou.model.dto.questionbankquestion;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 题库题目批量创建请求
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class QuestionBankQuestionBatchAddRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id 列表
     */
    private List<Long> questionIdList;

    private static final long serialVersionUID = 1L;
}
