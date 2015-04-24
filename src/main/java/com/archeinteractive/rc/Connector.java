package com.archeinteractive.rc;

import com.archeinteractive.rc.redis.RedisHandler;

import java.util.logging.Logger;

public interface Connector {
    public RedisHandler getRedis();

    public BaseConnectorSettings getBaseSettings();

    public Logger getLogger();
}
