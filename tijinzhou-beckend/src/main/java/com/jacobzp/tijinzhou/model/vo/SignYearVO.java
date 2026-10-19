package com.jacobzp.tijinzhou.model.vo;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * 一年签到
 */
@Data
public class SignYearVO implements Serializable {

    /**
     * 年
     */
    private Integer year;

    /**
     * 今天，Asia/Shanghai
     */
    private String today;

    /**
     * 该年每一天
     */
    private List<SignYearDayVO> days;

    private static final long serialVersionUID = 1L;
}
