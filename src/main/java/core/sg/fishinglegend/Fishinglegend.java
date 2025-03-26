package core.sg.fishinglegend;

import org.bukkit.plugin.java.JavaPlugin;

public class Fishinglegend extends JavaPlugin {

    public static Fishinglegend instance;
    private static BoatManager boatManager;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new FishingListener(), this);
        getServer().getPluginManager().registerEvents(new BoatManager(), this);
        getLogger().info("FishingTierPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // remove all on disable
        boatManager.removeAll();

        getLogger().info("FishingTierPlugin has been disabled!");
    }

    public static Fishinglegend getInstance() {
        return instance;
    }


}
