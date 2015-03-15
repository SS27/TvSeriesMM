package com.spstanchev.tvseries.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Stefan on 2/8/2015.
 */
public class Cast implements Parcelable{
    private Person person;
    private Person character;

    public Cast() {
    }

    /**
     * @return The person
     */
    public Person getPerson() {
        return person;
    }

    /**
     * @param person The person
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * @return The character
     */
    public Person getCharacter() {
        return character;
    }

    /**
     * @param character The character
     */
    public void setCharacter(Person character) {
        this.character = character;
    }

    protected Cast(Parcel in) {
        person = (Person) in.readValue(Person.class.getClassLoader());
        character = (Person) in.readValue(Person.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(person);
        dest.writeValue(character);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Cast> CREATOR = new Parcelable.Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel in) {
            return new Cast(in);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
}
