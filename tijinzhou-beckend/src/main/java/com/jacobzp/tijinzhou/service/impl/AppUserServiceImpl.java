package com.jacobzp.tijinzhou.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.jacobzp.tijinzhou.service.AppUserService;
import com.jacobzp.tijinzhou.mapper.AppUserMapper;
import com.jacobzp.tijinzhou.model.entity.AppUser;
import org.springframework.stereotype.Service;

/**
* @author 张鹏
* @description 针对表【app_user(用户)】的数据库操作Service实现
* @createDate 2026-10-09 20:03:46
*/
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser>
    implements AppUserService {

}




