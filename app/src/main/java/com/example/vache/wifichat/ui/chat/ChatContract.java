package com.example.vache.wifichat.ui.chat;


import android.content.Context;

import com.example.vache.wifichat.ui.model.Chat;
import com.example.vache.wifichat.ui.model.Message;

public class ChatContract {

    public interface View {
        void showData(Chat chat);

        void addMsg(Message message);

        void disconnect();

        Context getActivityView();

        void closeChat();

        void deleteChat(Chat chat);
    }

    public interface Presenter {
        void start();

        void sendMsg(String msg, boolean mine);

        void disconnect();

        void registerBR();

        void unregisterBR();

        void closeChat();

        boolean isNeedGoHome();

        void deleteChat();
    }
}
