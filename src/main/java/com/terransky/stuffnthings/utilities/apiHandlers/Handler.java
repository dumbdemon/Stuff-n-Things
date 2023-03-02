package com.terransky.stuffnthings.utilities.apiHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class Handler {

    private final ThreadFactory threadFactory;

    protected Handler(String threadName) {
        this.threadFactory = job -> {
            Thread thread = new Thread(job);
            thread.setName(threadName);
            thread.setDaemon(true);
            return thread;
        };
    }

    protected HttpClient getHttpClient(ExecutorService service) {
        return HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(5))
            .executor(service)
            .build();
    }

    protected ThreadFactory getThreadFactory() {
        return threadFactory;
    }

    protected ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
