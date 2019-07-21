package com.example.vache.wifichat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

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

        Log.d("XXXXXXXXXx", action);

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mManager != null) {
                mManager.requestPeers(mChannel, presenter.getPeersListener());
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()){
                mManager.requestConnectionInfo(mChannel, presenter.getConnectionInfoListener());
            } else {
//                mActivity.status.setText("Device disconnected");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}
