package com.jacobzp.tijinzhou.model.dto.appuser;

import java.io.Serializable;
import lombok.Data;

/**
 * 用户编辑请求
 *
 * @author <a href="https://github.com/jacob-z-p">jacobzp</a>
 */
@Data
public class AppUserEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    private static final long serialVersionUID = 1L;
}
