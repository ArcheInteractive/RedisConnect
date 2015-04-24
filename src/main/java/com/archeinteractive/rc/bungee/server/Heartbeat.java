package com.archeinteractive.rc.bungee.server;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class Heartbeat {
    public Heartbeat(ServerInfo info, Long timeHeartbeat, List<String> players, boolean vipOnly) {
        this.info = info;
        this.timeHeartbeat = timeHeartbeat;
        this.players = players;
        this.vipOnly = vipOnly;
    }

    /**
     * The Server that sent the ping.
     */
    private ServerInfo info;

    /**
     * The time the heartbeat was sent.
     */
    private Long timeHeartbeat;

    /**
     * Players.
     */
    private List<String> players;

    /**
     * VIP Only
     */
    private boolean vipOnly;

    public ServerInfo getInfo() {
        return info;
    }

    public Long getTimeHeartbeat() {
        return timeHeartbeat;
    }

    public List<String> getPlayers() {
        return players;
    }

    public boolean isVipOnly() {
        return vipOnly;
    }

    @Override
    public String toString() {
        return "Heartbeat{" +
                "info=" + info +
                ", timeHeartbeat=" + timeHeartbeat +
                ", players=" + players +
                ", vipOnly=" + vipOnly +
                '}';
    }
}
