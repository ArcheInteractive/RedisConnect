package com.archeinteractive.rc.bungee.server;

import com.archeinteractive.rc.redis.pubsub.NetTaskSubscribe;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import com.archeinteractive.rc.bungee.BungeeConnector;

import java.util.HashMap;

public class BaseReceiver {
    public BaseReceiver(BungeeConnector plugin) {
        plugin.getRedis().registerChannel("send");
    }

    @NetTaskSubscribe(args = {"player", "server"}, name = "send")
    public void onSend(HashMap<String, Object> args) {
        Object p = args.get("player");
        Object s = args.get("server");

        if (!(p instanceof String) || !(s instanceof String)) {
            return;
        }

        String player = (String) p;
        String server = (String) s;
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(player);

        if (player == null || proxiedPlayer == null) {
            return;
        }

        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);

        if (serverInfo == null) {
            proxiedPlayer.sendMessage(ChatColor.RED + "This server is offline, please try again later!");
            return;
        }

        proxiedPlayer.connect(serverInfo);
    }
}
