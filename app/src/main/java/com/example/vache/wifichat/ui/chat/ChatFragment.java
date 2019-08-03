package com.example.vache.wifichat.ui.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.vache.wifichat.R;
import com.example.vache.wifichat.WifiDirectBR;
import com.example.vache.wifichat.ui.model.Chat;
import com.example.vache.wifichat.ui.model.Message;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Objects;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFragment extends Fragment implements ChatContract.View {


    private ChatContract.Presenter presenter;
    private MessagesAdapter adapter;
    private ImageButton sendButton;
    private ConstraintLayout sendMsgLayout;
    private EditText sendText;
//    private RecyclerView recyclerView;
    Boolean isEditMode = null;
    private boolean backPressed = false;

    public ChatFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                if (isEditMode != null && isEditMode) {
                    new AlertDialog.Builder(getContext())
                            .setTitle("Warning")
                            .setMessage("Are you sure you want to disconnect?")

                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    backPressed = true;
//                                        closeChat();
                                    presenter.disconnect();
                                }
                            })

                            .setNegativeButton(android.R.string.cancel, null)
                            .show();

                } else {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
                    int n = R.id.action_secondFragment_to_firstFragment;
                    navController.navigate(n, new Bundle());
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
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
        Boolean isServer = null;
        Boolean groupedFormed = null;
        InetAddress goa = null;
        String p2pDeviceName = null;
        if (args != null) {
            if (args.containsKey("chat")) {
                chat = (Chat) args.getSerializable("chat");
            }
            if (args.containsKey("isEditMode")) {
                isEditMode = args.getBoolean("isEditMode");
            }
            if (args.containsKey("isServer")) {
                isServer = args.getBoolean("isServer");
            }
            if (args.containsKey("goa")) {
                goa = (InetAddress) args.getSerializable("goa");
            }
            if (args.containsKey("p2pDeviceName")) {
                p2pDeviceName = args.getString("p2pDeviceName");
            }
            if (args.containsKey("groupedFormed")) {
                groupedFormed = args.getBoolean("groupedFormed");
            }
        }

        if (isEditMode) {
            ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle(p2pDeviceName);
        } else {
            ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle(chat.getUser().getName());
        }

        //todo nodo es back
//        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setDisplayShowHomeEnabled(true);


        presenter = new ChatPresenter(this, chat, p2pDeviceName, isServer, groupedFormed, goa, isEditMode);
        adapter = new MessagesAdapter();
        RecyclerView recyclerView = getView().findViewById(R.id.recycler_messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        presenter.start();

        sendButton = view.findViewById(R.id.send_message_butt);
        sendText = view.findViewById(R.id.send_message_input);

        sendMsgLayout = view.findViewById(R.id.send_message_layout);

        if (isEditMode) {
            sendMsgLayout.setVisibility(View.VISIBLE);
            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String msg = sendText.getText().toString();
                    if (!msg.isEmpty()) {
                        presenter.sendMsg(msg, true);
                        sendText.setText("");
                    }
                }
            });
        }
    }

    @Override
    public void showData(Chat chat) {
        adapter.setData(chat.getMessages() == null ? new ArrayList<Message>() : chat
                .getMessages());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void addMsg(Message message) {
        adapter.addMessage(message);
        RecyclerView rv = getView().findViewById(R.id.recycler_messages);
        rv.smoothScrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void disconnect() {
        super.onDestroy();
    }

    @Override
    public Context getActivityView() {
        return getActivity();
    }

    @Override
    public void closeChat() {
        NavHostFragment.findNavController(ChatFragment.this).navigateUp();
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

    @Override
    public void onDestroy() {
//        if (!backPressed)
//            presenter.disconnect();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.registerBR();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unregisterBR();
    }

}
