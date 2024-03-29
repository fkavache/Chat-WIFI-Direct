package com.example.vache.wifichat.ui.chat;


import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
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
    private static final String SPECIAL_MSG = "TAVZARIKO777";
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
    private IntentFilter mIntentFilter;
    private ChatBR mReceiver;
    private boolean needGoHome;

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
        mIntentFilter = new IntentFilter();

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
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

                serverClass = new ServerClass();
                serverClass.start();

                isServer = true;

            } else if (groupFormed) {
//            status.setText("Client");
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

    @Override
    public void registerBR() {
        mReceiver = new ChatBR(mManager, mChannel, this);
        view.getActivityView().registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void unregisterBR() {
        view.getActivityView().unregisterReceiver(mReceiver);
    }

    @Override
    public void closeChat() {
        view.closeChat();
    }

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(9999);
                serverSocket.setReuseAddress(true);
                serverSocket.setSoTimeout(60000);
                socket = serverSocket.accept();
                socket.setReuseAddress(true);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
                view.removeProgress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void end_() {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
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
            int a = 10;
            while (true) {
                try {
                    socket.connect(new InetSocketAddress(hostAddr, 9999), 2000);
                    if (socket.isConnected()) {
                        sendReceive = new SendReceive(socket);
                        sendReceive.start();
                        view.removeProgress();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                --a;
                if (a == 0) {
                    Disconnect disconnect = new Disconnect();
                    disconnect.start();
                }
            }

        }

        public void end_() {
            try {
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
        private boolean end = false;

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
            while (socket != null && !getEnd()) {
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

        synchronized void setEnd() {
            end = true;
        }

        synchronized boolean getEnd() {
            return end;
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
                        if (msg.equals(ChatPresenter.SPECIAL_MSG)) {
                            ChatPresenter.this.disconnect();
                        } else {
                            sendMsg(msg, false);
                        }
                    }
                    break;
            }
            return true;
        }
    });


    private class Disconnect extends Thread {
        @Override
        public void run() {


            if (isServer) {

                if (mManager != null && mChannel != null) {
                    if (sendReceive != null) {
                        sendReceive.setEnd();
                    }
                    if (serverClass != null) {
                        serverClass.end_();
                    }
                    if (clientClass != null) {
                        clientClass.end_();
                    }
                    sendReceive = null;
                    serverClass = null;
                    clientClass = null;

                    mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                        @Override
                        public void onGroupInfoAvailable(WifiP2pGroup group) {
                            if (group != null && mManager != null && mChannel != null
                                    && group.isGroupOwner()) {

                                mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {

                                    @Override
                                    public void onSuccess() {

                                        needGoHome = true;
                                    }

                                    @Override
                                    public void onFailure(int reason) {
                                    }
                                });
                            }
                        }
                    });
                }
            } else {
                MySendReceive mySendReceive = new MySendReceive(SPECIAL_MSG);
                mySendReceive.start();
                if (sendReceive != null) {
                    sendReceive.setEnd();
                }
            }
        }
    }


    @Override
    public boolean isNeedGoHome() {
        return needGoHome;
    }

    @Override
    public void deleteChat() {
        view.deleteChat(chat);
    }
}
