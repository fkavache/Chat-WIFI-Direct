package com.example.vache.wifichat.ui.chat;


import com.example.vache.wifichat.ui.model.Chat;

public class ChatPresenter implements ChatContract.Presenter {
    private ChatContract.View view;
    private Chat chat;

    public ChatPresenter(ChatContract.View view, Chat chat) {
        this.view = view;
        this.chat = chat;
    }

    @Override
    public void start() {
        view.showData(chat);
    }
}
