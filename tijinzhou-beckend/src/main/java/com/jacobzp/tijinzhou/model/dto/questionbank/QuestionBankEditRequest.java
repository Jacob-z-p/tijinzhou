package com.jacobzp.tijinzhou.model.dto.questionbank;

import java.io.Serializable;
import lombok.Data;

/**
 * 题库编辑请求
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class QuestionBankEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片
     */
    private String picture;

    private static final long serialVersionUID = 1L;
}
