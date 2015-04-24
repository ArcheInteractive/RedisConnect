package com.archeinteractive.rc.bungee.server;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import com.archeinteractive.rc.bungee.BungeeConnector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DynamicRegistrationModule {
    private static DynamicRegistrationModule instance;
    protected ServerHeartbeatHandler serverHeartbeatHandler;

    public DynamicRegistrationModule(BungeeConnector plugin) {
        instance = this;
        this.serverHeartbeatHandler = new ServerHeartbeatHandler();
        register(plugin);
    }

    private void register(BungeeConnector plugin) {
        plugin.getRedis().registerChannel("heartbeat");
        plugin.getRedis().registerTask(new ServerHandler());
        plugin.getRedis().registerTask(new BaseReceiver(plugin));
    }

    public ConcurrentMap<String, ServerInfo> getServerInfo() {
        return new ConcurrentHashMap<>(ProxyServer.getInstance().getServers());
    }

    public static DynamicRegistrationModule getInstance() {
        return instance;
    }

    public ServerHeartbeatHandler getServerHeartbeatHandler() {
        return serverHeartbeatHandler;
    }
}
