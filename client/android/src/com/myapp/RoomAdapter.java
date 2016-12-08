package com.myapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    List<Room> list;
    Context context;
    public RoomAdapter(List<Room> list, Context context){
        this.list = list;
        this.context = context;
    }
    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_row,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RoomAdapter.ViewHolder holder, int position) {
        holder.roomName.setText(list.get(position).getRoomName());
        holder.hostName.setText(list.get(position).getHostName());
        holder.gameName.setText(list.get(position).getGameName());
        holder.roomState.setText(list.get(position).getState());

        final int pos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Global.socket.emit("joinRoom",list.get(pos).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomName, hostName, gameName, roomState;
        public ViewHolder(View itemView) {
            super(itemView);
            roomName = (TextView)itemView.findViewById(R.id.roomName);
            hostName = (TextView)itemView.findViewById(R.id.hostName);
            gameName = (TextView)itemView.findViewById(R.id.gameName);
            roomState = (TextView)itemView.findViewById(R.id.roomState);
        }
    }
}
