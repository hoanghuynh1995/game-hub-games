package com.myapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.AndroidLauncher;
import com.mygdx.game.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


/**
 * Created by hoang on 11/4/2016.
 */

public class MainActivity extends AppCompatActivity{
//    @BindView(R.id.btnStartGame1)
//    Button btnStartGame1;
//    @BindView(R.id.btnStartGame2)
//    Button btnStartGame2;
    @BindView(R.id.btnCreateRoom)
    Button btnCreateRoom;
    ArrayList<Room> rooms;
    RoomAdapter roomAdapter;
    RecyclerView roomRv;

    Socket socket;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);
        requestPermission();

        setRecyclerView();
        connectSocket();

    }
    public void connectSocket(){
        try {
            Global.socket = IO.socket("http://192.168.1.14:8080");
            Global.socket.connect();
            socket = Global.socket;
        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void joinRoom(String roomId){
        socket.emit("joinRoom",roomId);
    }
    void setRecyclerView(){
        rooms = new ArrayList<Room>();
        roomAdapter = new RoomAdapter(rooms,this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        roomRv = (RecyclerView)findViewById(R.id.roomList);
        roomRv.setAdapter(roomAdapter);
        roomRv.setLayoutManager(layoutManager);
    }
//    @OnClick(R.id.btnStartGame1)
//    void startGame1(){
//        Intent intent = new Intent(this, AndroidLauncher.class);
//        intent.putExtra("GameName","game1");
//        startActivity(intent);
//    }
//    @OnClick(R.id.btnStartGame2)
//    void startGame2(){
//        Intent intent = new Intent(this, AndroidLauncher.class);
//        intent.putExtra("GameName","game2");
//        startActivity(intent);
//    }
    @OnClick(R.id.btnCreateRoom)
    public void createRoom(){
        EditText etRoomName = (EditText)findViewById(R.id.etRoomName);
        String roomName = etRoomName.getText().toString();
        if(!roomName.equals("")) {
            socket.emit("createRoom", roomName);
        }else{
            Toast.makeText(this,"You must enter room name",Toast.LENGTH_LONG).show();
        }
    }
    private void requestPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.INTERNET},
                        1);
            }
        }
    }
    public void configSocketEvents(){
        socket.on("connected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject)args[0];
                try {
                    Global.name = data.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("getRooms", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray data = (JSONArray) args[0];
                try {
                    for(int i=0;i<data.length();i++){
                        JSONObject room = (JSONObject)data.getJSONObject(i);
                        Room r = new Room();
                        String roomId = room.getString("id");
                        String roomName = room.getString("roomName");
                        String hostName = room.getString("hostName");
                        String roomState = room.getString("roomState");
                        String gameName = room.getString("gameName");
                        r.setId(roomId);
                        r.setRoomName(roomName);
                        r.setHostName(hostName);
                        r.setState(roomState);
                        r.setGameName(gameName);
                        rooms.add(r);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                roomAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("newRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject room = (JSONObject) args[0];
                try {
                    Room r = new Room();
                    String roomId = room.getString("id");
                    String roomName = room.getString("roomName");
                    String hostName = room.getString("hostName");
                    String roomState = room.getString("roomState");
                    String gameName = room.getString("gameName");
                    r.setId(roomId);
                    r.setRoomName(roomName);
                    r.setHostName(hostName);
                    r.setState(roomState);
                    r.setGameName(gameName);
                    rooms.add(r);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //roomAdapter.notifyDataSetChanged();
                            roomRv.setAdapter(roomAdapter);
                            Toast.makeText(getBaseContext(),"Number of rooms: " + rooms.size(),Toast.LENGTH_LONG).show();
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("removeRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject room = (JSONObject) args[0];
                try {
                    for(int i=0;i<rooms.size();i++){
                        if(room.getString("id").equals(rooms.get(i).getId())){
                            rooms.remove(i);
                            i--;
                            break;
                        }
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roomRv.setAdapter(roomAdapter);
                            Toast.makeText(getBaseContext(),"Number of rooms: " + rooms.size(),Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).on("createRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject room = (JSONObject) args[0];
                Room r = getRoomInfo(room);
                ArrayList<String> players = getPlayersInRoom(room);

                Intent intent = new Intent(MainActivity.this,RoomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("room",r);
                bundle.putStringArrayList("players",players);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        }).on("joinRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject room = (JSONObject) args[0];
                Room r = getRoomInfo(room);
                ArrayList<String> players = getPlayersInRoom(room);

                Intent intent = new Intent(MainActivity.this,RoomActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("room",r);
                bundle.putStringArrayList("players",players);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    private void unregisterSocketEvents(){
        socket.off("getRooms");
        socket.off("newRoom");
        socket.off("removeRoom");
        socket.off("createRoom");
        socket.off("joinRoom");
    }
    private Room getRoomInfo(JSONObject data){
        Room roomData = null;
        try {
            JSONObject room = (JSONObject)data.getJSONObject("room");

            String roomId = room.getString("id");
            String roomName = room.getString("roomName");
            String hostName = room.getString("hostName");
            String roomState = room.getString("roomState");
            String gameName = room.getString("gameName");

            roomData = new Room();
            roomData.setId(roomId);
            roomData.setRoomName(roomName);
            roomData.setHostName(hostName);
            roomData.setState(roomState);
            roomData.setGameName(gameName);
        }catch(JSONException e){
            e.printStackTrace();
        }
        return roomData;
    }
    private ArrayList<String> getPlayersInRoom(JSONObject data){
        ArrayList<String> list = null;
        try {
            JSONObject players = (JSONObject) data.getJSONObject("players");
            Iterator iterator = players.keys();
            list = new ArrayList<>();
            while(iterator.hasNext()){
                list.add((String)iterator.next());
            }
//            list = new ArrayList<>();
//            for(int i=0;i<players.length();i++){
//                list.add(players.getString(i));
//            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onResume() {
        configSocketEvents();
        socket.emit("getRooms");
        super.onResume();
    }

    @Override
    protected void onPause() {
        rooms.clear();
        roomAdapter.notifyDataSetChanged();
        unregisterSocketEvents();
        super.onPause();
    }

}
