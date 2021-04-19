package com.customwrld.flame.pigeon;

public abstract class Payload {
    public String name;

    public abstract void receive(Payload payload);

    public abstract void send();

    public abstract String toJson();

    public abstract Payload fromJson(String json);
}
