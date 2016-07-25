package com.google.face2face.backend;

public class GiftEvent {
    public String name;
    public String maleText;
    public String femaleText;
    public Gift[] gifts = new Gift[3];
    public String description;

    public GiftEvent(String name) {
        this.name = name;
    }
}
