package com.jacobzp.tijinzhou.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.constant.CommonConstant;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.exception.ThrowUtils;
import com.jacobzp.tijinzhou.mapper.QuestionMapper;
import com.jacobzp.tijinzhou.model.dto.question.QuestionQueryRequest;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.model.entity.Question;
import com.jacobzp.tijinzhou.model.entity.QuestionBank;
import com.jacobzp.tijinzhou.model.entity.QuestionBankQuestion;
import com.jacobzp.tijinzhou.model.vo.AppUserVO;
import com.jacobzp.tijinzhou.model.vo.QuestionBankVO;
import com.jacobzp.tijinzhou.model.vo.QuestionVO;
import com.jacobzp.tijinzhou.service.AppUserService;
import com.jacobzp.tijinzhou.service.QuestionBankQuestionService;
import com.jacobzp.tijinzhou.service.QuestionBankService;
import com.jacobzp.tijinzhou.service.QuestionService;
import com.jacobzp.tijinzhou.utils.SqlUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 题目服务实现
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {

    @Resource
    private QuestionBankQuestionService questionBankQuestionService;

    @Resource
    private QuestionBankService questionBankService;

    @Resource
    private AppUserService appUserService;

    @Override
    public void validQuestion(Question question, boolean add) {
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(question.getTitle()), ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public QuestionVO getQuestionVO(Question question, boolean includeAnswer, boolean includeQuestionBank) {
        if (question == null) {
            return null;
        }
        Map<Long, AppUser> userMap = new HashMap<>();
        Long userId = question.getUser_id();
        if (userId != null && userId > 0) {
            AppUser appUser = appUserService.getById(userId);
            if (appUser != null) {
                userMap.put(userId, appUser);
            }
        }
        QuestionVO questionVO = toQuestionVO(question, userMap, includeAnswer);
        if (includeQuestionBank) {
            fillQuestionBankList(List.of(questionVO));
        }
        return questionVO;
    }

    @Override
    public Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, boolean includeAnswer,
            boolean includeQuestionBank) {
        List<Question> questionList = questionPage.getRecords();
        Page<QuestionVO> voPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollUtil.isEmpty(questionList)) {
            return voPage;
        }
        Set<Long> userIdSet = questionList.stream()
                .map(Question::getUser_id)
                .filter(userId -> userId != null && userId > 0)
                .collect(Collectors.toSet());
        Map<Long, AppUser> userMap = new HashMap<>();
        if (CollUtil.isNotEmpty(userIdSet)) {
            appUserService.listByIds(userIdSet).forEach(appUser -> userMap.put(appUser.getId(), appUser));
        }
        List<QuestionVO> voList = questionList.stream()
                .map(question -> toQuestionVO(question, userMap, includeAnswer))
                .collect(Collectors.toList());
        if (includeQuestionBank) {
            fillQuestionBankList(voList);
        }
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 按题目 id 批量补所属题库，题库上不再嵌题目
     *
     * @param questionVOList
     */
    private void fillQuestionBankList(List<QuestionVO> questionVOList) {
        Set<Long> questionIdSet = questionVOList.stream()
                .map(QuestionVO::getId)
                .filter(questionId -> questionId != null && questionId > 0)
                .collect(Collectors.toSet());
        Map<Long, List<Long>> questionIdBankIdListMap = new HashMap<>();
        Set<Long> bankIdSet = new HashSet<>();
        if (CollUtil.isNotEmpty(questionIdSet)) {
            List<QuestionBankQuestion> relationList = questionBankQuestionService.list(
                    new QueryWrapper<QuestionBankQuestion>().in("question_id", questionIdSet));
            for (QuestionBankQuestion relation : relationList) {
                Long questionId = relation.getQuestion_id();
                Long bankId = relation.getQuestion_bank_id();
                if (questionId == null || bankId == null || bankId <= 0) {
                    continue;
                }
                questionIdBankIdListMap.computeIfAbsent(questionId, key -> new ArrayList<>()).add(bankId);
                bankIdSet.add(bankId);
            }
        }
        Map<Long, QuestionBank> bankMap = new HashMap<>();
        if (CollUtil.isNotEmpty(bankIdSet)) {
            questionBankService.listByIds(bankIdSet).forEach(questionBank -> bankMap.put(questionBank.getId(), questionBank));
        }
        Set<Long> bankUserIdSet = bankMap.values().stream()
                .map(QuestionBank::getUser_id)
                .filter(userId -> userId != null && userId > 0)
                .collect(Collectors.toSet());
        Map<Long, AppUser> bankUserMap = new HashMap<>();
        if (CollUtil.isNotEmpty(bankUserIdSet)) {
            appUserService.listByIds(bankUserIdSet).forEach(appUser -> bankUserMap.put(appUser.getId(), appUser));
        }
        for (QuestionVO questionVO : questionVOList) {
            List<Long> bankIdList = questionIdBankIdListMap.getOrDefault(questionVO.getId(), List.of());
            List<QuestionBankVO> questionBankVOList = new ArrayList<>();
            for (Long bankId : bankIdList) {
                QuestionBank questionBank = bankMap.get(bankId);
                if (questionBank == null) {
                    continue;
                }
                QuestionBankVO questionBankVO = QuestionBankVO.objToVo(questionBank);
                Long userId = questionBank.getUser_id();
                if (userId != null && bankUserMap.containsKey(userId)) {
                    questionBankVO.setUser(AppUserVO.objToVo(bankUserMap.get(userId)));
                }
                questionBankVOList.add(questionBankVO);
            }
            questionVO.setQuestionBankList(questionBankVOList);
        }
    }

    private QuestionVO toQuestionVO(Question question, Map<Long, AppUser> userMap, boolean includeAnswer) {
        QuestionVO questionVO = QuestionVO.objToVo(question);
        Long userId = question.getUser_id();
        if (userId != null && userMap.containsKey(userId)) {
            questionVO.setUser(AppUserVO.objToVo(userMap.get(userId)));
        }
        if (!includeAnswer) {
            questionVO.setAnswer(null);
        }
        return questionVO;
    }

    @Override
    public QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        if (questionQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = questionQueryRequest.getSearchText();
        String sortField = questionQueryRequest.getSortField();
        String sortOrder = questionQueryRequest.getSortOrder();
        Long id = questionQueryRequest.getId();
        Long notId = questionQueryRequest.getNotId();
        String title = questionQueryRequest.getTitle();
        String content = questionQueryRequest.getContent();
        List<String> tags = questionQueryRequest.getTags();
        Long userId = questionQueryRequest.getUserId();
        Integer status = questionQueryRequest.getStatus();
        Long questionBankId = questionQueryRequest.getQuestionBankId();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(notId != null, "id", notId);
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.eq(status != null, "status", status);
        if (questionBankId != null) {
            List<Long> questionIdList = questionBankQuestionService.list(new QueryWrapper<QuestionBankQuestion>()
                            .eq("question_bank_id", questionBankId))
                    .stream()
                    .map(QuestionBankQuestion::getQuestion_id)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(questionIdList)) {
                queryWrapper.eq("id", -1);
            } else {
                queryWrapper.in("id", questionIdList);
            }
        }
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), CommonConstant.SORT_ORDER_ASC.equals(sortOrder),
                sortField);
        return queryWrapper;
    }
}
