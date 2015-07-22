package com.archeinteractive.rc.redis;

public class RedisSettings {
    private String host = "127.0.0.1";
    private int port = 6379;
    private String password = "";
    private int maxConnections = 10000;

    public RedisSettings() {}

    public RedisSettings(String host, int port, String password, int maxConnections) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.maxConnections = maxConnections;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getPassword() {
        return password;
    }

    public int getMaxConnections() {
        return maxConnections;
    }
}
