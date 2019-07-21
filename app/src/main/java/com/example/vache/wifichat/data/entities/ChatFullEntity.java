package com.example.vache.wifichat.data.entities;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class ChatFullEntity {
    @Embedded
    public ChatEntity chat;

    @Relation(parentColumn = "userId", entityColumn = "id", entity = UserEntity.class)
    public UserEntity user;

    @Relation(parentColumn = "id", entityColumn = "chatId", entity = MessageEntity.class)
    public List<MessageEntity> messages;
}
