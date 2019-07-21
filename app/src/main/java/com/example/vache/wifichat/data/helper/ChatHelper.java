package com.example.vache.wifichat.data.helper;

import com.example.vache.wifichat.data.entities.ChatEntity;
import com.example.vache.wifichat.data.entities.ChatFullEntity;
import com.example.vache.wifichat.data.entities.ChatHalfEntity;
import com.example.vache.wifichat.ui.model.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatHelper {

    public static List<Chat> fromHalfEntities(List<ChatHalfEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<Chat> res = new ArrayList<>();
        for (ChatHalfEntity entity : entities) {
            res.add(fromEntity(entity));
        }
        return res;
    }

    public static Chat fromEntity(ChatHalfEntity entity) {
        if (entity == null) {
            return null;
        }
        Chat res = new Chat();
        res.setId(entity.chat.getId());
        res.setUser(UserHelper.fromEntity(entity.user));
        return res;
    }

    public static List<Chat> fromFullEntities(List<ChatFullEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<Chat> res = new ArrayList<>();
        for (ChatFullEntity entity : entities) {
            res.add(fromEntity(entity));
        }
        return res;
    }

    public static Chat fromEntity(ChatFullEntity entity) {
        if (entity == null) {
            return null;
        }
        Chat res = new Chat();
        res.setId(entity.chat.getId());
        res.setUser(UserHelper.fromEntity(entity.user));
        res.setMessages(MessageHelper.fromEntities(entity.messages));
        return res;
    }

    public static List<ChatEntity> toEntities(List<Chat> chats) {
        if (chats == null) {
            return null;
        }
        List<ChatEntity> res = new ArrayList<>();
        for (Chat chat : chats) {
            res.add(toEntity(chat));
        }
        return res;
    }

    public static ChatEntity toEntity(Chat chat) {
        if (chat == null) {
            return null;
        }
        ChatEntity res = new ChatEntity();
        res.setId(chat.getId());
        if (chat.getUser() != null) {
            res.setUserId(chat.getUser().getId());
        }
        return res;
    }
}
