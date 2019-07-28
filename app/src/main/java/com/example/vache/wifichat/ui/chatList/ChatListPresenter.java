package com.example.vache.wifichat.ui.chatList;

import com.example.vache.wifichat.data.Database;
import com.example.vache.wifichat.data.helper.ChatHelper;
import com.example.vache.wifichat.ui.model.Chat;

import java.util.List;

public class ChatListPresenter implements ChatListContract.Presenter {


    private ChatListContract.View view;

    public ChatListPresenter(ChatListContract.View view) {
        this.view = view;
    }

    @Override
    public void start(String name) {

        List<Chat> chats = Database.getInstance().dataDao().getChatList();
        view.showData(chats);
    }

    @Override
    public void onClickChat(Chat chat) {
        Chat fullChat = ChatHelper.fromEntity(Database.getInstance().dataDao().getChatWithValues(chat.getId()));
        view.startChat(fullChat);
    }
}
