package fr.premier.regions;

import fr.premier.regions.database.RegionsDatabase;
import fr.premier.regions.region.RegionManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionsPlugin extends JavaPlugin {

    private RegionManager regionManager;
    private RegionsDatabase database;

    @Override
    public void onEnable() {
        this.regionManager = new RegionManager(this);
        this.database = new RegionsDatabase(this);
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }

    public RegionsDatabase getDatabase() {
        return database;
    }
}
