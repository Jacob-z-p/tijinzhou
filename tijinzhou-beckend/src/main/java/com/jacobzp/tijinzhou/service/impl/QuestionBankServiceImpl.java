package com.jacobzp.tijinzhou.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jacobzp.tijinzhou.common.ErrorCode;
import com.jacobzp.tijinzhou.constant.CommonConstant;
import com.jacobzp.tijinzhou.exception.BusinessException;
import com.jacobzp.tijinzhou.exception.ThrowUtils;
import com.jacobzp.tijinzhou.mapper.QuestionBankMapper;
import com.jacobzp.tijinzhou.model.dto.questionbank.QuestionBankQueryRequest;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import com.jacobzp.tijinzhou.model.entity.QuestionBank;
import com.jacobzp.tijinzhou.model.vo.AppUserVO;
import com.jacobzp.tijinzhou.model.vo.QuestionBankVO;
import com.jacobzp.tijinzhou.service.AppUserService;
import com.jacobzp.tijinzhou.service.QuestionBankService;
import com.jacobzp.tijinzhou.utils.SqlUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * 题库服务实现
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Service
public class QuestionBankServiceImpl extends ServiceImpl<QuestionBankMapper, QuestionBank>
        implements QuestionBankService {

    @Resource
    private AppUserService appUserService;

    @Override
    public void validQuestionBank(QuestionBank questionBank, boolean add) {
        if (questionBank == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (add) {
            ThrowUtils.throwIf(StringUtils.isBlank(questionBank.getTitle()), ErrorCode.PARAMS_ERROR);
        }
    }

    @Override
    public QuestionBankVO getQuestionBankVO(QuestionBank questionBank) {
        if (questionBank == null) {
            return null;
        }
        Map<Long, AppUser> userMap = new HashMap<>();
        Long userId = questionBank.getUser_id();
        if (userId != null && userId > 0) {
            AppUser appUser = appUserService.getById(userId);
            if (appUser != null) {
                userMap.put(userId, appUser);
            }
        }
        return toQuestionBankVO(questionBank, userMap);
    }

    @Override
    public Page<QuestionBankVO> getQuestionBankVOPage(Page<QuestionBank> questionBankPage) {
        List<QuestionBank> questionBankList = questionBankPage.getRecords();
        Page<QuestionBankVO> voPage = new Page<>(questionBankPage.getCurrent(), questionBankPage.getSize(),
                questionBankPage.getTotal());
        if (CollUtil.isEmpty(questionBankList)) {
            return voPage;
        }
        Set<Long> userIdSet = questionBankList.stream()
                .map(QuestionBank::getUser_id)
                .filter(userId -> userId != null && userId > 0)
                .collect(Collectors.toSet());
        Map<Long, AppUser> userMap = new HashMap<>();
        if (CollUtil.isNotEmpty(userIdSet)) {
            appUserService.listByIds(userIdSet).forEach(appUser -> userMap.put(appUser.getId(), appUser));
        }
        List<QuestionBankVO> voList = questionBankList.stream()
                .map(questionBank -> toQuestionBankVO(questionBank, userMap))
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    private QuestionBankVO toQuestionBankVO(QuestionBank questionBank, Map<Long, AppUser> userMap) {
        QuestionBankVO questionBankVO = QuestionBankVO.objToVo(questionBank);
        Long userId = questionBank.getUser_id();
        if (userId != null && userMap.containsKey(userId)) {
            questionBankVO.setUser(AppUserVO.objToVo(userMap.get(userId)));
        }
        return questionBankVO;
    }

    @Override
    public QueryWrapper<QuestionBank> getQueryWrapper(QuestionBankQueryRequest questionBankQueryRequest) {
        QueryWrapper<QuestionBank> queryWrapper = new QueryWrapper<>();
        if (questionBankQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = questionBankQueryRequest.getSearchText();
        String sortField = questionBankQueryRequest.getSortField();
        String sortOrder = questionBankQueryRequest.getSortOrder();
        Long id = questionBankQueryRequest.getId();
        Long notId = questionBankQueryRequest.getNotId();
        String title = questionBankQueryRequest.getTitle();
        String description = questionBankQueryRequest.getDescription();
        Long userId = questionBankQueryRequest.getUserId();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("description", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.ne(notId != null, "id", notId);
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(userId != null, "user_id", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), CommonConstant.SORT_ORDER_ASC.equals(sortOrder),
                sortField);
        return queryWrapper;
    }
}
