package com.archeinteractive.rc.redis.queue;

import com.archeinteractive.rc.redis.RedisHandler;

import java.util.function.Consumer;

public class RegisterTask extends Queue {
    private Object o;
    private Consumer<Object> consumer;
    private RedisHandler handler;

    public RegisterTask(Object o, Consumer<Object> consumer, RedisHandler handler) {
        this.o = o;
        this.consumer = consumer;
        this.handler = handler;
    }

    @Override
    public String process() {
        consumer.accept(o);
        return "Task \"" + o.getClass().getName() + "\" registered!";
    }

    @Override
    public boolean conditionsMet() {
        return handler.isConnected();
    }
}
