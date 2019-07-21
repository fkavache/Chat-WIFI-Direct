package com.example.vache.wifichat.ui.chat;


import com.example.vache.wifichat.ui.model.Chat;

public class ChatContract {

    public interface View {
        void showData(Chat chat);
    }

    public interface Presenter {
        void start();

    }
}
