package com.archeinteractive.rc.redis.queue;

import com.archeinteractive.rc.redis.RedisHandler;

import java.util.function.Consumer;

public class ChannelSubscribe extends Queue {
    private String channel;
    private Consumer<String> consumer;
    private RedisHandler handler;

    public ChannelSubscribe(String channel, Consumer<String> consumer, RedisHandler handler) {
        this.channel = channel;
        this.consumer = consumer;
        this.handler = handler;
    }

    @Override
    public String process() {
        consumer.accept(channel);
        return "Channel \"" + channel + "\" registered!";
    }

    @Override
    public boolean conditionsMet() {
        return handler.isConnected();
    }
}
