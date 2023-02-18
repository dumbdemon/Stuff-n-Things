package com.terransky.stuffnthings.database.helpers;

import com.mongodb.MongoInterruptedException;
import com.mongodb.MongoTimeoutException;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Hold all {@link Subscriber Subscribers} for {@link com.mongodb.reactivestreams.client.MongoClient MongoClient} actions
 */
@SuppressWarnings("unused")
public class Subscribers {
    public static class ObjectSubscriber<T> implements Subscriber<T> {

        private final List<T> objects;
        private final List<RuntimeException> errors;
        private final CountDownLatch latch;
        private volatile Subscription subscription;

        /**
         * The default subscriber for all {@link com.mongodb.reactivestreams.client.MongoClient MongoClient} actions
         */
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

        /**
         * Get the list of all items processed
         *
         * @return A {@link List} of type {@link T}
         */
        public List<T> getObjects() {
            return objects;
        }

        /**
         * Get a runtime exception if one had occurred during operation
         *
         * @return A {@link RuntimeException} or null
         */
        public RuntimeException getError() {
            if (errors.size() > 0) {
                return errors.get(0);
            }
            return null;
        }

        /**
         * This is the inverted function of {@link #hasNoError(Class, String)} except it does not log if there is an error.
         *
         * @return True if there is an error
         */
        public boolean hasError() {
            return getError() != null;
        }

        /**
         * This is the inverted function of {@link #hasNoError(Class, String)}.
         *
         * @param clazz   Class for the log to represent
         * @param message The message to send with the error if one has occurred
         * @return True if there is an error
         */
        public boolean hasError(Class<?> clazz, String message) {
            return !hasNoError(clazz, message);
        }

        /**
         * Checks if there is an error. If there is, it will log it.<br/>
         * Use this function for return statements.
         *
         * @param clazz   Class for the log to represent
         * @param message The message to send with the error if one has occurred
         * @return True if there is no error
         */
        public boolean hasNoError(Class<?> clazz, String message) {
            if (getError() != null) {
                LoggerFactory.getLogger(clazz).error(message, getError());
                return true;
            }
            return false;
        }

        /**
         * Get all objects of type {@link T} that got return.
         *
         * @return A {@link List} of type {@link T}
         * @throws MongoTimeoutException     If the operation takes longer than 60 seconds.
         * @throws MongoInterruptedException If the operation get interrupted for any reason.
         */
        public List<T> get() {
            return await().getObjects();
        }

        /**
         * Get all objects of type {@link T} that got returned
         *
         * @param timeout The amount of time to wait
         * @param unit    The unit of time to wait
         * @return A {@link List} of type {@link T}
         * @throws MongoTimeoutException     If the operation takes longer than the time given.
         * @throws MongoInterruptedException If the operation get interrupted for any reason.
         */
        public List<T> get(final long timeout, TimeUnit unit) {
            return await(timeout, unit).getObjects();
        }

        /**
         * Get an optional of the first element
         *
         * @return An {@link Optional} or type {@link T}
         */
        public Optional<T> first() {
            return await().getObjects().stream().findFirst();
        }

        /**
         * Call to wait 60 seconds to allow the subscriber to finish processing.
         *
         * @return This {@link ObjectSubscriber} instance
         */
        public ObjectSubscriber<T> await() {
            return await(60, TimeUnit.SECONDS);
        }

        /**
         * Call to wait to allow the subscriber to finish processing.
         *
         * @param timeout The amount of time to wait
         * @param unit    The unit of time to wait
         * @return This {@link ObjectSubscriber} instance
         */
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

        /**
         * An {@link OperationSubscriber OperationSubscriber} that processes a {@link Consumer} on each entry
         *
         * @param consumer A consumer to apply to each entry
         */
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

        /**
         * A {@link ConsumerSubscriber ConsumerSubscriber} that logs each entry.
         *
         * @param log A {@link Logger}
         */
        public LoggerSubscriber(Logger log) {
            super(t -> log.info(t.toString()));
        }

        /**
         * A {@link ConsumerSubscriber ConsumerSubscriber} that logs each entry.
         *
         * @param clazz An object class
         */
        public LoggerSubscriber(Class<?> clazz) {
            this(LoggerFactory.getLogger(clazz));
        }
    }

    public static class MappingSubscriber<T> extends OperationSubscriber<T> {

        /**
         * Returns a stream consisting of the results of applying the given
         * function to the objects from the publisher.
         *
         * @param mapper The mapping function to apply to a value, if present
         * @param <R>    The kind of element in the new list
         * @return The stream of new object
         */
        public <R> Stream<R> map(Function<? super T, R> mapper) {
            return await().getObjects().stream().map(mapper);
        }

        /**
         * Returns a stream consisting of the results of applying the given
         * function to the objects from the publisher.
         *
         * @param timeout The amount of time to wait
         * @param unit    The unit of time to wait
         * @param mapper  The mapping function to apply to a value, if present
         * @param <R>     The kind of element in the new list
         * @return The stream of new object
         */
        public <R> Stream<R> map(final long timeout, TimeUnit unit, Function<? super T, R> mapper) {
            return await(timeout, unit).getObjects().stream().map(mapper);
        }

        /**
         * Returns a list of consisting of the results of applying the given
         * function to the objects from the publisher.
         *
         * @param mapper The mapping function to apply to a value, if present
         * @param <R>    The kind of element in the new list
         * @return A list of objects
         */
        public <R> List<R> mapToList(Function<? super T, R> mapper) {
            return map(mapper).toList();
        }

        /**
         * Returns a list of consisting of the results of applying the given
         * function to the objects from the publisher.
         *
         * @param timeout The amount of time to wait
         * @param unit    The unit of time to wait
         * @param mapper  The mapping function to apply to a value, if present
         * @param <R>     The kind of element in the new list
         * @return A list of objects
         */
        public <R> List<R> mapToList(final long timeout, TimeUnit unit, Function<? super T, R> mapper) {
            return map(timeout, unit, mapper).toList();
        }
    }
}
