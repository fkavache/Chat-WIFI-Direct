package com.example.vache.wifichat;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class MainPresenter implements MainContract._Presenter {

    private MainContract._View view;
    private List<WifiP2pDevice> peers;

    public MainPresenter(MainContract._View view) {
        this.view = view;
    }

    @Override
    public void onClickPresenter(WifiP2pDevice p2pdevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = p2pdevice.deviceAddress;

        view.onClickView(p2pdevice, config);
    }

    @Override
    public WifiP2pManager.PeerListListener getPeersListener() {
        this.peers = new ArrayList<>();
        WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                if(!peerList.getDeviceList().equals(peers)){
                    peers.clear();
                    peers.addAll(peerList.getDeviceList());
                }

                view.peersListenerHandler(peers);
            }
        };
        return peerListListener;
    }

    @Override
    public WifiP2pManager.ConnectionInfoListener getConnectionInfoListener() {
        WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                final InetAddress groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
                view.connectionInfoListenerHandler(wifiP2pInfo, groupOwnerAddress);
            }
        };
        return connectionInfoListener;
    }
}
