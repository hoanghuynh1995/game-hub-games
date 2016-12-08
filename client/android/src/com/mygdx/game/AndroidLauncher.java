package com.mygdx.game;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.io.File;

import io.socket.client.IO;
import io.socket.client.Socket;
import pong.Pong;

public class AndroidLauncher extends AndroidApplication {
	private String gameName;
	Socket socket;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;

		requestPermission();
		try {
			socket = IO.socket("http://192.168.0.101:8080");
			socket.connect();
		} catch(Exception e){
			System.out.println(e);
		}
		initialize(new Pong(socket), config);
	}

	private void requestPermission() {
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
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE)
				!= PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.INTERNET)) {

			} else {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
						2);
			}
		}
	}

}
