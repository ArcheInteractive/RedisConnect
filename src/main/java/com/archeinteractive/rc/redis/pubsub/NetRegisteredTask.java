package com.archeinteractive.rc.redis.pubsub;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetRegisteredTask {
    private List<String> args;
    private String name;

    private Map<Object, Method> handlers;

    public NetRegisteredTask(String name, List<String> args, Map<Object, Method> handlers) {
        this.args = args;
        this.name = name;
        this.handlers = handlers;
    }

    public void registerHandler(Object o, Method m) {
        this.handlers.put(o, m);
    }

    public void callHandlers(HashMap<String, Object> data) throws IllegalArgumentException {
        for (Map.Entry<Object, Method> objectMethodEntry : this.handlers.entrySet()) {
            try {
                objectMethodEntry.getValue().invoke(objectMethodEntry.getKey(), data);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // Something went wrong? Null data perhaps.
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Incorrect arguments while invoking handler", e);
            }
        }
    }
}
