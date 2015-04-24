package com.archeinteractive.rc.bungee.server;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import com.archeinteractive.rc.redis.pubsub.NetTaskSubscribe;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;

public class ServerHandler {
    @NetTaskSubscribe(name = "heartbeat", args = {"name", "ip", "port", "players", "vipOnly"})
    public void onHeartbeat(HashMap<String, Object> args) {
        Object i = args.get("ip");
        Object n = args.get("name");
        Object p = args.get("port");
        Object pl = args.get("players");
        Object v = args.get("vipOnly");

        if ((i instanceof String) == false
                || (n instanceof String) == false
                || (p instanceof Number) == false
                || (pl instanceof List) == false
                || (v instanceof Boolean) == false) {
            return;
        }

        String ip = (String) i;
        String name = (String) n;
        Number port = (Number) p;
        List list = (List) pl;
        Boolean vipOnly = (Boolean) v;

        InetSocketAddress socketAddress = new InetSocketAddress(ip, port.intValue());
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(name);
        if (serverInfo != null) {
            if (serverInfo.getAddress().equals(socketAddress)) {
                DynamicRegistrationModule.getInstance().serverHeartbeatHandler.heartbeatReceived(serverInfo, list, vipOnly);
                return;
            }
            disconnectAll(serverInfo);
        }
        ServerInfo info = ProxyServer.getInstance().constructServerInfo(name, socketAddress, "", false);
        ProxyServer.getInstance().getServers().put(name, info);
        DynamicRegistrationModule.getInstance().serverHeartbeatHandler.heartbeatReceived(info, list, vipOnly);
    }

    @NetTaskSubscribe(name = "disconnect", args = {"name"})
    public void onDisconnect(HashMap<String, Object> args) {
        Object n = args.get("name");
        if ((n instanceof String) == false) {
            return;
        }
        String name = (String) n;
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(name);
        if (serverInfo == null) {
            return;
        }
        disconnectAll(serverInfo);
        ProxyServer.getInstance().getServers().remove(name);
    }

    /**
     * Disconnect all players from the server.
     *
     * @param info The server to disconnect players from.
     */
    public static void disconnectAll(ServerInfo info) {
        for (ProxiedPlayer player : info.getPlayers()) {
            player.disconnect("The server is currently unavailable, please try again soon!");
        }
    }
}
