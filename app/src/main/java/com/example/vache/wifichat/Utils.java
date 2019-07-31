package com.example.vache.wifichat;

import android.net.wifi.p2p.WifiP2pManager;

public class Utils {
    private static final Utils ourInstance = new Utils();

    public static Utils getInstance() {
        return ourInstance;
    }

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;

    private Utils() {
    }

    public WifiP2pManager getManager() {
        return mManager;
    }

    public void setManager(WifiP2pManager mManager) {
        this.mManager = mManager;
    }

    public WifiP2pManager.Channel getChannel() {
        return mChannel;
    }

    public void setChannel(WifiP2pManager.Channel mChannel) {
        this.mChannel = mChannel;
    }
}
