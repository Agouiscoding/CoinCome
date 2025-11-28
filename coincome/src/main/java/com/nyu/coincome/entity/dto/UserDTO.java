package com.nyu.coincome.entity.dto;
import lombok.Data;
import java.util.Date;

@Data
public class UserDTO {
    private Integer userId;
    private String username;
    private String email;
    private Date createdAt;
    private Integer isAdmin;
}
