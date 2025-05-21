package cn.org.agatha.leaderboardLLM;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class LeaderboardLLM extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic

        // 加载配置文件
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // 检查配置文件中是否包含 APP_ID 和 dashscope key
        FileConfiguration config = getConfig();
        if (!config.isSet("app_id") || !config.isSet("dashscope_key")) {
            getLogger().warning("config.yml 中缺少 app_id 或 dashscope_key，请检查配置文件！");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}