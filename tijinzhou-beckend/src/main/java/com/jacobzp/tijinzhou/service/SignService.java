package com.jacobzp.tijinzhou.service;

import com.jacobzp.tijinzhou.model.vo.SignCalendarVO;
import com.jacobzp.tijinzhou.model.vo.SignDayVO;
import com.jacobzp.tijinzhou.model.vo.SignResultVO;
import com.jacobzp.tijinzhou.model.vo.SignYearVO;
import java.time.LocalDate;

/**
 * 签到
 */
public interface SignService {

    /**
     * 今天签到。重复签到仍成功。
     *
     * @param userId
     * @return
     */
    SignResultVO sign(Long userId);

    /**
     * 月历。连续天数和本月签到数相对今天，与正在查看的月份无关。
     *
     * @param userId
     * @param year
     * @param month
     * @return
     */
    SignCalendarVO calendar(Long userId, int year, int month);

    /**
     * 某年每一天是否签到。只读 Bitmap。
     *
     * @param userId
     * @param year
     * @return
     */
    SignYearVO year(Long userId, int year);

    /**
     * 某一天的签到和刷题题目
     *
     * @param userId
     * @param date
     * @return
     */
    SignDayVO day(Long userId, LocalDate date);
}
