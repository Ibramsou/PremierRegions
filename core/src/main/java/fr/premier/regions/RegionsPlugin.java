package fr.premier.regions;

import fr.premier.regions.database.RegionsDatabase;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionsPlugin extends JavaPlugin {

    private RegionsDatabase database;

    @Override
    public void onEnable() {
        this.database = new RegionsDatabase(this);
    }

    public RegionsDatabase getDatabase() {
        return database;
    }
}
