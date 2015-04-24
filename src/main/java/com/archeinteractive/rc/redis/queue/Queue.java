package com.archeinteractive.rc.redis.queue;

public abstract class Queue {
    private String message = "";

    public abstract String process();

    public abstract boolean conditionsMet();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
