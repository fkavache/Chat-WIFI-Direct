package com.example.vache.wifichat.ui.model;

import java.io.Serializable;
import java.util.List;

public class Chat  implements Serializable {

    private long id;

    private User user;

    private List<Message> messages;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
