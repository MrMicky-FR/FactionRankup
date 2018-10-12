package fr.mrmicky.factionrankup.compatibility;

public class Compatibility {

    private static IFactionManager factionManager;

    public static IFactionManager get() {
        return factionManager;
    }

    public static void setFactionManager(IFactionManager factionManager) {
        Compatibility.factionManager = factionManager;
    }
}
