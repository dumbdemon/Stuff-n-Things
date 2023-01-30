package com.terransky.stuffnthings.database.helpers;

import com.mongodb.MongoInterruptedException;
import com.mongodb.MongoTimeoutException;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Subscribers {
    public static class ObjectSubscriber<T> implements Subscriber<T> {

        private final List<T> objects;
        private final List<RuntimeException> errors;
        private final CountDownLatch latch;
        private volatile Subscription subscription;

        public ObjectSubscriber() {
            this.objects = new ArrayList<>();
            this.errors = new ArrayList<>();
            this.latch = new CountDownLatch(1);
        }

        @Override
        public void onSubscribe(Subscription s) {
            this.subscription = s;
        }

        @Override
        public void onNext(T t) {
            objects.add(t);
        }

        @Override
        public void onError(@NotNull Throwable t) {
            if (t instanceof RuntimeException exception) {
                errors.add(exception);
            } else {
                errors.add(new RuntimeException("An error occurred", t));
            }
            onComplete();
        }

        @Override
        public void onComplete() {
            latch.countDown();
        }

        public List<T> getObjects() {
            return objects;
        }

        public RuntimeException getError() {
            if (errors.size() > 0) {
                return errors.get(0);
            }
            return null;
        }

        public List<T> get() {
            return await().getObjects();
        }

        public List<T> get(final long timeout, TimeUnit unit) {
            return await(timeout, unit).getObjects();
        }

        public T first() {
            List<T> objects = await().getObjects();
            return !objects.isEmpty() ? getObjects().get(0) : null;
        }

        public ObjectSubscriber<T> await() {
            return await(60, TimeUnit.SECONDS);
        }

        public ObjectSubscriber<T> await(final long timeout, final TimeUnit unit) {
            subscription.request(Integer.MAX_VALUE);
            try {
                if (!latch.await(timeout, unit)) {
                    throw new MongoTimeoutException("Publisher timed out");
                }
            } catch (InterruptedException e) {
                throw new MongoInterruptedException("Action interrupted", e);
            }
            if (!errors.isEmpty()) {
                throw errors.get(0);
            }
            return this;
        }
    }

    public static class OperationSubscriber<T> extends ObjectSubscriber<T> {

        @Override
        public void onSubscribe(Subscription s) {
            super.onSubscribe(s);
            s.request(Integer.MAX_VALUE);
        }
    }

    public static class ConsumerSubscriber<T> extends OperationSubscriber<T> {

        private final Consumer<T> consumer;

        public ConsumerSubscriber(final Consumer<T> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onNext(T t) {
            consumer.accept(t);
            super.onNext(t);
        }
    }

    public static class LoggerSubscriber<T> extends ConsumerSubscriber<T> {
        public LoggerSubscriber(Logger log) {
            super(t -> log.info(t.toString()));
        }
    }
}
