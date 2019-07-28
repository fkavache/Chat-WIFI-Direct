package com.example.vache.wifichat.data.helper;


import com.example.vache.wifichat.data.entities.MessageEntity;
import com.example.vache.wifichat.ui.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageHelper {

    public static List<Message> fromEntities(List<MessageEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<Message> res = new ArrayList<>();
        for (MessageEntity entity : entities) {
            res.add(fromEntity(entity));
        }
        return res;
    }

    public static Message fromEntity(MessageEntity entity) {
        if (entity == null) {
            return null;
        }
        Message res = new Message();
        res.setId(entity.getId());
        res.setUserId(entity.getUserId());
        res.setMessage(entity.getMessage());
        res.setDate(DateConverter.toDate(entity.getDate()));
        res.setChatId(entity.getChatId());
        return res;
    }

    public static List<MessageEntity> toEntities(List<Message> messages) {
        if (messages == null) {
            return null;
        }
        List<MessageEntity> res = new ArrayList<>();
        for (Message message : messages) {
            res.add(toEntity(message));
        }
        return res;
    }

    public static MessageEntity toEntity(Message message) {
        if (message == null) {
            return null;
        }
        MessageEntity res = new MessageEntity();
        res.setId(message.getId());
        res.setUserId(message.getUserId());
        res.setMessage(message.getMessage());
        res.setDate(DateConverter.fromDate(message.getDate()));
        res.setChatId(message.getChatId());
        return res;
    }
}
