package com.uhcl.reachapp.data_models;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactsListPOJO implements Parcelable {
    public String phone, contact_name;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.phone);
        dest.writeString(this.contact_name);
    }

    public ContactsListPOJO() {
    }

    protected ContactsListPOJO(Parcel in) {
        this.phone = in.readString();
        this.contact_name = in.readString();
    }

    public static final Creator<ContactsListPOJO> CREATOR = new Creator<ContactsListPOJO>() {
        @Override
        public ContactsListPOJO createFromParcel(Parcel source) {
            return new ContactsListPOJO(source);
        }

        @Override
        public ContactsListPOJO[] newArray(int size) {
            return new ContactsListPOJO[size];
        }
    };
}
