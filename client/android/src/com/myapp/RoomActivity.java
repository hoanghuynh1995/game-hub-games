package com.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mygdx.game.AndroidLauncher;
import com.mygdx.game.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by hoang on 11/18/2016.
 */

public class RoomActivity extends AppCompatActivity {
    RecyclerView roomRv;
    PlayerAdapter adapter;
    ArrayList<String> players;
    Button start;
    Socket socket;
    Room room = new Room();

    TextView roomName, hostName, gameName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.room_layout);

        setRecyclerView();
        init();
        socket = Global.socket;
        configSocketEvents();
    }

    private void init(){
        roomName = (TextView)findViewById(R.id.tvRoomName);
        hostName = (TextView)findViewById(R.id.tvHostName);
        gameName = (TextView)findViewById(R.id.tvGameName);

        Bundle bundle = getIntent().getExtras();
        room = bundle.getParcelable("room");
        roomName.setText(room.getRoomName());
        hostName.setText(room.getHostName());
        gameName.setText(room.getGameName());

        players.addAll(bundle.getStringArrayList("players"));
        adapter.notifyDataSetChanged();

        start = (Button)findViewById(R.id.btnStartGame);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RoomActivity.this, AndroidLauncher.class);
                intent.putExtra("GameName","game1");
                startActivity(intent);
            }
        });
    }

    private void setRecyclerView() {
        players = new ArrayList<>();
        adapter = new PlayerAdapter(players);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        roomRv = (RecyclerView)findViewById(R.id.playerList);
        roomRv.setAdapter(adapter);
        roomRv.setLayoutManager(layoutManager);
    }
    public void leaveRoom(){
        if(room.getHostName().equals(Global.name)){
            socket.emit("removeRoom");
        }else{
            socket.emit("leaveRoom",room.getId());
        }
    }
    public void configSocketEvents(){
        socket.on("roomInfo", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                try {
                    room.setId(data.getString("id"));
                    room.setGameName(data.getString("gameName"));
                    room.setHostName(data.getString("hostName"));
                    room.setState(data.getString("roomState"));
                    room.setGameName(data.getString("gameName"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray data = (JSONArray) args[0];
                try {
                    for(int i=0;i<data.length();i++){
                        JSONObject player = data.getJSONObject(i);
                        String opponentName = player.getString("name");
                        players.add(opponentName);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("newPlayer",new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                String opponentName = (String)args[0];
                players.add(opponentName);
                RoomActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).on("playerLeft",new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                String opponentName = (String)args[0];
                if (opponentName.equals(Global.name)) {
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        RoomActivity.this.finish();
                    }
                }else{
                    for(int i=0;i<players.size();i++){
                        if(players.get(i).equals(opponentName)){
                            players.remove(i);
                            break;
                        }
                    }
                    RoomActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //roomRv.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            //RoomActivity.this.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).on("removeRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject)args[0];
                try {
                    if(data.getString("id").equals(room.getId())) {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack();
                        } else {
                            RoomActivity.this.finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        leaveRoom();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        socket.off("roomInfo");
//        socket.off("getPlayers");
//        socket.off("newPlayer");
//        socket.off("removeRoom");
    }
}
