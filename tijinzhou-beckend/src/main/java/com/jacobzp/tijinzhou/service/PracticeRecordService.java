package com.jacobzp.tijinzhou.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.jacobzp.tijinzhou.model.entity.UserPracticeRecord;
import com.jacobzp.tijinzhou.model.vo.SignDayQuestionVO;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * 刷题记录
 */
public interface PracticeRecordService extends IService<UserPracticeRecord> {

    /**
     * 记录一次刷题。同一用户、同一题、同一天重复提交视为成功。
     *
     * @param userId
     * @param questionId
     * @return
     */
    boolean addRecord(Long userId, Long questionId);

    /**
     * 统计某月每天的刷题数
     *
     * @param userId
     * @param month
     * @return
     */
    Map<LocalDate, Integer> countByMonth(Long userId, YearMonth month);

    /**
     * 某一天刷过的题目，按记录创建时间升序
     *
     * @param userId
     * @param date
     * @return
     */
    List<SignDayQuestionVO> listQuestions(Long userId, LocalDate date);
}
