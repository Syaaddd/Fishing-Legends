package core.sg.fishinglegend;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Fishinglegend extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new FishingListener(), this);
        getServer().getPluginManager().registerEvents(new BoatManager(this), this);
        getLogger().info("FishingTierPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("FishingTierPlugin has been disabled!");
    }
}
