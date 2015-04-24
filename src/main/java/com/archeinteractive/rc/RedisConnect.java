package com.archeinteractive.rc;

import com.archeinteractive.rc.redis.RedisHandler;

public class RedisConnect {
    private static Connector connector;

    public static void setConnector(Connector connector) {
        RedisConnect.connector = connector;
    }

    public static RedisHandler getRedisHandler() {
        return connector != null ? connector.getRedis() : null;
    }
}
