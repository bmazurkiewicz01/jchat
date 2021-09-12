package com.bmazurkiewicz01.client;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Room {
    private StringProperty name;
    private StringProperty owner;
    private IntegerProperty connected;

    public Room(String name, String owner, int connected) {
        this.name = new SimpleStringProperty(name);
        this.owner = new SimpleStringProperty(owner);
        this.connected = new SimpleIntegerProperty(connected);
    }

    public String getName() {
        return name.get();
    }

    public String getOwner() {
        return owner.get();
    }

    public int getConnected() {
        return connected.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setOwner(String owner) {
        this.owner.set(owner);
    }

    public void setConnected(int connected) {
        this.connected.set(connected);
    }
}
