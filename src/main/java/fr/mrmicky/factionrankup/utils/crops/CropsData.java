package fr.mrmicky.factionrankup.utils.crops;

import org.bukkit.block.BlockState;

public abstract class CropsData {

    private static final boolean MODERN = isModern();

    protected final BlockState blockState;

    protected CropsData(BlockState blockState) {
        this.blockState = blockState;
    }

    public abstract void setRipe();

    public abstract boolean isRiped();

    public static CropsData of(BlockState state) {
        return MODERN ? CropsDataModern.of(state) : CropsDataLegacy.of(state);
    }

    private static boolean isModern() {
        try {
            Class.forName("org.bukkit.block.data.BlockData");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
