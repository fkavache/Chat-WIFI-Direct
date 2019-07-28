package com.example.vache.wifichat.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.vache.wifichat.R;
import com.example.vache.wifichat.ui.model.Message;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessageViewHolder> {


    private List<Message> messages = new ArrayList<>();

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = viewType == 0 ?
                R.layout.message_mine : R.layout.message_others;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.getMessageTextView().setText(message.getMessage());
        holder.getDateTextView().setText(MessageViewHolder.SIMPLE_DATE_FORMAT.format(message.getDate()));
        holder.getMessageTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.setShowDate(!holder.isShowDate());
                holder.getDateTextView().setVisibility(holder.isShowDate() ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.getUserId() == null ? 0 : 1;
    }

    public void setData(List<Message> messages) {
        this.messages = messages;
    }
}
