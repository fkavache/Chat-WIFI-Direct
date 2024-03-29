package com.example.vache.wifichat.ui.chatList;


import com.example.vache.wifichat.ui.model.Chat;

import java.util.List;

public class ChatListContract {
    public interface View {

        void showData(List<Chat> chats);

        void startChat(Chat chat);

        void deleteChat(Chat chat);

        void deleteAll();
    }

    public interface Presenter {

        void start(String name);

        void onClickChat(Chat chat);

        void deleteChat(Chat chat);

        void deleteAll();
    }
}
