package fr.mrmicky.factionrankup.storage;

public interface StorageProvider {

    void init() throws Exception;

    void shutdown();

    void setFactionLevel(String factionId, int level);

    void deleteFaction(String factionId);

}
