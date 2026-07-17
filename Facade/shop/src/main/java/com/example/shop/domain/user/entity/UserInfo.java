package com.example.shop.domain.user.entity;

import com.example.shop.domain.user.dto.response.Creator;
import com.example.shop.domain.user.dto.response.UserSummary;
import com.example.shop.domain.user.dto.response.UserResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserInfo {

    private Long id;
    private String email;
    private String name;
    private int point;

    public Long getId() { return id; }

    public static UserInfo from (User entity) {
        UserInfo info = new UserInfo();
        info.id = entity.getId();
        info.email = entity.getEmail();
        info.point = entity.getPoint();
        info.name = entity.getName();
        return info;
    }

    public UserSummary toUserSummary(){
        return new UserSummary(id, email, name);
    }

    public UserResponse toUserResponse() {
        return new UserResponse(name, point);
    }

    public Creator toCreator() {
        return new Creator(name);
    }

}
