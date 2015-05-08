package com.archeinteractive.rc.bukkit;

import com.archeinteractive.rc.Connector;
import com.archeinteractive.rc.RedisConnect;
import com.archeinteractive.rc.redis.pubsub.NetTask;
import com.archeinteractive.rc.redis.RedisHandler;
import com.archeinteractive.rc.utils.JsonConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BukkitConnector extends JavaPlugin implements Connector {
    private static BukkitConnector instance;

    private BukkitConnectorSettings connectorSettings;
    private RedisHandler<JavaPlugin> redis;
    private BukkitTask task;

    public void onEnable() {
        instance = this;
        RedisConnect.setConnector(instance);

        loadConfig();
        redis = init("redis.json");
        register();
    }

    private RedisHandler init(String file) {
        return new RedisHandler<>(getLogger(), connectorSettings.getRedis(), this, Bukkit.getScheduler()::runTaskAsynchronously);
    }

    public void onDisable() {
        redis.disable();
    }

    private BukkitConnectorSettings loadConfig() {
        if (!getDataFolder().exists()) {
            getLogger().info("Config folder not found! Creating...");
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "settings.json");
        connectorSettings = JsonConfig.load(file, BukkitConnectorSettings.class);
        connectorSettings.save(file);

        return connectorSettings;
    }

    public RedisHandler getRedis() {
        return redis;
    }

    @Override
    public BukkitConnectorSettings getSettings() {
        return connectorSettings;
    }

    @SuppressWarnings("deprecation")
    private void register() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new BukkitRunnable() {
            @Override
            public void run() {
                List<String> players = new ArrayList<String>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    players.add(player.getName());
                }

                NetTask.withName("heartbeat")
                        .withArg("name", "" + connectorSettings.getName())
                        .withArg("ip", Bukkit.getIp())
                        .withArg("port", Bukkit.getPort())
                        .withArg("players", players)
                        .send("heartbeat");
            }
        }, connectorSettings.getHeartbeatInterval() * 20, connectorSettings.getHeartbeatInterval() * 20);
    }

    public static BukkitConnector getInstance() {
        return instance;
    }
}
