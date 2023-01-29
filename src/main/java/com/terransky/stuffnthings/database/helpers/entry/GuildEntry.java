package com.terransky.stuffnthings.database.helpers.entry;

import com.terransky.stuffnthings.database.helpers.Property;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class GuildEntry {

    @BsonProperty("guildId")
    private String idReference;
    @BsonProperty("killMaximum")
    private Long killMaximum;
    @BsonProperty("killTimeout")
    private Long killTimeout;
    @BsonProperty("reportWebhook")
    private String webhookId;
    @BsonProperty("reportResponse")
    private String reportResponse;

    public GuildEntry() {
    }

    public GuildEntry(String idReference) {
        this.idReference = idReference;
        this.killMaximum = 5L;
        this.killTimeout = TimeUnit.MINUTES.toMillis(10);
        this.reportResponse = "Got it. Message has been reported.";
    }

    @BsonProperty("guildId")
    public String getIdReference() {
        return idReference;
    }

    @BsonProperty("guildId")
    public void setIdReference(String idReference) {
        this.idReference = idReference;
    }

    @BsonProperty("killMaximum")
    public Long getKillMaximum() {
        return killMaximum;
    }

    @BsonProperty("killMaximum")
    public GuildEntry setKillMaximum(Long killMaximum) {
        this.killMaximum = killMaximum;
        return this;
    }

    @BsonProperty("killTimeout")
    public Long getKillTimeout() {
        return killTimeout;
    }

    @BsonProperty("killTimeout")
    public GuildEntry setKillTimeout(Long killTimeout) {
        this.killTimeout = killTimeout;
        return this;
    }

    @BsonProperty("reportWebhook")
    public String getWebhookId() {
        return webhookId;
    }

    @BsonProperty("reportWebhook")
    public void setWebhookId(String webhookId) {
        this.webhookId = webhookId;
    }

    @BsonProperty("reportResponse")
    public String getReportResponse() {
        return reportResponse;
    }

    @BsonProperty("reportResponse")
    public void setReportResponse(String reportResponse) {
        this.reportResponse = reportResponse;
    }

    public Optional<Object> getProperty(@NotNull Property property) {
        switch (property) {
            case KILLS_MAX -> {
                return Optional.ofNullable(killMaximum);
            }
            case KILLS_TIMEOUT -> {
                return Optional.ofNullable(killTimeout);
            }
            case REPORT_WEBHOOK -> {
                return Optional.ofNullable(webhookId);
            }
            case REPORT_RESPONSE -> {
                return Optional.ofNullable(reportResponse);
            }
            default -> throw new IllegalArgumentException(String.format("%S is not a guild property.", property));
        }
    }

    @Override
    public String toString() {
        return "GuildEntry{" +
            "idReference='" + idReference + '\'' +
            ", killMaximum=" + killMaximum +
            ", killTimeout=" + killTimeout +
            ", webhookId='" + webhookId + '\'' +
            ", reportResponse='" + reportResponse + '\'' +
            '}';
    }
}
