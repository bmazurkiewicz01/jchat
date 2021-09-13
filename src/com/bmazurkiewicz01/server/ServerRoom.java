package com.bmazurkiewicz01.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerRoom {
    private String name;
    private String owner;
    private List<ClientThread> clientList;

    public ServerRoom(String name, String owner) {
        this.name = name;
        this.owner = owner;
        clientList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<ClientThread> getClientList() {
        return clientList;
    }

    public void addClient(ClientThread client) {
        if (client != null) clientList.add(client);
    }

    public void removeClient(ClientThread client) {
        if (client != null) clientList.remove(client);
    }

    public int getConnected() {
        return clientList.size();
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%d", name, owner, getConnected());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerRoom that = (ServerRoom) o;
        return name.equals(that.name) && owner.equals(that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, owner) * 17;
    }
}
