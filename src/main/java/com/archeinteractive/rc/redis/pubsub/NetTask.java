package com.archeinteractive.rc.redis.pubsub;

import com.archeinteractive.rc.RedisConnect;
import com.google.gson.Gson;
import com.archeinteractive.rc.redis.RedisHandler;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class NetTask {
    private static final Gson gson = new Gson();
    private Map<String, Object> data;
    private String task;
    private String channel;

    private NetTask(String name) {
        this.task = name;
        this.data = new HashMap<>();
    }

    public static NetTask withName(String name) {
        return new NetTask(name);
    }

    public NetTask withArg(String arg, Object o) {
        this.data.put(arg, o);
        return this;
    }

    public boolean send(String channel) {
        this.channel = channel;
        RedisHandler handler = RedisConnect.getRedis();

        if (handler.isConnected()) {
            Jedis jedis = handler.getJedis();
            jedis.publish(channel, gson.toJson(this));
            handler.returnRedis(jedis);
        } else {
            handler.queueNetTaskSend(this);
        }

        return true;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getChannel() {
        return channel;
    }
}
