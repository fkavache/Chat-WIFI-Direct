package com.example.vache.wifichat.ui.chatList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vache.wifichat.R;
import com.example.vache.wifichat.data.Database;
import com.example.vache.wifichat.data.entities.ChatEntity;
import com.example.vache.wifichat.ui.model.Chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatListFragment extends Fragment implements ChatListContract.View {

    private ChatListAdapter adapter;

    private ChatListContract.Presenter presenter;

    private Button clearHistory;

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
        clearHistory = getView().findViewById(R.id.clear_history);

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.deleteAll();
            }
        });

//        Database.getInstance().insertTestData();

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
        args.putBoolean("isEditMode", false);
        args.putSerializable("chat", chat);
        navController.navigate(R.id.action_firstFragment_to_secondFragment, args);
    }

    @Override
    public void deleteChat(final Chat chat) {
        new AlertDialog.Builder(getContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete?")

            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Database database = Database.getInstance();
                    database.dataDao().deleteChatU(chat.getUser().getId());
                    adapter.removeChat(chat);
                    ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("History(" + adapter.getData().size() + ")");
                }
            })

            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }

    @Override
    public void deleteAll() {
        new AlertDialog.Builder(getContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to clear history?")

            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Database database = Database.getInstance();
                    database.dataDao().deleteAll();
                    adapter.setData(new ArrayList<Chat>());
                    adapter.notifyDataSetChanged();
                    ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("History(0)");
                }
            })

            .setNegativeButton(android.R.string.cancel, null)
            .show();
    }
}
