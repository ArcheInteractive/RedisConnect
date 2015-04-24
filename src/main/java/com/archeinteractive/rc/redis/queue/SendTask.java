package com.archeinteractive.rc.redis.queue;

import com.archeinteractive.rc.redis.pubsub.NetTask;

public class SendTask extends Queue {
    private NetTask task;

    public SendTask(NetTask task) {
        this.task = task;
    }

    @Override
    public String process() {
        task.send(task.getChannel(), task.getHandler());
        return "Task \"" + task.getTask() + "\" sent!";
    }

    @Override
    public boolean conditionsMet() {
        return (task.getHandler().isConnected() && task.getHandler().getDispatch().isTaskRegistered(task));
    }
}

