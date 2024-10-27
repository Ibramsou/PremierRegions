package fr.premier.regions;

import fr.premier.regions.data.PlayerDataListener;
import fr.premier.regions.data.PlayerDataManager;
import fr.premier.regions.database.RegionsDatabase;
import fr.premier.regions.flag.FlagManager;
import fr.premier.regions.region.RegionListener;
import fr.premier.regions.region.RegionManager;
import fr.premier.regions.wand.WandListener;
import fr.premier.regions.wand.WandManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class RegionsPlugin extends JavaPlugin {

    private static RegionsPlugin instance;

    public static RegionsPlugin getInstance() {
        return instance;
    }

    private RegionManager regionManager;
    private FlagManager flagManager;
    private RegionsDatabase database;
    private PlayerDataManager playerDataManager;
    private WandManager wandManager;

    @Override
    public void onEnable() {
        instance = this;

        this.flagManager = new FlagManager();
        this.regionManager = new RegionManager(this);
        this.database = new RegionsDatabase(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.wandManager = new WandManager(this);

        List.of(new RegionListener(), new PlayerDataListener(this), new WandListener(this)).forEach(this::registerEvent);
    }

    @Override
    public void onDisable() {
        this.playerDataManager.disable();
        this.database.disable();
    }

    private void registerEvent(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public WandManager getWandManager() {
        return wandManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public FlagManager getFlagManager() {
        return flagManager;
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public RegionsDatabase getDatabase() {
        return database;
    }
}
