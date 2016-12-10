package com.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mygdx.game.AndroidLauncher;
import com.mygdx.game.R;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.badlogic.gdx.Input.Keys.S;
import static com.myapp.Global.socket;

/**
 * Created by hoang on 12/6/2016.
 */

public class SimpleEntry extends AppCompatActivity {
    Button btnStart;
    public static Socket socket = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_entry);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket.emit("startGame");
            }
        });
        if(socket == null) {
            try {
                socket = IO.socket("http://192.168.0.101:8080");
                if (!socket.connected()) {
                    socket.connect();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        configSocket();
    }
    void configSocket(){
        socket.on("openGame", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Intent intent = new Intent(SimpleEntry.this, AndroidLauncher.class);
                startActivity(intent);
            }
        });
    }
}
