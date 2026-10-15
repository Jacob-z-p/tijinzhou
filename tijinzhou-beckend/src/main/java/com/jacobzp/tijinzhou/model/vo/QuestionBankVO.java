package com.jacobzp.tijinzhou.model.vo;

import com.jacobzp.tijinzhou.model.entity.QuestionBank;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 题库视图
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class QuestionBankVO implements Serializable {

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

    /**
     * 创建用户 id
     */
    private Long userId;

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
     * 对象转包装类
     *
     * @param questionBank
     * @return
     */
    public static QuestionBankVO objToVo(QuestionBank questionBank) {
        if (questionBank == null) {
            return null;
        }
        QuestionBankVO questionBankVO = new QuestionBankVO();
        questionBankVO.setId(questionBank.getId());
        questionBankVO.setTitle(questionBank.getTitle());
        questionBankVO.setDescription(questionBank.getDescription());
        questionBankVO.setPicture(questionBank.getPicture());
        questionBankVO.setUserId(questionBank.getUser_id());
        questionBankVO.setCreateTime(questionBank.getCreate_time());
        questionBankVO.setUpdateTime(questionBank.getUpdate_time());
        return questionBankVO;
    }

    /**
     * 包装类转对象
     *
     * @param questionBankVO
     * @return
     */
    public static QuestionBank voToObj(QuestionBankVO questionBankVO) {
        if (questionBankVO == null) {
            return null;
        }
        QuestionBank questionBank = new QuestionBank();
        questionBank.setId(questionBankVO.getId());
        questionBank.setTitle(questionBankVO.getTitle());
        questionBank.setDescription(questionBankVO.getDescription());
        questionBank.setPicture(questionBankVO.getPicture());
        questionBank.setUser_id(questionBankVO.getUserId());
        questionBank.setCreate_time(questionBankVO.getCreateTime());
        questionBank.setUpdate_time(questionBankVO.getUpdateTime());
        return questionBank;
    }

    private static final long serialVersionUID = 1L;
}
