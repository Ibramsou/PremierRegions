package fr.premier.regions.wand;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public record WandKey(World world, UUID uuid) {

    public static WandKey of(Player player) {
        return new WandKey(player.getWorld(), player.getUniqueId());
    }

    public static WandKey of(Player player, World world) {
        return new WandKey(world, player.getUniqueId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WandKey wandKey = (WandKey) o;
        return Objects.equals(uuid, wandKey.uuid) && Objects.equals(world, wandKey.world);
    }

    @Override
    public int hashCode() {
        return Objects.hash(world, uuid);
    }
}
