package com.example.vache.wifichat;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import java.net.InetAddress;
import java.util.List;

public interface MainContract {

    interface _View {
        void onClickView(WifiP2pDevice p2pdevice, WifiP2pConfig config);

        void peersListenerHandler(List<WifiP2pDevice> peersList);

        void connectionInfoListenerHandler(WifiP2pInfo wifiP2pInfo, InetAddress goa);

        Context getViewContext();
    }

    interface _Presenter {
        void onClickPresenter(WifiP2pDevice p2pdevice);

        WifiP2pManager.PeerListListener getPeersListener();

        WifiP2pManager.ConnectionInfoListener getConnectionInfoListener();
    }

}