package org.tensorflow.lite.examples.detection;

import android.os.Parcel;
import android.os.Parcelable;

public class ObjectDetected implements Parcelable {

    private String name;
    private String fidelidad;
    private int imgClima;


    protected ObjectDetected() {


    }

    public int getImgClima() {
        return imgClima;
    }

    public String getFidelidad() {
        return fidelidad;
    }

    public void setFidelidad(String fidelidad) {
        this.fidelidad = fidelidad;
    }

    public void setImgClima(int imgClima) {
        this.imgClima = imgClima;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected ObjectDetected(Parcel in) {
        name = in.readString();
        fidelidad = in.readString();
        imgClima = in.readInt();
    }



    public static final Creator<ObjectDetected> CREATOR = new Creator<ObjectDetected>() {
        @Override
        public ObjectDetected createFromParcel(Parcel in) {
            return new ObjectDetected(in);
        }

        @Override
        public ObjectDetected[] newArray(int size) {
            return new ObjectDetected[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i)
    {
        parcel.writeString(name);
        parcel.writeString(fidelidad);
        parcel.writeInt(imgClima);
    }
}
