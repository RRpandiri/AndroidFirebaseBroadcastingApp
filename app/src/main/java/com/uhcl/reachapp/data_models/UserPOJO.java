package com.uhcl.reachapp.data_models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserPOJO implements Parcelable {

    private String phonenumber = "";
    private String username = "";
    private String contactname_inphone = "";
    private String profilepic_firebaseuri = null;
    private String profilepic_phoneuri = null;
    private String chatWith = "";

    //for Broadcast..
    private String IsBroadcast = "";
    private String single_group_key = "";
    private String single_owner = "";
    private String single_group_name = "";
    private String single_members = "";
    private String ALL_broadcastInfo = "";

    public UserPOJO() {
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContactname_inphone() {
        return contactname_inphone;
    }

    public void setContactname_inphone(String contactname_inphone) {
        this.contactname_inphone = contactname_inphone;
    }

    public String getProfilepic_phoneuri() {
        return profilepic_phoneuri;
    }

    public void setProfilepic_phoneuri(String profilepic_phoneuri) {
        this.profilepic_phoneuri = profilepic_phoneuri;
    }

    public String getProfilepic_firebaseuri() {
        return profilepic_firebaseuri;
    }

    public void setProfilepic_firebaseuri(String profilepic_firebaseuri) {
        this.profilepic_firebaseuri = profilepic_firebaseuri;
    }

    public String getChatWith() {
        return chatWith;
    }

    public void setChatWith(String chatWith) {
        this.chatWith = chatWith;
    }

    public String getIsBroadcast() {
        return IsBroadcast;
    }

    public void setIsBroadcast(String isBroadcast) {
        this.IsBroadcast = isBroadcast;
    }

    public String getSingle_owner() {
        return single_owner;
    }

    public void setSingle_owner(String single_owner) {
        this.single_owner = single_owner;
    }

    public String getSingle_group_name() {
        return single_group_name;
    }

    public void setSingle_group_name(String single_group_name) {
        this.single_group_name = single_group_name;
    }

    public String getSingleMembers() {
        return single_members;
    }

    public void setSingleMembers(String members) {
        this.single_members = members;
    }

    public String getALL_broadcastInfo() {
        return ALL_broadcastInfo;
    }

    public void setALL_broadcastInfo(String ALL_broadcastInfo) {
        this.ALL_broadcastInfo = ALL_broadcastInfo;
    }

    protected UserPOJO(Parcel in) {
        this.phonenumber = in.readString();
        this.username = in.readString();
        this.contactname_inphone = in.readString();
        this.profilepic_firebaseuri = in.readString();
        this.profilepic_phoneuri = in.readString();
        this.chatWith = in.readString();
        this.IsBroadcast = in.readString();
        this.single_owner = in.readString();
        this.single_group_name = in.readString();
        this.single_members = in.readString();
        this.ALL_broadcastInfo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phonenumber);
        dest.writeString(this.username);
        dest.writeString(this.contactname_inphone);
        dest.writeString(this.profilepic_firebaseuri);
        dest.writeString(this.profilepic_phoneuri);
        dest.writeString(this.chatWith);
        dest.writeString(this.IsBroadcast);
        dest.writeString(this.single_owner);
        dest.writeString(this.single_group_name);
        dest.writeString(this.single_members);
        dest.writeString(this.ALL_broadcastInfo);
    }

    public static final Creator<UserPOJO> CREATOR = new Creator<UserPOJO>() {
        @Override
        public UserPOJO createFromParcel(Parcel source) {
            return new UserPOJO(source);
        }

        @Override
        public UserPOJO[] newArray(int size) {
            return new UserPOJO[size];
        }
    };
}
