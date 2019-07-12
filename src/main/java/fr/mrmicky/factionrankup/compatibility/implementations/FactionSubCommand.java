package fr.mrmicky.factionrankup.compatibility.implementations;

/**
 * @author MrMicky
 */
public abstract class FactionSubCommand {

    private final String name;
    private final String description;

    public FactionSubCommand(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
