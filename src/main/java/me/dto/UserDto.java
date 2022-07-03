package me.dto;

import lombok.Data;
import me.vo.ResponseOrder;

import java.util.Date;
import java.util.List;

@Data
public class UserDto {

    private String email;
    private String pwd;
    private String name;
    private String userId;
    private Date createAt;
    private String encryptedPwd;
    private List<ResponseOrder> orders;

}
