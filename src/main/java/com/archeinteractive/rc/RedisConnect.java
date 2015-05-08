package com.archeinteractive.rc;

import com.archeinteractive.rc.redis.RedisHandler;

public class RedisConnect {
    private static Connector connector;

    public static void setConnector(Connector connector) {
        RedisConnect.connector = connector;
    }

    public static RedisHandler getRedis() {
        return connector != null ? connector.getRedis() : null;
    }

    public static void debug(String ... lines) {
        if (connector.getSettings().isDebug()) {
            for (String line : lines) {
                connector.getLogger().info(line);
            }
        }
    }
}
