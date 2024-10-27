package fr.premier.regions.api;

public interface PreRegionsAPI {

    static PreRegionsAPI getInstance() {
        return PreRegionsProvider.getApi();
    }
}
