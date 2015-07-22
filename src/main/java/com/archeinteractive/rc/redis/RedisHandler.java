package com.archeinteractive.rc.redis;

import com.archeinteractive.rc.RedisConnect;
import com.archeinteractive.rc.redis.pubsub.NetHandler;
import com.archeinteractive.rc.redis.pubsub.NetTask;
import com.archeinteractive.rc.redis.queue.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.logging.Logger;

public class RedisHandler<T> {
    private static RedisHandler instance;
    private Logger logger;
    private RediSettings settings;
    private T plugin;
    private BiConsumer<? super T, Runnable> task;
    private JedisPool pool;
    private NetHandler dispatch;
    private boolean connected = false;
    private CopyOnWriteArrayList<Queue> pending = new CopyOnWriteArrayList<>();
    private QueueConsumer consumer;

    public RedisHandler(Logger logger, RediSettings settings, T plugin, BiConsumer<? super T, Runnable> task) {
        this.logger = logger;
        this.settings = settings;
        this.plugin = plugin;
        this.task = task;
        this.consumer = new QueueConsumer(logger, pending);
        RedisHandler.instance = this;
        init();
    }
    
    public static RedisHandler getInstance() {
        return instance;
    }

    public void init() {
        RedisConnect.debug("Scheduling connection to Redis server.");
        task.accept(plugin, () -> {
            logger.info("Connecting to Redis server...");
            pool = getJedisPool();
            Jedis rsc = null;

            try {
                rsc = getJedis();

                if (rsc == null) {
                    return;
                }

                rsc.exists(String.valueOf(System.currentTimeMillis()));
                logger.info("Connected to Redis.");
                connected = true;
            } catch (JedisConnectionException ex) {
                if (rsc != null) {
                    returnRedis(rsc);
                }

                pool.destroy();
                pool = null;
                rsc = null;
            } finally {
                if (rsc != null && pool != null) {
                    returnRedis(rsc);
                }
            }
        });

        dispatch = new NetHandler(logger, plugin, task);
        task.accept(plugin, consumer);
    }

    private JedisPool getJedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxActive(settings.getMaxConnections());

        if (settings.getPassword() == null || settings.getPassword().equals("")) {
            return RedisHandler.this.pool = new JedisPool(config, settings.getHost(), settings.getPort(), 0);
        } else {
            return RedisHandler.this.pool = new JedisPool(config, settings.getHost(), settings.getPort(), 0, settings.getPassword());
        }
    }

    public void disable() {
        if (pool != null) {
            pool.destroy();
        }
    }

    public Jedis getJedis() {
        return pool == null ? null : pool.getResource();
    }

    public void returnRedis(Jedis jedis) {
        if (pool == null) {
            return;
        }

        try {
            pool.returnResource(jedis);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(jedis);
        }
    }

    public void registerChannel(String channel) {
        if (connected) {
            registerChannel0(channel);
        } else {
            pending.add(new ChannelSubscribe(channel, this::registerChannel0, this));
        }
    }

    private void registerChannel0(String channel) {
        task.accept(plugin, () -> {
            Jedis jedis = RedisHandler.this.getJedis();

            if (jedis == null) {
                return;
            }

            RedisHandler.this.dispatch.addChannel(channel);
            jedis.subscribe(RedisHandler.this.dispatch.getDelegate(), channel);
            returnRedis(jedis);
        });
    }

    public void queueNetTaskSend(NetTask netTask) {
        pending.add(new SendTask(netTask));
    }

    public void registerTask(Object o) {
        if (connected) {
            registerTask0(o);
        } else {
            pending.add(new RegisterTask(o, this::registerTask0, this));
        }
    }

    private void registerTask0(Object o) {
        dispatch.registerTasks(o);
    }

    public boolean isConnected() {
        return connected;
    }

    public NetHandler getDispatch() {
        return dispatch;
    }
}
