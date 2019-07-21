package com.example.vache.wifichat;

import android.net.wifi.p2p.WifiP2pDevice;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PeersAdapter extends RecyclerView.Adapter<PeersAdapter.PeerHolder> {

    private MainContract._Presenter presenter;
    private PeersAdapter th;
    private List<WifiP2pDevice> data;

    public PeersAdapter(MainContract._Presenter presenter) {
        this.presenter = presenter;
        this.th = this;
        data = new ArrayList<>();
    }

    public void setData(List<WifiP2pDevice> data){
        this.data = data;
        notifyDataSetChanged();
    }

    public List<WifiP2pDevice> getData(){
        return data;
    }

    @NonNull
    @Override
    public PeerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, viewGroup, false);

        return new PeerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PeerHolder itemViewHolder, int index) {

        WifiP2pDevice device = data.get(index);
        String deviceName = device.deviceName;

        itemViewHolder.device_name.setText(deviceName);
    }

//    private String getFormatedDate(Date date){
//        String pattern = "dd/MM/YYYY";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        return simpleDateFormat.format(date);
//    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class PeerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView device_name;

        public PeerHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            device_name = itemView.findViewById(R.id.list_item);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            WifiP2pDevice device = data.get(position);

            presenter.onClickPresenter(device);
        }
    }

//    public String filter(String str){
//        int maxLen = 15;
//        if(layoutManagerType == LayoutManagerType.GRID_LAYOUT_MANAGER)
//            maxLen = 10;
//        if(str.length() >= maxLen)
//            return str.substring(0, maxLen) + "...";
//        return str;
//    }
}