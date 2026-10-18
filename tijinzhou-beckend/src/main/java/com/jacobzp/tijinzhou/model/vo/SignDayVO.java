package com.jacobzp.tijinzhou.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 某一天的签到和刷题
 */
@Data
public class SignDayVO implements Serializable {

    /**
     * 日期
     */
    private String date;

    /**
     * 是否签到
     */
    private Boolean signed;

    /**
     * 当天题目，按刷题时间升序
     */
    private List<SignDayQuestionVO> questions;

    private static final long serialVersionUID = 1L;
}
