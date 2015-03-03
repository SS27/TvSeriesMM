package com.spstanchev.tvseries.models;

/**
 * Created by Stefan on 2/8/2015.
 */
public class Cast {

    private Person person;
    private Character character;

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
    public Character getCharacter() {
        return character;
    }

    /**
     * @param character The character
     */
    public void setCharacter(Character character) {
        this.character = character;
    }
}
