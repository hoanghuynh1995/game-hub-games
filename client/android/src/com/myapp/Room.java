package com.myapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hoang on 11/18/2016.
 */

public class Room implements Parcelable{
    private String id;
    private String roomName;
    private String hostName;
    private String gameName;
    private String state;

    public Room(){};
    protected Room(Parcel in) {
        id = in.readString();
        roomName = in.readString();
        hostName = in.readString();
        gameName = in.readString();
        state = in.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(roomName);
        parcel.writeString(hostName);
        parcel.writeString(gameName);
        parcel.writeString(state);
    }
}
