package com.minkiapps.cardability.test.net.model;

public class Joke {

    private String id;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Joke{" +
                "id='" + id + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
