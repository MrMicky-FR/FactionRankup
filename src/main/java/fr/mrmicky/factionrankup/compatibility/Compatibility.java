package fr.mrmicky.factionrankup.compatibility;

public final class Compatibility {

    private Compatibility() {
        throw new UnsupportedOperationException();
    }

    private static IFactionManager factionManager;

    public static IFactionManager get() {
        return factionManager;
    }

    public static void setFactionManager(IFactionManager factionManager) {
        Compatibility.factionManager = factionManager;
    }
}
