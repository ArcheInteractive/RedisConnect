package com.archeinteractive.rc.redis.pubsub;

import com.archeinteractive.rc.RedisConnect;
import com.google.gson.JsonParseException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class NetHandler<T> {
    protected Logger logger;
    protected NetDelegate delegate;
    private Map<String, NetTaskSubscribe> tasks;
    private Map<NetTaskSubscribe, NetRegisteredTask> handlers;
    protected CopyOnWriteArrayList<String> channels;

    public NetHandler(Logger logger, T plugin, BiConsumer<? super T, Runnable> task) {
        this.logger = logger;
        this.delegate = new NetDelegate(this);
        this.tasks = new HashMap<>();
        this.handlers = new HashMap<>();
        this.channels = new CopyOnWriteArrayList<>();

        task.accept(plugin, delegate);
    }

    public NetHandler(Logger logger, ExecutorService service) {
        this.logger = logger;
        this.delegate = new NetDelegate(this);
        this.tasks = new HashMap<>();
        this.handlers = new HashMap<>();
        this.channels = new CopyOnWriteArrayList<>();

        service.execute(delegate);
    }

    public void registerTasks(Object o) {
        for (Method method : o.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(NetTaskSubscribe.class)) {
                continue;
            }

            if (method.getParameterTypes().length != 1) {
                continue;
            }

            if (method.getParameterTypes()[0].equals(HashMap.class) == false) {
                continue;
            }

            NetTaskSubscribe task = method.getAnnotation(NetTaskSubscribe.class);
            NetRegisteredTask handler;
            if (this.handlers.containsKey(task.name().toLowerCase()) == false) {
                handler = new NetRegisteredTask(task.name(),
                        Arrays.asList(task.args()),
                        new HashMap<Object, Method>());
                this.tasks.put(task.name().toLowerCase(), task);
                this.handlers.put(task, handler);
            } else {
                logger.info("Task: " + task.name() + " already registered");
                return;
            }

            logger.info("Registered Task: " + task.name());
            handler.registerHandler(o, method);
        }
    }

    public boolean handleMessage(NetTask task) {
        try {
            NetTaskSubscribe netTask = this.tasks.get(task.getTask().toLowerCase());
            if (netTask == null) {
                RedisConnect.debug("UNKNOWN NetTask - Could not find task for " + task.getTask());
                return true;
            }

            if (task.getData().keySet().containsAll(Arrays.asList(netTask.args())) == false) {
                RedisConnect.debug("Missing Arguments for NetTask: " + netTask.name() + " on channel: " + task.getChannel());
                return false;
            }

            NetRegisteredTask registeredTask = this.handlers.get(netTask);
            registeredTask.callHandlers(new HashMap<>(task.getData()));
        } catch (JsonParseException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            logger.info(e.getMessage());
        }

        return true;
    }

    public NetDelegate getDelegate() {
        return delegate;
    }

    public void addChannel(String channel) {
        channels.add(channel);
    }

    public boolean isTaskRegistered(NetTask t) {
        return tasks.containsKey(t.getTask().toLowerCase());
    }
}
