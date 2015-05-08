package com.archeinteractive.rc.bungee.server;

import com.archeinteractive.rc.RedisConnect;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import com.archeinteractive.rc.bungee.BungeeConnector;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class ServerHeartbeatHandler implements Runnable {
    /**
     * Stores previous heartbeats.
     */
    private Cache<ServerInfo, Heartbeat> heartbeats = CacheBuilder.newBuilder().expireAfterWrite(BungeeConnector.getInstance().getSettings().getHeartbeatExpire(), TimeUnit.SECONDS).build();

    /**
     * Stores the current task.
     */
    private ScheduledTask task;

    /**
     * Creates a new handler, and schedules it in the BungeeCord scheduler.
     */
    public ServerHeartbeatHandler() {
        schedule();
    }

    /**
     * Call this method when a server sends a heartbeat.
     *
     * @param info The server that a heartbeat was received for.
     */
    public void heartbeatReceived(ServerInfo info, List playerList) {
        ArrayList<String> players = new ArrayList<>();
        for (Object p : playerList) {
            if ((p instanceof String) == false) {
                continue;
            }
            players.add((String) p);
        }
        this.heartbeats.put(info, new Heartbeat(info, Calendar.getInstance().getTimeInMillis(), players));
    }

    @Override
    public void run() {
        ConcurrentMap<String, ServerInfo> allServerInfo = DynamicRegistrationModule.getInstance().getServerInfo();
        for (ServerInfo info : allServerInfo.values()) {
            Heartbeat heartbeat = heartbeats.getIfPresent(info);
            if (heartbeat == null) {
                ProxyServer.getInstance().getServers().remove(info.getName());
                ServerHandler.disconnectAll(info);
                this.heartbeats.invalidate(info);
                RedisConnect.debug("Heartbeat was not found for: " + info.getName() + ". Invalidating server.");
            }
        }
        schedule();
    }

    /**
     * Reschedule this in the scheduler for execution.
     */
    public void schedule() {
        task = ProxyServer.getInstance().getScheduler().schedule(BungeeConnector.getInstance(), this, 30, TimeUnit.SECONDS);
    }

    /**
     * Get players online
     */
    public static Integer getPlayersOnline() {
        List<Heartbeat> heartbeats = new ArrayList<>(DynamicRegistrationModule.getInstance().serverHeartbeatHandler.heartbeats.asMap().values());
        Integer count = 0;
        for (Heartbeat heartbeat : heartbeats) {
            count += heartbeat.getPlayers().size();
        }
        return count;
    }

    public void shutdown() {
        task.cancel();
    }
}
