package com.jacobzp.tijinzhou.model.dto.question;

import com.jacobzp.tijinzhou.common.PageRequest;
import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题目查询请求
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 排除的 id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

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
     * 创建用户 id
     */
    private Long userId;

    /**
     * 状态：0草稿 1已发布
     */
    private Integer status;

    /**
     * 所属题库 id
     */
    private Long questionBankId;

    private static final long serialVersionUID = 1L;
}
