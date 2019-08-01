package com.example.vache.wifichat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.WIFI_P2P_SERVICE;

public class PeersFragment extends Fragment implements MainContract._View {

    public TextView status;
    private Button discover;
    private Button sendButton;
    private Button disconnectButton;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private List<WifiP2pDevice> peers;
    private MainPresenter presenter;
    private PeersAdapter adapter;
    private RecyclerView peersV;
    private ProgressBar progressBar;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;

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
//        sendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
// todo nodo
//        disconnectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Disconnect disconnect = new Disconnect();
//                disconnect.run();
//            }
//        });
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
        mManager = Utils.getInstance().getManager();
        mChannel = Utils.getInstance().getChannel();
        peers = new ArrayList<>();
        peersV = view.findViewById(R.id.peersv);
        progressBar = view.findViewById(R.id.progressBar);
        discover = view.findViewById(R.id.discover);
        sendButton = view.findViewById(R.id.button);
        disconnectButton = view.findViewById(R.id.disconnect_butt);

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

//        discoverPeers();
    }

    @Override
    public void onClickView(final WifiP2pDevice p2pdevice, WifiP2pConfig config) {

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                presenter.setCurrentPeerName(p2pdevice.deviceName);
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
        Toast.makeText(getActivity().getApplicationContext(), "Connected !!!!!!!!!!!!!!"
                , Toast.LENGTH_SHORT).show();
        NavController navController = Navigation.findNavController(getActivity(), R.id.fragment);
        Bundle args = new Bundle();
        args.putBoolean("isEditMode", true);
        args.putBoolean("isServer", wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner);
        args.putSerializable("goa", goa);
        args.putString("p2pDeviceName", presenter.getCurrentPeerName());
        args.putBoolean("groupedFormed", wifiP2pInfo.groupFormed);
        navController.navigate(R.id.action_thirdFragment_to_secondFragment, args);
    }

    @Override
    public Context getViewContext() {
        return getActivity();
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


}
