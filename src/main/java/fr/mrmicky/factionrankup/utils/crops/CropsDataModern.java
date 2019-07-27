package fr.mrmicky.factionrankup.utils.crops;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;

public class CropsDataModern extends CropsData {

    private final Ageable crops;

    public CropsDataModern(BlockState blockState, Ageable crops) {
        super(blockState);
        this.crops = crops;
    }

    @Override
    public void setRipe() {
        crops.setAge(crops.getMaximumAge());
    }

    @Override
    public boolean isRiped() {
        return crops.getAge() == crops.getMaximumAge();
    }

    private static boolean isCrops(Material type) {
        switch (type) {
            case WHEAT:
            case BEETROOT:
            case POTATOES:
            case CARROTS:
                return true;
            default:
                return false;
        }
    }

    public static CropsData of(BlockState state) {
        BlockData blockData = state.getBlockData();

        if (!(blockData instanceof Ageable) || !isCrops(state.getType())) {
            return null;
        }

        return new CropsDataModern(state, (Ageable) blockData);
    }
}
