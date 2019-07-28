package com.example.vache.wifichat.ui.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Chat  implements Serializable {

    private long id;

    private User user;

    private List<Message> messages;

    private int countMessages;

    private Date firstMessageDate;
    private Date lastMessageDate;

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

    public int getCountMessages() {
        return countMessages;
    }

    public void setCountMessages(int countMessages) {
        this.countMessages = countMessages;
    }

    public Date getFirstMessageDate() {
        return firstMessageDate;
    }

    public void setFirstMessageDate(Date firstMessageDate) {
        this.firstMessageDate = firstMessageDate;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }
}
