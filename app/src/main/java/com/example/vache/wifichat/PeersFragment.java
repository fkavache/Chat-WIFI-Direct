package com.example.vache.wifichat;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.WIFI_P2P_SERVICE;

public class PeersFragment extends Fragment implements MainContract._View {

    public  TextView status;
    private Button discover;
    private Button send;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private List<WifiP2pDevice> peers;
    private MainPresenter presenter;
    private PeersAdapter adapter;
    private RecyclerView peersV;
    private ProgressBar progressBar;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private SendReceive sendReceive;
    private ServerClass serverClass;
    private ClientClass clientClass;

    int x = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.peers_fragment, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init(view);

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverPeers();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MySendReceive mySendReceive = new MySendReceive("HA EXLA" + x);
                x++ ;
                mySendReceive.start();
            }
        });
    }

    private void discoverPeers() {
        adapter.setData(new ArrayList<WifiP2pDevice>());
        progressBar.setVisibility(View.VISIBLE);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                status.setText("Discovery Started");
            }

            @Override
            public void onFailure(int i) {
                status.setText("Failed to Start Discovery");
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void init(View view) {
        status = view.findViewById(R.id.chat_status);
        mManager = (WifiP2pManager) Objects.requireNonNull(getActivity()).getSystemService(WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(getActivity(), getActivity().getMainLooper(), null);
        peers = new ArrayList<>();
        peersV = view.findViewById(R.id.peersv);
        progressBar = view.findViewById(R.id.progressBar);
        discover = view.findViewById(R.id.discover);
        send = view.findViewById(R.id.button);

        progressBar.setVisibility(View.VISIBLE);

        presenter = new MainPresenter(this);
        adapter = new PeersAdapter(presenter);

        peersV.setAdapter(adapter);
        peersV.setLayoutManager(new LinearLayoutManager(getActivity()));

        presenter = new MainPresenter(this);

        mIntentFilter = new IntentFilter();

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        discoverPeers();
    }

    @Override
    public void onClickView(final WifiP2pDevice p2pdevice, WifiP2pConfig config) {

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getActivity().getApplicationContext(), "Connected to " + p2pdevice.deviceName
                        , Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int i) {
                Toast.makeText(getActivity().getApplicationContext(), "Failed to connect to" + p2pdevice.deviceName
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void peersListenerHandler(List<WifiP2pDevice> peersList) {
        this.peers = peersList;
        if (peers.isEmpty()) {
            status.setText("No Device Found");
            adapter.setData(new ArrayList<WifiP2pDevice>());
        } else {
//            status.setText("");
            adapter.setData(peers);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void connectionInfoListenerHandler(WifiP2pInfo wifiP2pInfo, InetAddress goa) {
        Log.d("SSSSSSSSSSSSSSSSSSSs", "conInfo");
        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            Log.d("LLLLLLLLLLLLLLLLLLLLLL", "SERVERRRRRR");

            serverClass = new ServerClass();
            serverClass.start();

        } else if (wifiP2pInfo.groupFormed) {
//            status.setText("Client");
            Log.d("LLLLLLLLLLLLLLLLLLLLL", "CLIENTTTTTT");
            clientClass = new ClientClass(goa);
            clientClass.start();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new WifiDirectBR(mManager, mChannel, presenter);
        getActivity().registerReceiver(mReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
    }

    public class ServerClass extends Thread {
        Socket socket;
        ServerSocket serverSocket;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(8888);
                socket = serverSocket.accept();
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                socket.connect(new InetSocketAddress(hostAddr, 8888), 500);
                sendReceive = new SendReceive(socket);
                sendReceive.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    byte[] readBuff = (byte[]) message.obj;
                    String msg = new String(readBuff, 0, message.arg1);
                    status.setText(msg);
                    break;
            }
            return true;
        }
    });
}
