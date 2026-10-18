package com.jacobzp.tijinzhou.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * 月历中的一天
 */
@Data
public class SignCalendarDayVO implements Serializable {

    /**
     * 日期
     */
    private String date;

    /**
     * 是否签到
     */
    private Boolean signed;

    /**
     * 当天刷题数
     */
    private Integer practiceCount;

    private static final long serialVersionUID = 1L;
}
