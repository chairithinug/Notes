package com.u.notes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class NotesInstance implements Parcelable {


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NotesInstance createFromParcel(Parcel in) {
            return new NotesInstance(in);
        }

        public NotesInstance[] newArray(int size) {
            return new NotesInstance[size];
        }
    };

    private String createdDate;
    private String lastModifiedDate;
    private String forWhom;
    private String title;
    private String data;

    public NotesInstance() {        // blank constructor
    }

    public NotesInstance(String createdDate, String lastModifiedDate, String forWhom, String title, String data) {        // blank constructor
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
        this.forWhom = forWhom;
        this.title = title;
        this.data = data;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getForWhom() {
        return forWhom;
    }

    public void setForWhom(String forWhom) {
        this.forWhom = forWhom;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public NotesInstance(Parcel in) {
        this.createdDate = in.readString();
        this.lastModifiedDate = in.readString();
        this.forWhom = in.readString();
        this.title = in.readString();
        this.data = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.createdDate);
        dest.writeString(this.lastModifiedDate);
        dest.writeString(this.forWhom);
        dest.writeString(this.title);
        dest.writeString(this.data);
    }

    @Override
    public String toString() {
        return toJSON().toString();
    }

    public JSONObject toJSON() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("createdDate", createdDate);
            jo.put("lastModifiedDate", lastModifiedDate);
            jo.put("forWhom", forWhom);
            jo.put("title", title);
            jo.put("data", data);
            return jo;
        } catch (JSONException e) {
            return null;
        }
    }
}
