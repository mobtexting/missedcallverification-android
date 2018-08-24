package com.mobtexting.missedcallphoneverification.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Mobtexting server response
 */
public class ServerResponse implements Serializable{

    @SerializedName("message")
    private String message;
    @SerializedName("status")
    private boolean status;
    @SerializedName("response_code")
    private int responseCode;

    public ServerResponse(String message, boolean status, int responseCode) {
        this.message = message;
        this.status = status;
        this.responseCode = responseCode;
    }

    protected ServerResponse(Parcel in) {
        message = in.readString();
        status = in.readByte() != 0;
        responseCode = in.readInt();
    }

    public static final Parcelable.Creator<ServerResponse> CREATOR = new Parcelable.Creator<ServerResponse>() {
        @Override
        public ServerResponse createFromParcel(Parcel in) {
            return new ServerResponse(in);
        }

        @Override
        public ServerResponse[] newArray(int size) {
            return new ServerResponse[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }


}
