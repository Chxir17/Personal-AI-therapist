package com.aitherapist.aitherapist.scheduled;

import jakarta.persistence.*;

@Entity
public class ScheduledMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String chatId;
    private String messageText;

    public ScheduledMessage() {
    }

    public ScheduledMessage(Long id, String chatId, String messageText) {
        this.id = id;
        this.chatId = chatId;
        this.messageText = messageText;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}