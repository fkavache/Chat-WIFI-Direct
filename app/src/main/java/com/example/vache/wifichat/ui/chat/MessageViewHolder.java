package com.example.vache.wifichat.ui.chat;

import android.view.View;
import android.widget.TextView;


import com.example.vache.wifichat.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private TextView messageTextView;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.view_text_message);
    }

    public TextView getMessageTextView() {
        return messageTextView;
    }
}
