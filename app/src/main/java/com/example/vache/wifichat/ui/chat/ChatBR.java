package com.example.vache.wifichat.ui.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.vache.wifichat.MainPresenter;

import androidx.navigation.fragment.NavHostFragment;

public class ChatBR extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private ChatContract.Presenter presenter;

    public ChatBR(WifiP2pManager manager
            , WifiP2pManager.Channel channel
            , ChatContract.Presenter presenter) {
        super();

        this.mManager = manager;
        this.mChannel = channel;
        this.presenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.e("PPPPPPPPPPPPPPPPPP", "WIFI_P2P_PEERS_CHANGED_ACTION");
//            if (mManager != null) {
//                mManager.requestPeers(mChannel, presenter.getPeersListener());
//            }
            presenter.closeChat();
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            Log.e("PPPPPPPPPPPPPPPPPP", "WIFI_P2P_CONNECTION_CHANGED_ACTION");
//            if (presenter.isNeedGoHome()) {
//                presenter.closeChat();
//            }
//            if (mManager == null) {
//                return;
//            }
//
//            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
//
//            if (networkInfo.isConnected()){
//                Log.e("PPPPPPPPPPPPPPPPPP", "Rame QENI tooo");
//                mManager.requestConnectionInfo(mChannel, presenter.getConnectionInfoListener());
//            } else {
//                int c = 1;
////                mActivity.status.setText("Device disconnected");
//            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}
