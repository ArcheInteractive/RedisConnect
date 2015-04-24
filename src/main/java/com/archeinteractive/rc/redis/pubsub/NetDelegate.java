package com.archeinteractive.rc.redis.pubsub;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import redis.clients.jedis.JedisPubSub;

public class NetDelegate extends JedisPubSub implements Runnable {
    private static final Gson gson = new Gson();
    private NetHandler handler;

    public NetDelegate(NetHandler handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(String channel, String data) {
        if (handler.channels.contains(channel) == false) {
            return;
        }

        NetTask task;
        try {
            task = gson.fromJson(data, NetTask.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return;
        }

        if (handler.handleMessage(task) == false) {
            handler.logger.warning("Failed to handle redis message!");
        }
    }

    public void onPMessage(String s, String s2, String s3) {}

    public void onSubscribe(String s, int i) {}

    public void onUnsubscribe(String s, int i) {}

    public void onPUnsubscribe(String s, int i) {}

    public void onPSubscribe(String s, int i) {}

    public void run() {}
}
