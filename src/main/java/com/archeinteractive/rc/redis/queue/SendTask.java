package com.archeinteractive.rc.redis.queue;

import com.archeinteractive.rc.RedisConnect;
import com.archeinteractive.rc.redis.pubsub.NetTask;

public class SendTask extends Queue {
    private NetTask task;

    public SendTask(NetTask task) {
        this.task = task;
    }

    @Override
    public String process() {
        task.send(task.getChannel());
        return "Task \"" + task.getTask() + "\" sent!";
    }

    @Override
    public boolean conditionsMet() {
        return (RedisConnect.getRedis().isConnected() && RedisConnect.getRedis().getDispatch().isTaskRegistered(task));
    }
}

