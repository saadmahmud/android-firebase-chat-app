package com.surf.saad.app.data;

public class MessageAttachment {

    private String attachmentName;
    private String attachmentDownloadUrl;
    private String messageId;
    private String attachmentType;

    public MessageAttachment(String attachmentName, String attachmentDownloadUrl, String messageId, String attachmentType) {
        this.attachmentName = attachmentName;
        this.attachmentDownloadUrl = attachmentDownloadUrl;
        this.messageId = messageId;
        this.attachmentType = attachmentType;
    }

    public String getAttachmentName() {
        return attachmentName;
    }

    public void setAttachmentName(String attachmentName) {
        this.attachmentName = attachmentName;
    }

    public String getAttachmentDownloadUrl() {
        return attachmentDownloadUrl;
    }

    public void setAttachmentDownloadUrl(String attachmentDownloadUrl) {
        this.attachmentDownloadUrl = attachmentDownloadUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }


}
