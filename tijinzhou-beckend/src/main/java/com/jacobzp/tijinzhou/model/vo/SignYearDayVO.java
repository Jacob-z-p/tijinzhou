package com.jacobzp.tijinzhou.model.vo;

import java.io.Serializable;
import lombok.Data;

/**
 * 年热力图中的一天
 */
@Data
public class SignYearDayVO implements Serializable {

    /**
     * 日期
     */
    private String date;

    /**
     * 是否签到
     */
    private Boolean signed;

    private static final long serialVersionUID = 1L;
}
