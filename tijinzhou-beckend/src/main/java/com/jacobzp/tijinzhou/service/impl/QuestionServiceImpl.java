package com.jacobzp.tijinzhou.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jacobzp.tijinzhou.service.QuestionService;
import com.jacobzp.tijinzhou.mapper.QuestionMapper;
import com.jacobzp.tijinzhou.model.entity.Question;
import org.springframework.stereotype.Service;

/**
* @author 张鹏
* @description 针对表【question(题目)】的数据库操作Service实现
* @createDate 2026-10-09 20:03:47
*/
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
    implements QuestionService {

}




