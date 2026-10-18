package com.jacobzp.tijinzhou.model.dto.practicerecord;

import java.io.Serializable;
import lombok.Data;

/**
 * 记录一次刷题
 */
@Data
public class PracticeRecordAddRequest implements Serializable {

    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
