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

    private BukkitConnectorSettings dynamicSettings;
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
        return new RedisHandler<>(getLogger(), dynamicSettings.getRedis(), this, Bukkit.getScheduler()::runTaskAsynchronously);
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
        dynamicSettings = JsonConfig.load(file, BukkitConnectorSettings.class);

        if (!file.exists()) {
            dynamicSettings.save(file);
        }

        return dynamicSettings;
    }

    public RedisHandler getRedis() {
        return redis;
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
                        .withArg("name", "" + dynamicSettings.getName())
                        .withArg("ip", Bukkit.getIp())
                        .withArg("port", Bukkit.getPort())
                        .withArg("players", players)
                        .send("heartbeat");
            }
        }, 10 * 20, 10 * 20);
    }

    public static BukkitConnector getInstance() {
        return instance;
    }

    public BukkitConnectorSettings getDynamicSettings() {
        return dynamicSettings;
    }
}
