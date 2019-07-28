package com.example.vache.wifichat.data;


import com.example.vache.wifichat.data.entities.ChatEntity;
import com.example.vache.wifichat.data.entities.ChatFullEntity;
import com.example.vache.wifichat.data.entities.ChatHalfEntity;
import com.example.vache.wifichat.data.entities.MessageEntity;
import com.example.vache.wifichat.data.entities.UserEntity;
import com.example.vache.wifichat.data.helper.ChatHelper;
import com.example.vache.wifichat.data.helper.DateConverter;
import com.example.vache.wifichat.ui.model.Chat;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public abstract class DataDao {

    public List<Chat> getChatList() {
        List<Chat> res = ChatHelper.fromHalfEntities(getChats());
        for (Chat chat : res) {
            chat.setFirstMessageDate(DateConverter.toDate(getFirstMessageDate(chat.getId())));
            chat.setLastMessageDate(DateConverter.toDate(getLastMessageDate(chat.getId())));
            chat.setCountMessages(getMessagesCount(chat.getId()));
        }
        return res;
    }

    @Query("SELECT * FROM chat")
    public abstract List<ChatHalfEntity> getChats();

    @Query("SELECT * FROM chat WHERE id like :id")
    public abstract ChatFullEntity getChatWithValues(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertUser(UserEntity user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertChat(ChatEntity chat);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long insertMessage(MessageEntity message);

    @Delete
    public abstract int deleteChat(ChatEntity chat);

    @Query("SELECT MIN(date) FROM message WHERE chatId = :chatId ")
    public abstract Long getFirstMessageDate(long chatId);

    @Query("SELECT MAX(id) FROM message WHERE chatId = :chatId ")
    public abstract Long getLastMessageDate(long chatId);

    @Query("SELECT COUNT(id) FROM message WHERE chatId = :chatId ")
    public abstract int getMessagesCount(long chatId);
}
