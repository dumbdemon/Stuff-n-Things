package com.terransky.stuffnthings.utilities.general.configobjects;

@SuppressWarnings("unused")
public class RequestConfig {

    private String webhookUrl;
    private String channelId;

    RequestConfig() {
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }
}
