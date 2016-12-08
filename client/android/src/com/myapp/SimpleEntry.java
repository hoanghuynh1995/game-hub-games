package com.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mygdx.game.AndroidLauncher;
import com.mygdx.game.R;

/**
 * Created by hoang on 12/6/2016.
 */

public class SimpleEntry extends AppCompatActivity {
    Button btnStart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_entry);

        btnStart = (Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SimpleEntry.this, AndroidLauncher.class);
                startActivity(intent);
            }
        });
    }
}
