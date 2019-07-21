package com.example.vache.wifichat.data;


import com.example.vache.wifichat.data.entities.ChatEntity;
import com.example.vache.wifichat.data.entities.MessageEntity;
import com.example.vache.wifichat.data.entities.UserEntity;
import com.example.vache.wifichat.ui.App;

import androidx.room.Room;
import androidx.room.RoomDatabase;


@androidx.room.Database(entities = {UserEntity.class, ChatEntity.class, MessageEntity.class}, version = 1)
public abstract class Database extends RoomDatabase {

    private static final String DATABASE_NAME = "initial_final_app_database_test_sxva_vafshe";

    private static Database INSTANCE;

    private static final Object lock = new Object();

    public abstract DataDao dataDao();

    public static Database getInstance() {
        synchronized (lock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                        App.getContext(),
                        Database.class,
                        DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
//            insertTestData();
        }
        return INSTANCE;
    }

    public static void insertTestData() {
        UserEntity u1 = new UserEntity();
        u1.setName("chami_dzma_1");
        UserEntity u2 = new UserEntity();
        u2.setName("chami_dzma_2");

        u1.setId(getInstance().dataDao().insertUser(u1));
        u2.setId(getInstance().dataDao().insertUser(u2));

        ChatEntity chat1 = new ChatEntity();
        chat1.setUserId(u1.getId());
        ChatEntity chat2 = new ChatEntity();
        chat2.setUserId(u2.getId());

        chat1.setId(getInstance().dataDao().insertChat(chat1));
        chat2.setId(getInstance().dataDao().insertChat(chat2));

        for (int i = 0; i < 10; i++) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessage("kai dzmao ra..111");
            messageEntity.setChatId(chat1.getId());
            if (i % 2 == 0) {
                messageEntity.setUserId(u1.getId());
            }
            getInstance().dataDao().insertMessage(messageEntity);
        }
        for (int i = 0; i < 10; i++) {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessage("kai dzmao ra..222");
            messageEntity.setChatId(chat2.getId());
            if (i < 5) {
                messageEntity.setUserId(u2.getId());
            }
            getInstance().dataDao().insertMessage(messageEntity);
        }
    }
}
