package com.jacobzp.tijinzhou.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.util.Date;
import lombok.Data;

/**
 * 用户刷题记录
 *
 * @TableName user_practice_record
 */
@TableName(value = "user_practice_record")
@Data
public class UserPracticeRecord {

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户 id
     */
    private Long user_id;

    /**
     * 题目 id
     */
    private Long question_id;

    /**
     * 刷题日期，按 Asia/Shanghai 自然日
     */
    private LocalDate practice_date;

    /**
     * 创建时间
     */
    private Date create_time;
}
