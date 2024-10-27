package fr.premier.regions;

import fr.premier.regions.database.RegionsDatabase;
import fr.premier.regions.flag.FlagManager;
import fr.premier.regions.region.RegionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionsPlugin extends JavaPlugin {

    private static RegionsPlugin instance;

    public static RegionsPlugin getInstance() {
        return instance;
    }

    private RegionManager regionManager;
    private FlagManager flagManager;
    private RegionsDatabase database;

    @Override
    public void onEnable() {
        instance = this;

        this.flagManager = new FlagManager();
        this.regionManager = new RegionManager(this);
        this.database = new RegionsDatabase(this);
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
