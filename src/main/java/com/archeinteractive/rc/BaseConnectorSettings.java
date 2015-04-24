package com.archeinteractive.rc;

import com.archeinteractive.rc.redis.RediSettings;
import com.archeinteractive.rc.utils.JsonConfig;

public class BaseConnectorSettings extends JsonConfig {
    private RediSettings redis = new RediSettings("localhost", 6379, null, -1);

    public RediSettings getRedis() {
        return redis;
    }
}
