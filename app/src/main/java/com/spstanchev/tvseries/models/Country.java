package com.spstanchev.tvseries.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stefan on 2/8/2015.
 */
public class Country implements Parcelable {

    private String name;
    private String code;
    private String timezone;

    public Country() {
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return The timezone
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * @param timezone The timezone
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    protected Country(Parcel in) {
        name = in.readString();
        code = in.readString();
        timezone = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(timezone);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Country> CREATOR = new Parcelable.Creator<Country>() {

        @Override
        public Country createFromParcel(Parcel in) {
            return new Country(in);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };


}
