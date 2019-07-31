package com.example.vache.wifichat.ui.chat;


import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.telephony.AccessNetworkConstants;
import android.util.Log;

import com.example.vache.wifichat.Utils;
import com.example.vache.wifichat.data.Database;
import com.example.vache.wifichat.ui.model.Chat;
import com.example.vache.wifichat.ui.model.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatPresenter implements ChatContract.Presenter {
    private ChatContract.View view;
    private Chat chat;
    private String p2pDeviceName;
    private SendReceive sendReceive;
    private ServerClass serverClass;
    private ClientClass clientClass;
    private Boolean isServer;
    private Boolean groupFormed;
    private InetAddress goa;
    private Boolean isEditMode;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    public ChatPresenter(ChatContract.View view, Chat chat, String p2pDeviceName, Boolean isServer, Boolean groupFormed, InetAddress goa, Boolean isEditMode) {
        this.view = view;
        this.chat = chat;
        this.p2pDeviceName = p2pDeviceName;
        this.isServer = isServer;
        this.groupFormed = groupFormed;
        this.goa = goa;
        this.isEditMode = isEditMode;
        mManager = Utils.getInstance().getManager();
        mChannel = Utils.getInstance().getChannel();
    }

    @Override
    public void start() {
        if (chat == null) {
            chat = Database.getInstance().dataDao().getChatWithName(p2pDeviceName);
            if (chat == null) {
                chat = Database.getInstance().dataDao().createNewChat(p2pDeviceName);
            }
        }
        if (isEditMode) {
            if (isServer) {
                Log.d("KKKKKKKKKKKKKKKKKKKKKKK", "SERVERRRRRR");

                serverClass = new ServerClass();
                serverClass.start();

                isServer = true;

            } else if (groupFormed) {
//            status.setText("Client");
                Log.d("LLLLLLLLLLLLLLLLLLLLL", "CLIENTTTTTT");
                clientClass = new ClientClass(goa);
                clientClass.start();
                isServer = false;
            }
        }
        view.showData(chat);
    }

    @Override
    public void sendMsg(String msg, boolean mine) {
        if (mine) {
            MySendReceive mySendReceive = new MySendReceive(msg);
            mySendReceive.start();
        }
        Message message = Database.getInstance().dataDao().createNewMessage(msg, chat.getId(), mine ? null : chat.getUser().getId());
        view.addMsg(message);
    }

    @Override
    public void disconnect() {
        Disconnect disconnect = new Disconnect();
        disconnect.start();
    }

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(9999);
                serverSocket.setReuseAddress(true);
                socket = serverSocket.accept();
                socket.setReuseAddress(true);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void end_() {
            try {
                Log.d("asas", ":asasaSAASFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFSSA");
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.interrupt();
        }
    }

    public class MySendReceive extends Thread {
        private String msg;

        public MySendReceive(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            try {
                sendReceive.write(msg.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public class ClientClass extends Thread {
        Socket socket;
        String hostAddr;

        public ClientClass(InetAddress hostAddress) {
            hostAddr = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAddr, 9999), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void end_() {
            try {
                Log.d("asas", ":asasaSAASFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFSSA");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            super.interrupt();
        }
    }

    public class SendReceive extends Thread {
        private Socket socket;
        private InputStream inputStream;
        private OutputStream outputStream;

        public SendReceive(Socket sct) {
            socket = sct;
            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buf = new byte[1024];
            int bytes;
            while (socket != null) {
                try {
                    bytes = inputStream.read(buf);
                    if (bytes > 0) {
                        handler.obtainMessage(1, bytes, -1, buf).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message message) {
            switch (message.what) {
                case 1:
                    byte[] readBuff = (byte[]) message.obj;
                    String msg = new String(readBuff, 0, message.arg1);
                    if (!msg.isEmpty()) {
                        sendMsg(msg, false);
                    }
                    break;
            }
            return true;
        }
    });


    private class Disconnect extends Thread {
        @Override
        public void run() {
            if (mManager != null && mChannel != null) {
                try {
                    if (serverClass != null) {
                        serverClass.end_();
                        serverClass.join();
                    }
                    if (clientClass != null) {
                        clientClass.end_();
                        clientClass.join();
                    }
                    sendReceive = null;
                    serverClass = null;
                    clientClass = null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        if (group != null && mManager != null && mChannel != null
                                && group.isGroupOwner()) {
                            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

                                @Override
                                public void onSuccess() {
                                    view.disconnect();
                                    Log.d("kai", "removeGroup onSuccess -");
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Log.d("kai", "removeGroup onFailure -" + reason);
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}
