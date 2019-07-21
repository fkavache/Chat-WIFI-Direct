package com.example.vache.wifichat.ui.chatList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vache.wifichat.R;
import com.example.vache.wifichat.ui.model.Chat;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatViewHolder> {

    private List<Chat> chats = new ArrayList<>();
    private ChatListContract.Presenter presenter;

    public ChatListAdapter(List<Chat> chats, ChatListContract.Presenter presenter) {
        this.chats = chats;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, final int position) {
        final Chat chat = chats.get(position);
        holder.getNameTextView().setText(chat.getUser().getName());
        holder.getBack().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onClickChat(chats.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void setData(List<Chat> chats) {
        this.chats = chats;
    }
}
