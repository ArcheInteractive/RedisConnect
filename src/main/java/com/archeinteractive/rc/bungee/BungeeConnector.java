package com.archeinteractive.rc.bungee;

import com.archeinteractive.rc.BaseConnectorSettings;
import com.archeinteractive.rc.Connector;
import com.archeinteractive.rc.RedisConnect;
import com.archeinteractive.rc.bungee.server.DynamicRegistrationModule;
import com.archeinteractive.rc.redis.RedisHandler;
import com.archeinteractive.rc.utils.JsonConfig;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

public class BungeeConnector extends Plugin implements Connector {
    private static BungeeConnector instance;

    private BungeeConnectorSettings connectorSettings;
    private RedisHandler<Plugin> redis;
    private DynamicRegistrationModule dynamicRegistrationModule;

    public void onEnable() {
        instance = this;
        RedisConnect.setConnector(instance);

        loadConfig();
        redis = init("redis.json");
        dynamicRegistrationModule = new DynamicRegistrationModule(this);
    }

    private RedisHandler init(String file) {
        return new RedisHandler<>(getLogger(), connectorSettings.getRedis(), this, ProxyServer.getInstance().getScheduler()::runAsync);
    }

    public void onDisable() {
        redis.disable();
    }

    private BaseConnectorSettings loadConfig() {
        if (!getDataFolder().exists()) {
            getLogger().info("Config folder not found! Creating...");
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "settings.json");
        connectorSettings = JsonConfig.load(file, BungeeConnectorSettings.class);
        connectorSettings.save(file);

        return connectorSettings;
    }

    public RedisHandler getRedis() {
        return redis;
    }

    @Override
    public BungeeConnectorSettings getSettings() {
        return connectorSettings;
    }

    public DynamicRegistrationModule getDynamicRegistrationModule() {
        return dynamicRegistrationModule;
    }

    public static BungeeConnector getInstance() {
        return instance;
    }
}
