package io.Sriptirc_wp_1258.gost;

import io.Sriptirc_wp_1258.gost.commands.GostCommand;
import io.Sriptirc_wp_1258.gost.commands.GostAdminCommand;
import io.Sriptirc_wp_1258.gost.commands.DivineGuardianCommand;
import io.Sriptirc_wp_1258.gost.commands.GhostParticleCommand;
import io.Sriptirc_wp_1258.gost.listeners.*;
import io.Sriptirc_wp_1258.gost.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Gost extends JavaPlugin {
    
    private static Gost instance;
    
    // 管理器
    private ConfigManager configManager;
    private GameManager gameManager;
    private PlayerManager playerManager;
    private ItemManager itemManager;
    private TeamManager teamManager;
    private AreaManager areaManager;
    private SelectionManager selectionManager;
    private EconomyManager economyManager;
    private ActionBarManager actionBarManager;
    private BotManager botManager;
    private LanguageManager languageManager;
    private ItemSpawnManager itemSpawnManager;
    private SecondChanceListener secondChanceListener;
    private DarkEffectManager darkEffectManager;
    private HeartbeatManager heartbeatManager;
    private DivineGuardianManager divineGuardianManager;
    private GhostParticleManager ghostParticleManager;
    // private CurrencyManager currencyManager; // 货币系统已取消
    // private NpcManager npcManager; // NPC系统已取消
    // private SpectatorManager spectatorManager; // 观战系统已搁置
    
    @Override
    public void onEnable() {
        instance = this;
        
        // 初始化管理器
        configManager = new ConfigManager(this);
        economyManager = new EconomyManager(this);
        areaManager = new AreaManager(this);
        selectionManager = new SelectionManager(this);
        teamManager = new TeamManager(this);
        itemManager = new ItemManager(this);
        playerManager = new PlayerManager(this);
        gameManager = new GameManager(this);
        actionBarManager = new ActionBarManager(this);
        botManager = new BotManager(this);
        languageManager = new LanguageManager(this);
        itemSpawnManager = new ItemSpawnManager(this);
        secondChanceListener = new SecondChanceListener(this);
        darkEffectManager = new DarkEffectManager(this);
        heartbeatManager = new HeartbeatManager(this);
        divineGuardianManager = new DivineGuardianManager(this);
        ghostParticleManager = new GhostParticleManager(this);
        // currencyManager = new CurrencyManager(this); // 暂时取消货币系统
        // spectatorManager = new SpectatorManager(this); // 暂时搁置观战系统
        // npcManager = new NpcManager(this); // 取消NPC系统
        
        // 加载语言
        languageManager.loadLanguage();
        
        // 加载神圣守护配置
        divineGuardianManager.loadConfig();
        
        // 加载鬼玩家粒子效果配置
        ghostParticleManager.loadConfig();
        
        // 插件加载完成提示
        getLogger().info("==========================================");
        getLogger().info("Gost v2.1.2 已成功加载！");
        getLogger().info("✨ 新增功能：鬼玩家粒子效果系统");
        getLogger().info("👻 母体鬼：红色环绕粒子");
        getLogger().info("👻 普通鬼：绿色环绕粒子");
        getLogger().info("⚙️ 管理命令：/ghostparticle 或 /gp");
        getLogger().info("🔧 配置版本：14");
        getLogger().info("==========================================");
        
        // 注册命令
        getCommand("gost").setExecutor(new GostCommand(this));
        getCommand("gostadmin").setExecutor(new GostAdminCommand(this));
        getCommand("divineguardian").setExecutor(new DivineGuardianCommand(this));
        getCommand("divineguardian").setTabCompleter(new DivineGuardianCommand(this));
        getCommand("ghostparticle").setExecutor(new GhostParticleCommand(this));
        getCommand("ghostparticle").setTabCompleter(new GhostParticleCommand(this));
        
        // 注册监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new InfectionListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemListener(this), this);
        getServer().getPluginManager().registerEvents(new SelectionListener(this), this);
        getServer().getPluginManager().registerEvents(secondChanceListener, this);
        
        getLogger().info("==========================================");
        getLogger().info("Gost v2.1.3 已成功加载！");
        getLogger().info("✨ 新增功能：鬼玩家粒子效果系统");
        getLogger().info("🔧 修复：管理员和创造模式玩家效果免疫问题");
        getLogger().info("🎮 新增：创造模式自动切换为生存模式");
        getLogger().info("⚖️ 确保：所有玩家公平游戏环境");
        getLogger().info("作者: 来自太空的小头脑");
        getLogger().info("主页: https://space.bilibili.com/3493116665400113");
        getLogger().info("==========================================");
    }
    
    @Override
    public void onDisable() {
        if (gameManager.isGameRunning()) {
            gameManager.forceStopGame();
        }
        
        // 清理队伍
        teamManager.cleanup();
        
        // 清理神圣守护数据
        if (divineGuardianManager != null) {
            divineGuardianManager.cleanup();
        }
        
        // 清理鬼玩家粒子效果数据
        if (ghostParticleManager != null) {
            ghostParticleManager.cleanup();
        }
        
        getLogger().info("Gost 插件已禁用");
    }
    
    // 获取管理器实例
    public static Gost getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public ItemManager getItemManager() {
        return itemManager;
    }
    
    public TeamManager getTeamManager() {
        return teamManager;
    }
    
    public AreaManager getAreaManager() {
        return areaManager;
    }
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }
    
    public BotManager getBotManager() {
        return botManager;
    }
    
    public LanguageManager getLanguageManager() {
        return languageManager;
    }
    
    public ItemSpawnManager getItemSpawnManager() {
        return itemSpawnManager;
    }
    
    public SecondChanceListener getSecondChanceListener() {
        return secondChanceListener;
    }
    
    public DarkEffectManager getDarkEffectManager() {
        return darkEffectManager;
    }
    
    public HeartbeatManager getHeartbeatManager() {
        return heartbeatManager;
    }
    
    public DivineGuardianManager getDivineGuardianManager() {
        return divineGuardianManager;
    }
    
    public GhostParticleManager getGhostParticleManager() {
        return ghostParticleManager;
    }
    
    // public CurrencyManager getCurrencyManager() {
    //     return currencyManager;
    // }
    
    // public NpcManager getNpcManager() {
    //     return npcManager;
    // }
    
    // public SpectatorManager getSpectatorManager() {
    //     return spectatorManager;
    // }
}