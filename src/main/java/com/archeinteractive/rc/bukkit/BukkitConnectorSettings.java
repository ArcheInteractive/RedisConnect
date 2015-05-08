package com.archeinteractive.rc.bukkit;

import com.archeinteractive.rc.BaseConnectorSettings;

public class BukkitConnectorSettings extends BaseConnectorSettings {
    private String name = "server1";
    private int heartbeatInterval = 5;

    public String getName() {
        return name;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }
}
