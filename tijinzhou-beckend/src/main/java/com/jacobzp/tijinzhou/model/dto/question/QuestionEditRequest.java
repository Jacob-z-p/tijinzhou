package com.jacobzp.tijinzhou.model.dto.question;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 题目编辑请求
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class QuestionEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 推荐答案
     */
    private String answer;

    /**
     * 状态：0草稿 1已发布
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
}
