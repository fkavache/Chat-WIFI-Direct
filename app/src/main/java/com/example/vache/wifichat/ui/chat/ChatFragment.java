package com.example.vache.wifichat.ui.chat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.example.vache.wifichat.R;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFragment extends Fragment implements ChatContract.View {


    private ChatContract.Presenter presenter;
    private MessagesAdapter adapter;
    private ImageButton sendButton;
    private EditText sendText;
    Boolean isEditMode = null;

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
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete this entry?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
                //todo nodo backing
                NavHostFragment.findNavController(ChatFragment.this).navigateUp();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        //TODO nodo maybe for chat sending
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View res = inflater.inflate(R.layout.fragment_chat, container, false);
        sendButton = res.findViewById(R.id.send_message_butt);
        sendText = res.findViewById(R.id.send_message_input);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "SEND"
                        , Toast.LENGTH_SHORT).show();
                String msg = sendText.getText().toString();
                if (!msg.isEmpty()) {
                    presenter.sendMsg(msg, true);
                    sendText.setText("");
                }
            }
        });

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
    }

    @Override
    public void disconnect() {

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
        super.onDestroy();
    }
}
