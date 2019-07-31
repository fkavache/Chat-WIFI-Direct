package com.example.vache.wifichat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;


public class MainPresenter implements MainContract._Presenter {

    private MainContract._View view;
    private List<WifiP2pDevice> peers;


    private String currentPeerName;

    public MainPresenter(MainContract._View view) {
        this.view = view;
//        String[] permissions = {android.Manifest.permission.ACCESS_WIFI_STATE,
//                android.Manifest.permission.CHANGE_WIFI_STATE,
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.CHANGE_NETWORK_STATE,
//                android.Manifest.permission.INTERNET,
//                android.Manifest.permission.ACCESS_NETWORK_STATE,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//        };
        if (!hasPermissions(view.getViewContext(),  android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            requestPermissions(view.getViewContext(),   android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onClickPresenter(WifiP2pDevice p2pdevice) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = p2pdevice.deviceAddress;
        currentPeerName = p2pdevice.deviceName;
        view.onClickView(p2pdevice, config);
    }

    @Override
    public WifiP2pManager.PeerListListener getPeersListener() {
        this.peers = new ArrayList<>();
        WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                if (!peerList.getDeviceList().equals(peers)) {
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

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void requestPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 1);
            }
        }
    }

    public String getCurrentPeerName() {
        return currentPeerName;
    }

    public void setCurrentPeerName(String currentPeerName) {
        this.currentPeerName = currentPeerName;
    }
}
