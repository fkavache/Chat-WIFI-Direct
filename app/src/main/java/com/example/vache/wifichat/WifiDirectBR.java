package com.example.vache.wifichat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class WifiDirectBR extends BroadcastReceiver {
    private WifiP2pManager         mManager;
    private WifiP2pManager.Channel mChannel;
    private MainPresenter          presenter;

    public WifiDirectBR(WifiP2pManager manager
            , WifiP2pManager.Channel channel
            , MainPresenter presenter) {
        super();

        this.mManager  = manager;
        this.mChannel  = channel;
        this.presenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d("actionACTIONXXXXXXXXXx", action);

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.d("XXXXXXXXXx", "WIFI_P2P_PEERS_CHANGED_ACTION");
            if (mManager != null) {
                mManager.requestPeers(mChannel, presenter.getPeersListener());
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            Log.d("XXXXXXXXXx", "WIFI_P2P_CONNECTION_CHANGED_ACTION");
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()){
                WifiP2pGroup group = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
                List<WifiP2pDevice> clientList = new ArrayList<>(group.getClientList());
                if(!clientList.isEmpty()){
                    presenter.setCurrentPeerName(clientList.get(0).deviceName);
                } else {
                    presenter.setCurrentPeerName(group.getOwner().deviceName);
                }

                Log.d("XXXXXXXXXx", "Rame QENI tooo");
                mManager.requestConnectionInfo(mChannel, presenter.getConnectionInfoListener());
            } else {
                int c = 1;
//                mActivity.status.setText("Device disconnected");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}
