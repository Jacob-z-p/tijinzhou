package com.jacobzp.tijinzhou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 题库题目
 * @TableName question_bank_question
 */
@TableName(value ="question_bank_question")
@Data
public class QuestionBankQuestion {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题库 id
     */
    private Long question_bank_id;

    /**
     * 题目 id
     */
    private Long question_id;

    /**
     * 创建用户 id
     */
    private Long user_id;

    /**
     * 创建时间
     */
    private Date create_time;
}