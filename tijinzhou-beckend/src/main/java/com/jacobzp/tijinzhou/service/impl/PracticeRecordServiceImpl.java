package com.jacobzp.tijinzhou.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.mapper.UserPracticeRecordMapper;
import com.jacobzp.tijinzhou.model.entity.Question;
import com.jacobzp.tijinzhou.model.entity.UserPracticeRecord;
import com.jacobzp.tijinzhou.model.vo.SignDayQuestionVO;
import com.jacobzp.tijinzhou.service.PracticeRecordService;
import com.jacobzp.tijinzhou.service.QuestionService;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 刷题记录
 */
@Service
public class PracticeRecordServiceImpl extends ServiceImpl<UserPracticeRecordMapper, UserPracticeRecord>
        implements PracticeRecordService {

    static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    static final String DELETED_TITLE = "题目已删除";

    @Resource
    private QuestionService questionService;

    /**
     * 添加刷题记录
     * @param userId
     * @param questionId
     * @return
     */
    @Override
    public boolean addRecord(Long userId, Long questionId) {
        // 各种判读
        if (userId == null || userId <= 0 || questionId == null || questionId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 添加刷题记录
        UserPracticeRecord record = new UserPracticeRecord();
        record.setUser_id(userId);
        record.setQuestion_id(questionId);
        record.setPractice_date(LocalDate.now(ZONE));
        try {
            boolean saved = this.save(record); // 添加刷题记录到数据库 [user_practice_record]
            if (!saved) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        } catch (DuplicateKeyException ignored) {
            return true;
        }
        return true;
    }

    /**
     * 按月统计刷题数量
     * @param userId
     * @param month
     * @return
     */
    @Override
    public Map<LocalDate, Integer> countByMonth(Long userId, YearMonth month) {
        LocalDate start = month.atDay(1); // 记录了年月日
        LocalDate end = month.atEndOfMonth(); // 记录了年月日
        // 创建查询条件
        QueryWrapper<UserPracticeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.ge("practice_date", start);
        queryWrapper.le("practice_date", end);
        // 数据库查询
        List<UserPracticeRecord> records = this.list(queryWrapper);
        // 内存中统计
        Map<LocalDate, Integer> counts = new HashMap<>();
        for (UserPracticeRecord record : records) {
            counts.merge(record.getPractice_date(), 1, Integer::sum);
        }
        return counts;
    }

    @Override
    public List<SignDayQuestionVO> listQuestions(Long userId, LocalDate date) {
        QueryWrapper<UserPracticeRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("practice_date", date);
        queryWrapper.orderByAsc("create_time", "id");
        List<UserPracticeRecord> records = this.list(queryWrapper);
        if (records.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> questionIds = records.stream().map(UserPracticeRecord::getQuestion_id).toList();
        Map<Long, Question> questionMap = questionService.listByIds(questionIds).stream()
                .collect(Collectors.toMap(Question::getId, Function.identity(), (left, right) -> left));
        List<SignDayQuestionVO> questions = new ArrayList<>();
        for (UserPracticeRecord record : records) {
            SignDayQuestionVO item = new SignDayQuestionVO();
            item.setId(record.getQuestion_id());
            Question question = questionMap.get(record.getQuestion_id());
            item.setTitle(question == null || question.getTitle() == null ? DELETED_TITLE : question.getTitle());
            questions.add(item);
        }
        return questions;
    }
}
