package com.example.vache.wifichat.data.helper;


import com.example.vache.wifichat.data.entities.UserEntity;
import com.example.vache.wifichat.ui.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserHelper {


    public static List<User> fromEntities(List<UserEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<User> res = new ArrayList<>();
        for (UserEntity entity : entities) {
            res.add(fromEntity(entity));
        }
        return res;
    }

    public static User fromEntity(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        User res = new User();
        res.setId(entity.getId());
        res.setName(entity.getName());
        return res;
    }

    public static List<UserEntity> toEntities(List<User> users) {
        if (users == null) {
            return null;
        }
        List<UserEntity> res = new ArrayList<>();
        for (User user : users) {
            res.add(toEntity(user));
        }
        return res;
    }

    public static UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        UserEntity res = new UserEntity();
        res.setId(user.getId());
        res.setName(user.getName());
        return res;
    }
}
