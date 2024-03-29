package com.example.vache.wifichat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
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

        ((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().setTitle("Chat");

        discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverPeers();
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
        mManager = Utils.getInstance().getManager();
        mChannel = Utils.getInstance().getChannel();
        peers = new ArrayList<>();
        peersV = view.findViewById(R.id.peersv);
        progressBar = view.findViewById(R.id.progressBar);
        discover = view.findViewById(R.id.discover);

        progressBar.setVisibility(View.VISIBLE);

        presenter = new MainPresenter(this);
        adapter = new PeersAdapter(presenter);

        peersV.setAdapter(adapter);
        peersV.setLayoutManager(new LinearLayoutManager(getActivity()));

        mIntentFilter = new IntentFilter();

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        statusCheckWifi();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            statusCheck();
        }

        discoverPeers();
    }

    private void statusCheckWifi() {
        WifiManager wifi = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifi.isWifiEnabled()){
            buildAlertMessageNoWifi();
        }
    }

    private void buildAlertMessageNoWifi() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your wifi seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        wifiManager.setWifiEnabled(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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
            status.setText(peers.size() + " Device found");
            adapter.setData(peers);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void connectionInfoListenerHandler(WifiP2pInfo wifiP2pInfo, InetAddress goa) {
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

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        String provider = Settings.Secure.getString(getActivity().getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.equals("")){
            buildAlertMessageNoGps();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Your location seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
