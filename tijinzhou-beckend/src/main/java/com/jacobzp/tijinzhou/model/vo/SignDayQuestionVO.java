package com.jacobzp.tijinzhou.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * 某天刷过的题目
 */
@Data
public class SignDayQuestionVO implements Serializable {

    /**
     * 题目 id
     */
    private Long id;

    /**
     * 题目标题。题目已删除时为「题目已删除」
     */
    private String title;

    private static final long serialVersionUID = 1L;
}
