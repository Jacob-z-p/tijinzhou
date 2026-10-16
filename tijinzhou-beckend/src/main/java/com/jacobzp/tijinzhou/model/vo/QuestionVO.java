package com.jacobzp.tijinzhou.model.vo;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jacobzp.tijinzhou.model.entity.Question;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 题目视图
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class QuestionVO implements Serializable {

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
    private List<String> tagList;

    /**
     * 推荐答案。公开接口不返回，空值不出现在 JSON 里
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String answer;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 状态：0草稿 1已发布
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人信息
     */
    private AppUserVO user;

    /**
     * 所属题库列表。嵌在题库详情里的题目不填，避免循环
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<QuestionBankVO> questionBankList;

    /**
     * 对象转包装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        questionVO.setId(question.getId());
        questionVO.setTitle(question.getTitle());
        questionVO.setContent(question.getContent());
        questionVO.setAnswer(question.getAnswer());
        questionVO.setUserId(question.getUser_id());
        questionVO.setStatus(question.getStatus());
        questionVO.setCreateTime(question.getCreate_time());
        questionVO.setUpdateTime(question.getUpdate_time());
        Object tags = question.getTags();
        if (tags != null && StringUtils.isNotBlank(tags.toString())) {
            questionVO.setTagList(JSONUtil.toList(tags.toString(), String.class));
        }
        return questionVO;
    }

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        question.setId(questionVO.getId());
        question.setTitle(questionVO.getTitle());
        question.setContent(questionVO.getContent());
        question.setAnswer(questionVO.getAnswer());
        question.setUser_id(questionVO.getUserId());
        question.setStatus(questionVO.getStatus());
        question.setCreate_time(questionVO.getCreateTime());
        question.setUpdate_time(questionVO.getUpdateTime());
        List<String> tagList = questionVO.getTagList();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        return question;
    }

    private static final long serialVersionUID = 1L;
}
