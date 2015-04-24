package com.archeinteractive.rc;

import com.archeinteractive.rc.redis.RedisHandler;

public interface Connector {
    public RedisHandler getRedis();
}
