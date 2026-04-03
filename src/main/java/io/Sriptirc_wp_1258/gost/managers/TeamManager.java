package io.Sriptirc_wp_1258.gost.managers;

import io.Sriptirc_wp_1258.gost.Gost;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamManager {
    
    private final Gost plugin;
    private Scoreboard scoreboard;
    private final Map<String, Team> teams = new HashMap<>();
    
    public TeamManager(Gost plugin) {
        this.plugin = plugin;
        initializeTeams();
    }
    
    private void initializeTeams() {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        
        // 清理旧的队伍
        cleanup();
        
        // 创建人类队伍（白色）
        Team humanTeam = scoreboard.getTeam("gost_human");
        if (humanTeam == null) {
            humanTeam = scoreboard.registerNewTeam("gost_human");
        }
        humanTeam.setColor(ChatColor.WHITE);
        humanTeam.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getLanguageManager().getMessage("team.human").replaceAll("§[0-9a-fk-or]", "")));
        humanTeam.setPrefix(ChatColor.translateAlternateColorCodes('&', plugin.getLanguageManager().getMessage("team.team_human_prefix")));
        humanTeam.setAllowFriendlyFire(false);
        humanTeam.setCanSeeFriendlyInvisibles(true);
        teams.put("human", humanTeam);
        
        // 创建鬼队伍（红色）
        Team ghostTeam = scoreboard.getTeam("gost_ghost");
        if (ghostTeam == null) {
            ghostTeam = scoreboard.registerNewTeam("gost_ghost");
        }
        ghostTeam.setColor(ChatColor.RED);
        ghostTeam.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getLanguageManager().getMessage("team.ghost").replaceAll("§[0-9a-fk-or]", "")));
        ghostTeam.setPrefix(ChatColor.translateAlternateColorCodes('&', plugin.getLanguageManager().getMessage("team.team_ghost_prefix")));
        ghostTeam.setAllowFriendlyFire(false);
        ghostTeam.setCanSeeFriendlyInvisibles(true);
        teams.put("ghost", ghostTeam);
        
        // 创建母体鬼队伍（深红色）
        Team motherGhostTeam = scoreboard.getTeam("gost_mother_ghost");
        if (motherGhostTeam == null) {
            motherGhostTeam = scoreboard.registerNewTeam("gost_mother_ghost");
        }
        motherGhostTeam.setColor(ChatColor.DARK_RED);
        motherGhostTeam.setDisplayName(ChatColor.translateAlternateColorCodes('&', plugin.getLanguageManager().getMessage("team.ghost_mother").replaceAll("§[0-9a-fk-or]", "")));
        motherGhostTeam.setPrefix(ChatColor.translateAlternateColorCodes('&', plugin.getLanguageManager().getMessage("team.team_mother_ghost_prefix")));
        motherGhostTeam.setAllowFriendlyFire(false);
        motherGhostTeam.setCanSeeFriendlyInvisibles(true);
        teams.put("mother_ghost", motherGhostTeam);
        
        plugin.getLogger().info(ChatColor.translateAlternateColorCodes('&', plugin.getLanguageManager().getMessage("team.team_init_done")));
    }
    
    // 设置玩家队伍
    public void setPlayerTeam(Player player, PlayerManager.PlayerRole role) {
        removeFromAllTeams(player);
        
        switch (role) {
            case HUMAN:
                teams.get("human").addEntry(player.getName());
                player.setDisplayName(getTeamColor(role) + player.getName());
                player.setPlayerListName(getTeamColor(role) + player.getName());
                break;
            case GHOST_MOTHER:
                teams.get("mother_ghost").addEntry(player.getName());
                player.setDisplayName(getTeamColor(role) + player.getName());
                player.setPlayerListName(getTeamColor(role) + player.getName());
                break;
            case GHOST_NORMAL:
                teams.get("ghost").addEntry(player.getName());
                player.setDisplayName(getTeamColor(role) + player.getName());
                player.setPlayerListName(getTeamColor(role) + player.getName());
                break;
        }
        
        // 更新玩家计分板
        player.setScoreboard(scoreboard);
    }
    
    // 从所有队伍中移除玩家
    public void removeFromAllTeams(Player player) {
        for (Team team : teams.values()) {
            team.removeEntry(player.getName());
        }
    }
    
    // 清理所有队伍
    public void cleanup() {
        for (Team team : teams.values()) {
            team.unregister();
        }
        teams.clear();
        
        // 清理可能残留的队伍
        Team[] existingTeams = scoreboard.getTeams().toArray(new Team[0]);
        for (Team team : existingTeams) {
            if (team.getName().startsWith("gost_")) {
                team.unregister();
            }
        }
    }
    
    // 获取队伍颜色
    public ChatColor getTeamColor(PlayerManager.PlayerRole role) {
        switch (role) {
            case HUMAN:
                return ChatColor.WHITE;
            case GHOST_MOTHER:
                return ChatColor.DARK_RED;
            case GHOST_NORMAL:
                return ChatColor.RED;
            default:
                return ChatColor.GRAY;
        }
    }
    
    // 获取队伍名称
    public String getTeamName(PlayerManager.PlayerRole role) {
        switch (role) {
            case HUMAN:
                return "人类";
            case GHOST_MOTHER:
                return "母体鬼";
            case GHOST_NORMAL:
                return "鬼";
            default:
                return "未知";
        }
    }
}