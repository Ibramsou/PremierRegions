package fr.premier.regions.api;

import java.util.Iterator;
import java.util.ServiceLoader;

public class PreRegionsProvider {

    private static final PreRegionsAPI api;

    public static PreRegionsAPI getApi() {
        return api;
    }

    static {
        final ServiceLoader<PreRegionsAPI> loader = ServiceLoader.load(PreRegionsAPI.class, PreRegionsProvider.class.getClassLoader());
        final Iterator<PreRegionsAPI> iterator = loader.iterator();
        if (iterator.hasNext()) {
            api = iterator.next();
        } else {
            throw new IllegalArgumentException("No PreRegionsAPI implementation found");
        }
    }
}
