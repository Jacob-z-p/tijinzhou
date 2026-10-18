package com.jacobzp.tijinzhou.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 签到月历
 */
@Data
public class SignCalendarVO implements Serializable {

    /**
     * 年
     */
    private Integer year;

    /**
     * 月，1-12
     */
    private Integer month;

    /**
     * 今天
     */
    private String today;

    /**
     * 今天是否已签到
     */
    private Boolean signedToday;

    /**
     * 本月已签到天数，相对今天所在月
     */
    private Integer monthSignCount;

    /**
     * 连续签到天数
     */
    private Integer streak;

    /**
     * 当月每一天
     */
    private List<SignCalendarDayVO> days;

    private static final long serialVersionUID = 1L;
}
