package cn.org.agatha.leaderboardLLM;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;

public final class LeaderboardLLM extends JavaPlugin {

    private Map<String, Leaderboard> leaderboards = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic

        // 加载配置文件
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // 检查配置文件中是否包含 app_id 和 dashscope_key
        FileConfiguration config = getConfig();
        if (!config.isSet("app_id") || !config.isSet("dashscope_key")) {
            getLogger().warning("config.yml 中缺少 app_id 或 dashscope_key，请检查配置文件！");
        }

        // 注册命令处理器
        getCommand("ai").setExecutor(this::onCommand);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ai")) {
            if (args.length == 0) {
                sender.sendMessage("用法: /ai <enable|disable>");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "enable":
                    enableLeaderboard(sender);
                    return true;
                case "disable":
                    disableLeaderboard(sender);
                    return true;
                default:
                    sender.sendMessage("未知的子命令: " + args[0]);
                    return true;
            }
        }
        return false;
    }

    private void enableLeaderboard(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以启用排行榜！");
            return;
        }

        Player player = (Player) sender;
        String senderName = player.getName();
        if (leaderboards.containsKey(senderName)) {
            sender.sendMessage("你已经启用了排行榜！");
            return;
        }

        Leaderboard leaderboard = new Leaderboard(senderName);
        leaderboards.put(senderName, leaderboard);
        sender.sendMessage("排行榜已启用！");
        leaderboard.displayScoreboard(player); // 显示排行榜
    }

    private void disableLeaderboard(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以禁用排行榜！");
            return;
        }

        Player player = (Player) sender;
        String senderName = player.getName();
        if (!leaderboards.containsKey(senderName)) {
            sender.sendMessage("你还没有启用排行榜！");
            return;
        }

        Leaderboard leaderboard = leaderboards.remove(senderName);
        player.setScoreboard(player.getServer().getScoreboardManager().getNewScoreboard());
        sender.sendMessage("排行榜已禁用！");
    }

    // 简单的 Leaderboard 类
    private static class Leaderboard {
        private final String owner;
        private String[] rows; // 添加字符串数组来存储每一行的内容

        public Leaderboard(String owner) {
            this.owner = owner;
            this.rows = new String[0]; // 初始化为空数组
        }

        // 添加方法来添加行内容
        public void addRow(String row) {
            String[] newRows = new String[rows.length + 1];
            System.arraycopy(rows, 0, newRows, 0, rows.length);
            newRows[newRows.length - 1] = row;
            this.rows = newRows;
        }

        // 获取所有行内容
        public String[] getRows() {
            return rows;
        }

        // 显示排行榜
        public void displayScoreboard(Player player) {
            ScoreboardManager scoreboardManager = player.getServer().getScoreboardManager();
            if (scoreboardManager == null) {
                return;
            }

            Scoreboard scoreboard = scoreboardManager.getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("leaderboard", "dummy", "输入 /ai [问题] 向Agatha机器人提问。");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            for (int i = 0; i < rows.length; i++) {
                Score score = objective.getScore(rows[i]);
                score.setScore(rows.length - i); // 从高到低排序
            }

            player.setScoreboard(scoreboard);
        }
    }
}