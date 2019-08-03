package com.example.vache.wifichat.ui.chatList;

import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vache.wifichat.R;
import com.example.vache.wifichat.ui.chat.MessageViewHolder;
import com.example.vache.wifichat.ui.model.Chat;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListAdapter extends RecyclerView.Adapter {

    private List<Chat> chats = new ArrayList<>();
    private ChatListContract.Presenter presenter;

    public ChatListAdapter(List<Chat> chats, ChatListContract.Presenter presenter) {
        this.chats = chats;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = viewType == 0 ?
                R.layout.item_list_chat : R.layout.boundary_chat;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return viewType == 0 ? new ChatViewHolder(view) : new ChatBoundaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (position % 2 == 0) {
            final Chat chat = chats.get(position / 2);
            ChatViewHolder chatViewHolder = (ChatViewHolder) holder;
            chatViewHolder.getNameTextView().setText(chat.getUser().getName());
            chatViewHolder.getDatesTextView().setText(chat.getLastMessageDate() == null ? "" : MessageViewHolder.SIMPLE_DATE_FORMAT.format(chat.getLastMessageDate()));
            chatViewHolder.getMessagesCountTextView().setText(chat.getCountMessages() + "");
            chatViewHolder.getBack().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onClickChat(chats.get(position / 2));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chats.size() == 0 ? 0 : chats.size() * 2 - 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    public void setData(List<Chat> chats) {
        this.chats = chats;
    }

    public List<Chat> getData() {
        return chats;
    }

    public void removeChat(Chat chat){
        chats.remove(chat);
        notifyDataSetChanged();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        private TextView nameTextView;
        private TextView datesTextView;
        private TextView messagesCountTextView;
        private ConstraintLayout back;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnLongClickListener(this);
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

        @Override
        public boolean onLongClick(View view) {
            presenter.deleteChat(chats.get(getAdapterPosition() / 2));
            return true;
        }
    }

}
