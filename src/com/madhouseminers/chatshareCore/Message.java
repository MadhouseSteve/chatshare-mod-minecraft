package com.madhouseminers.chatshareCore;

import com.google.gson.Gson;

class Message {
    private String name;
    private String message;
    private MessageType type;
    private String sender;

    public Message(MessageType type) {
        this.type = type;
    }

    public Message setName(String name) {
        this.name = name;
        return this;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public String getMessage() {
        return this.message;
    }

    public MessageType getType() {
        return this.type;
    }

    public String getSender() {
        return this.sender;
    }

    public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Message fromJSON(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }
}
