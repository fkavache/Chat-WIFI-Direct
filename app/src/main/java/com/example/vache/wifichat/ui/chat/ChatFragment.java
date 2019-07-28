package com.example.vache.wifichat.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import com.example.vache.wifichat.R;
import com.example.vache.wifichat.ui.model.Chat;
import com.example.vache.wifichat.ui.model.Message;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFragment extends Fragment implements ChatContract.View {


    private ChatContract.Presenter presenter;
    private MessagesAdapter adapter;

    public ChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO nodo maybe for chat sending
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View res = inflater.inflate(R.layout.fragment_chat, container, false);
        return res;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        Chat chat = null;
        if (args != null && args.containsKey("chat")) {
            chat = (Chat) args.getSerializable("chat");
        }
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle(chat.getUser().getName());
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setDisplayShowHomeEnabled(true);


        presenter = new ChatPresenter(this, chat);
        adapter = new MessagesAdapter();
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        presenter.start();
    }

    @Override
    public void showData(Chat chat) {
        adapter.setData(chat.getMessages() == null ? new ArrayList<Message>() : chat
                .getMessages());
        adapter.notifyDataSetChanged();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            if (isFocusNameField) {
//                nameField.clearFocus();
//            }
            NavHostFragment.findNavController(this).navigateUp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
