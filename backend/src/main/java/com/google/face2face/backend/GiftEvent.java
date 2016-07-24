package com.google.face2face.backend;

public class GiftEvent {
    public String name;
    public String maleText;
    public String femaleText;
    public String[] gifts = new String[3];

    public GiftEvent(String name) {
        this.name = name;
    }
}
