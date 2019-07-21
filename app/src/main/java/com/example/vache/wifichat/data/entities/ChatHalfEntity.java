package com.example.vache.wifichat.data.entities;


import androidx.room.Embedded;
import androidx.room.Relation;

public class ChatHalfEntity {
    @Embedded
    public ChatEntity chat;

    @Relation(parentColumn = "userId", entityColumn = "id", entity = UserEntity.class)
    public UserEntity user;
}
