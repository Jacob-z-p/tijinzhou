package com.jacobzp.tijinzhou.model.dto.questionbankquestion;

import java.io.Serializable;
import lombok.Data;

/**
 * 题库题目创建请求
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class QuestionBankQuestionAddRequest implements Serializable {

    /**
     * 题库 id
     */
    private Long questionBankId;

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
