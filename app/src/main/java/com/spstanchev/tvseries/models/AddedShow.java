package com.spstanchev.tvseries.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stefan on 3/3/2015.
 */
public class AddedShow implements Parcelable {
    private Show show;
    private boolean isAdded;

    public AddedShow() {
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean isAdded) {
        this.isAdded = isAdded;
    }

    protected AddedShow (Parcel in){
        show = (Show) in.readValue(Show.class.getClassLoader());
        isAdded = (in.readInt() != 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(show);
        dest.writeInt(isAdded ? 1 : 0);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AddedShow> CREATOR = new Parcelable.Creator<AddedShow>() {
        @Override
        public AddedShow createFromParcel(Parcel in) {
            return new AddedShow(in);
        }

        @Override
        public AddedShow[] newArray(int size) {
            return new AddedShow[size];
        }
    };
}
