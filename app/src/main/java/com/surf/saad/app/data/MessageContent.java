package com.surf.saad.app.data;

import java.util.HashMap;
import java.util.Map;

public class MessageContent {
    String chatroomId;
    String messageId;
    String messageText;
    Map<String, String> messageStatus = new HashMap<>();
    String messageTimestamp;
    String userId;
    boolean isUserOwner;
    boolean hasAttachment;
    Map<String, String> attachmentDetail = new HashMap<>();

    public MessageContent() {
    }

    public MessageContent(String chatroomId, String messageId, String
            messageText, String messageTimestamp, String userId, Map<String, String> messageStatus) {
        this.chatroomId = chatroomId;
        this.messageId = messageId;
        this.messageText = messageText;
        this.messageTimestamp = messageTimestamp;
        this.userId = userId;
        this.messageStatus = messageStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessageTimestamp() {
        return messageTimestamp;
    }

    public void setMessageTimestamp(String messageTimestamp) {
        this.messageTimestamp = messageTimestamp;
    }

    public Map<String, String> getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Map<String, String> messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChatroomId() {
        return chatroomId;
    }

    public void setChatroomId(String chatroomId) {
        this.chatroomId = chatroomId;
    }

    public boolean isUserOwner() {
        return isUserOwner;
    }

    public void setUserOwner(boolean userOwner) {
        isUserOwner = userOwner;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public Map<String, String> getAttachmentDetail() {
        return attachmentDetail;
    }

    public void setAttachmentDetail(Map<String, String> attachmentDetail) {
        this.attachmentDetail = attachmentDetail;
    }
}
