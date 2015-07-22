package com.archeinteractive.rc;

import com.archeinteractive.rc.redis.RedisSettings;
import com.archeinteractive.rc.utils.JsonConfig;

public class BaseConnectorSettings extends JsonConfig {
    private RedisSettings redis = new RedisSettings("localhost", 6379, null, -1);
    private boolean debug = false;

    public RedisSettings getRedis() {
        return redis;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getName() {
        return null;
    }
}
