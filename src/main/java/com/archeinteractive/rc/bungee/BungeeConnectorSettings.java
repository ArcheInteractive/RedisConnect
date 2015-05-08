package com.archeinteractive.rc.bungee;

import com.archeinteractive.rc.BaseConnectorSettings;

public class BungeeConnectorSettings extends BaseConnectorSettings {
    private int heartbeatExpire = 30;

    public int getHeartbeatExpire() {
        return heartbeatExpire;
    }
}
