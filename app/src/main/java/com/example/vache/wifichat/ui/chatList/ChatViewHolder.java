package com.example.vache.wifichat.ui.chatList;

import android.view.View;
import android.widget.TextView;


import com.example.vache.wifichat.R;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ChatViewHolder extends RecyclerView.ViewHolder {

    private TextView nameTextView;
    private TextView datesTextView;
    private TextView messagesCountTextView;
    private ConstraintLayout back;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.view_text_name);
        back = itemView.findViewById(R.id.back);
        datesTextView = itemView.findViewById(R.id.start_end_date);
        messagesCountTextView = itemView.findViewById(R.id.count_messages);
    }

    public TextView getNameTextView() {
        return nameTextView;
    }

    public ConstraintLayout getBack() {
        return back;
    }

    public TextView getDatesTextView() {
        return datesTextView;
    }

    public TextView getMessagesCountTextView() {
        return messagesCountTextView;
    }
}

