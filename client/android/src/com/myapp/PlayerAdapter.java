package com.myapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mygdx.game.R;

import java.util.List;

/**
 * Created by hoang on 11/18/2016.
 */

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.ViewHolder> {
    List<String> list;
    public PlayerAdapter(List<String> list){
        this.list = list;
    }
    @Override
    public PlayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_row,parent,false);
        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(PlayerAdapter.ViewHolder holder, int position) {
        holder.playerName.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView playerName;
        public ViewHolder(View itemView) {
            super(itemView);
            playerName = (TextView)itemView.findViewById(R.id.playerName);
        }
    }
}
