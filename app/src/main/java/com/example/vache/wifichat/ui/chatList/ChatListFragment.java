package com.example.vache.wifichat.ui.chatList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vache.wifichat.R;
import com.example.vache.wifichat.ui.model.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListFragment extends Fragment implements ChatListContract.View {

    private ChatListAdapter adapter;

    private ChatListContract.Presenter presenter;

    public ChatListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new ChatListPresenter(this);
        adapter = new ChatListAdapter(new ArrayList<Chat>(), presenter);

        RecyclerView recyclerView = getView().findViewById(R.id.recyvler_view_list_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        presenter.start(null);
    }


    @Override
    public void showData(List<Chat> chats) {
        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("History(" + chats.size() + ")");
        adapter.setData(chats);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void startChat(Chat chat) {
        NavController navController = Navigation.findNavController(getActivity(), R.id.recyvler_view_list_chat);
        Bundle args = new Bundle();
        args.putSerializable("chat", chat);
        navController.navigate(R.id.action_firstFragment_to_secondFragment, args);
    }
}
