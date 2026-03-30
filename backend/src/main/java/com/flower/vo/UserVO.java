package com.flower.vo;

import lombok.Data;
import java.util.Date;

@Data
public class UserVO {
    private Integer id;
    private String username;
    private String email;
    private String phone;
    private String nickname;
    private String avatar;
    private String role;
    private Boolean isMember;
    private Date memberExpireTime;
    private Date createTime;
    private String status;
}
