package org.tensorflow.lite.examples.detection;

import android.os.Parcel;
import android.os.Parcelable;

public class Circles implements Parcelable {


    private String name;
    private String lastname;
    private String id;
    private String phone;
    private String latitud;
    private String longitud;
    private String picUrl = null;

    public Circles() {

    }

    public Circles(String name, String lastname, String id, String phone, String latitud, String longitud, String picUrl) {
        this.name = name;
        this.lastname = lastname;
        this.id = id;
        this.phone = phone;
        this.latitud = latitud;
        this.longitud = longitud;
        this.picUrl = picUrl;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    protected Circles(Parcel in) {
        name = in.readString();
        lastname = in.readString();
        id = in.readString();
        phone = in.readString();
        latitud = in.readString();
        longitud = in.readString();
        picUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(lastname);
        dest.writeString(id);
        dest.writeString(phone);
        dest.writeString(longitud);
        dest.writeString(latitud);
        dest.writeString(picUrl);
    }

    @SuppressWarnings("unused")
    public static final Creator<Circles> CREATOR = new Creator<Circles>() {
        @Override
        public Circles createFromParcel(Parcel in) {
            return new Circles(in);
        }

        @Override
        public Circles[] newArray(int size) {
            return new Circles[size];
        }
    };
}