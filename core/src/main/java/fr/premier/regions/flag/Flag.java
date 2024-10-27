package fr.premier.regions.flag;

import fr.premier.regions.api.flag.FlagState;
import fr.premier.regions.api.flag.PreFlag;

import java.util.Objects;

public record Flag(String name, String displayName, FlagState defaultState) implements PreFlag {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flag flag = (Flag) o;
        return Objects.equals(name, flag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public FlagState getDefaultState() {
        return defaultState;
    }
}
