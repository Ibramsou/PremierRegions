package fr.premier.regions.region;

import org.bukkit.Location;

import java.util.UUID;

public record Region(UUID uuid, String name, Location minLocation, Location maxLocation) {

}
