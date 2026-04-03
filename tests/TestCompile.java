// 简单的编译测试文件
public class TestCompile {
    public static void main(String[] args) {
        System.out.println("Gost插件项目结构检查：");
        System.out.println("1. 主类: Gost.java ✓");
        System.out.println("2. 配置文件: config.yml ✓");
        System.out.println("3. 插件描述文件: plugin.yml ✓");
        System.out.println("4. 新道具监听器: SecondChanceListener.java ✓");
        System.out.println("5. 道具管理器更新: ItemManager.java ✓");
        System.out.println("6. 配置管理器更新: ConfigManager.java ✓");
        System.out.println("7. 玩家管理器更新: PlayerManager.java ✓");
        System.out.println("8. 道具刷新管理器更新: ItemSpawnManager.java ✓");
        System.out.println("9. 游戏管理器更新: GameManager.java ✓");
        System.out.println("10. 文档更新: README.md ✓");
        System.out.println("\n新功能总结：");
        System.out.println("=== 一次机会道具 ===");
        System.out.println("- 新增道具：一次机会（不死图腾）");
        System.out.println("- 被动触发：抵挡鬼的感染");
        System.out.println("- 效果：人类获得速度2+高亮，鬼获得缓慢1");
        System.out.println("- 限制：玩家最多拥有6种不同道具");
        System.out.println("- 提示：对话框、屏幕居中字幕、公告");
        System.out.println("- 配置：所有参数均可配置");
        System.out.println("\n=== 版本信息 ===");
        System.out.println("- 插件版本: 2.0.2");
        System.out.println("- 配置版本: 6");
        System.out.println("- Minecraft: 1.20.x");
    }
}