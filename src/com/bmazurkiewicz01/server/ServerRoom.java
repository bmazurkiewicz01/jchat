package com.bmazurkiewicz01.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerRoom {
    private String name;
    private String owner;
    private final List<ClientThread> clientList;
    private final List<ClientThread> bannedClientList;

    public ServerRoom(String name, String owner) {
        this.name = name;
        this.owner = owner;
        clientList = new ArrayList<>();
        bannedClientList = new ArrayList<>();
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

    public List<ClientThread> getBannedClientList() {
        return bannedClientList;
    }

    public void addClient(ClientThread client) {
        if (client != null) clientList.add(client);
    }

    public void removeClient(ClientThread client) {
        if (client != null) clientList.remove(client);
    }

    public void addBannedClient(ClientThread client) {
        if (client != null) bannedClientList.add(client);
    }

    public void removeBannedClient(ClientThread client) {
        if (client != null) bannedClientList.remove(client);
    }

    public boolean isClientBanned(ClientThread client) {
        if (client != null) {
            for (ClientThread clientThread : bannedClientList) {
                if (clientThread.getClientName().equals(client.getClientName())) {
                    return true;
                }
            }
        }
        return false;
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
