package com.example.vache.wifichat.ui.chat;

import android.view.View;
import android.widget.TextView;


import com.example.vache.wifichat.R;

import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    private TextView messageTextView;
    private TextView DateTextView;
    private boolean showDate;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        messageTextView = itemView.findViewById(R.id.view_text_message);
        DateTextView = itemView.findViewById(R.id.date);
        showDate = false;
    }

    public TextView getMessageTextView() {
        return messageTextView;
    }

    public TextView getDateTextView() {
        return DateTextView;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }
}
