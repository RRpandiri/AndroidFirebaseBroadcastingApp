package com.uhcl.reachapp.data_models;

import android.os.Parcel;
import android.os.Parcelable;

public class BroadcastPOJO implements Parcelable {

    private String owner = "";
    private String group_name = "";
    private String members = "";

    public BroadcastPOJO() {
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    protected BroadcastPOJO(Parcel in) {
        this.owner = in.readString();
        this.group_name = in.readString();
        this.members = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.owner);
        dest.writeString(this.group_name);
        dest.writeString(this.members);
    }

    public static final Creator<BroadcastPOJO> CREATOR = new Creator<BroadcastPOJO>() {
        @Override
        public BroadcastPOJO createFromParcel(Parcel source) {
            return new BroadcastPOJO(source);
        }

        @Override
        public BroadcastPOJO[] newArray(int size) {
            return new BroadcastPOJO[size];
        }
    };
}
