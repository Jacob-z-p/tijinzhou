package com.jacobzp.tijinzhou.service.impl;

import com.jacobzp.tijinzhou.model.vo.SignCalendarDayVO;
import com.jacobzp.tijinzhou.model.vo.SignCalendarVO;
import com.jacobzp.tijinzhou.model.vo.SignDayVO;
import com.jacobzp.tijinzhou.model.vo.SignResultVO;
import com.jacobzp.tijinzhou.service.PracticeRecordService;
import com.jacobzp.tijinzhou.service.SignService;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 签到。一个月一个 Bitmap，偏移是当月第几天减 1。
 */
@Service
public class SignServiceImpl implements SignService {

    private static final DateTimeFormatter MONTH_KEY = DateTimeFormatter.ofPattern("yyyyMM");

    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PracticeRecordService practiceRecordService;

    /**
     * 签到:
     *  1.在redis的BitMap位进行修改
     *  2.封装SignResultVO
     *  3.返回
     * @param userId
     * @return
     */
    @Override
    public SignResultVO sign(Long userId) {
        LocalDate today = LocalDate.now(ZONE);
        Boolean previous = stringRedisTemplate.opsForValue().setBit(redisKey(userId, YearMonth.from(today)),
                today.getDayOfMonth() - 1L, true); // 返回值是修改之前的值
        SignResultVO result = new SignResultVO();
        result.setSigned(true);
        result.setAlreadySigned(Boolean.TRUE.equals(previous));
        result.setSignDate(today.toString());
        result.setMonthSignCount(monthCount(userId, YearMonth.from(today)));
        result.setStreak(streak(userId, today));
        return result;
    }

    @Override
    public SignCalendarVO calendar(Long userId, int year, int month) {
        YearMonth viewed = YearMonth.of(year, month);
        LocalDate today = LocalDate.now(ZONE);
        int length = viewed.lengthOfMonth(); // 月份天数
        long bits = monthBits(userId, viewed); // 从redis中获取每日签到信息
        Map<LocalDate, Integer> practiceCounts = practiceRecordService.countByMonth(userId, viewed); // 根据用户id查询该月刷题数
        List<SignCalendarDayVO> days = new ArrayList<>();
        for (int day = 1; day <= length; day++) {
            LocalDate date = viewed.atDay(day); // 创建日期
            SignCalendarDayVO item = new SignCalendarDayVO();
            item.setDate(date.toString()); // 添加日期
            item.setSigned(signedOn(bits, length, day)); // 判断该天是否签到  -> [0000000000 0000000100 0000000000 0] >> (31-18) ==> [0000000000 00000001] & 1 ==> 1
            item.setPracticeCount(practiceCounts.getOrDefault(date, 0)); // 从map中获取该天刷题数量
            days.add(item);
        }
        SignCalendarVO calendar = new SignCalendarVO();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setToday(today.toString());
        calendar.setSignedToday(signed(userId, today));
        calendar.setMonthSignCount(monthCount(userId, YearMonth.from(today)));
        calendar.setStreak(streak(userId, today));
        calendar.setDays(days);
        return calendar;
    }

    @Override
    public SignDayVO day(Long userId, LocalDate date) {
        SignDayVO day = new SignDayVO();
        day.setDate(date.toString());
        day.setSigned(signed(userId, date));
        day.setQuestions(practiceRecordService.listQuestions(userId, date));
        return day;
    }

    /**
     * 判断今日是否签到 -> 从redis中获取
     * @param userId
     * @param date
     * @return
     */
    private boolean signed(Long userId, LocalDate date) {
        Boolean bit = stringRedisTemplate.opsForValue().getBit(redisKey(userId, YearMonth.from(date)),
                date.getDayOfMonth() - 1L);
        return Boolean.TRUE.equals(bit);
    }

    /**
     * 统计连续签到天数
     * @param userId
     * @param today
     * @return
     */
    private int streak(Long userId, LocalDate today) {
        LocalDate cursor = today;
        if (!signed(userId, cursor)) {
            cursor = cursor.minusDays(1);
            if (!signed(userId, cursor)) {
                return 0;
            }
        }
        int streak = 0;
        YearMonth loaded = null;
        long bits = 0;
        int length = 0;
        while (true) {
            YearMonth month = YearMonth.from(cursor);
            if (!month.equals(loaded)) {
                loaded = month;
                bits = monthBits(userId, month);
                length = month.lengthOfMonth();
            }
            if (!signedOn(bits, length, cursor.getDayOfMonth())) {
                break;
            }
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    /**
     * 统计该月签到天数
     * @param userId
     * @param month
     * @return
     */
    private int monthCount(Long userId, YearMonth month) {
        byte[] key = redisKey(userId, month).getBytes(StandardCharsets.UTF_8);
        Long count = stringRedisTemplate.execute((RedisCallback<Long>) connection -> connection.stringCommands()
                .bitCount(key));
        return count == null ? 0 : count.intValue();
    }

    /**
     * 偏移 0 是返回值的最高位，对应当月 1 号。
     */
    private long monthBits(Long userId, YearMonth month) {
        int days = month.lengthOfMonth(); // 月份天数
        byte[] key = redisKey(userId, month).getBytes(StandardCharsets.UTF_8);
        List<Long> values = stringRedisTemplate.execute((RedisCallback<List<Long>>) connection -> connection
                .stringCommands()
                .bitField(key, BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(days))
                        .valueAt(0))); // 等价于: BITFIELD sign:用户:年月 GET u{天数} 0
        if (values == null || values.isEmpty() || values.get(0) == null) {
            return 0L;
        }
        return values.get(0);
    }

    /**
     * 判断当天是否签到
     * @param bits
     * @param daysInMonth
     * @param dayOfMonth
     * @return
     */
    private boolean signedOn(long bits, int daysInMonth, int dayOfMonth) {
        return ((bits >> (daysInMonth - dayOfMonth)) & 1L) == 1L;
    }

    private String redisKey(Long userId, YearMonth month) {
        return "sign:" + userId + ":" + month.format(MONTH_KEY);
    }
}
