package com.jacobzp.tijinzhou.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * 签到结果
 */
@Data
public class SignResultVO implements Serializable {

    /**
     * 调用结束后今天是否已签到
     */
    private Boolean signed;

    /**
     * 调用前今天是否已经签过
     */
    private Boolean alreadySigned;

    /**
     * 今天的日期
     */
    private String signDate;

    /**
     * 本月已签到天数，含今天
     */
    private Integer monthSignCount;

    /**
     * 连续签到天数
     */
    private Integer streak;

    private static final long serialVersionUID = 1L;
}
