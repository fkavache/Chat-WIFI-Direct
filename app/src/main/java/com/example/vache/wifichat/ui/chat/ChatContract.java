package com.example.vache.wifichat.ui.chat;


import com.example.vache.wifichat.ui.model.Chat;
import com.example.vache.wifichat.ui.model.Message;

public class ChatContract {

    public interface View {
        void showData(Chat chat);

        void addMsg(Message message);

        void disconnect();
    }

    public interface Presenter {
        void start();

        void sendMsg(String msg, boolean mine);

        void disconnect();
    }
}
