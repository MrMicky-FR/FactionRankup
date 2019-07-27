package fr.mrmicky.factionrankup.utils.crops;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;

@SuppressWarnings("deprecation")
public class CropsDataLegacy extends CropsData {

    private final Crops crops;

    public CropsDataLegacy(BlockState blockState, Crops crops) {
        super(blockState);
        this.crops = crops;
    }

    @Override
    public void setRipe() {
        crops.setState(CropState.RIPE);

        if (!(blockState.getData() instanceof Crops)) {
            blockState.setRawData(crops.getData());
        }
    }

    @Override
    public boolean isRiped() {
        return crops.getState() == CropState.RIPE;
    }

    private static boolean isCrops(Material type) {
        return type.getData() == Crops.class || type == Material.POTATO || type == Material.CARROT || type.toString().equals("BEETROOT_BLOCK");
    }

    private static MaterialData transformCropsData(MaterialData data) {
        if (data instanceof Crops) {
            return data;
        }

        if (isCrops(data.getItemType())) {
            return new Crops(data.getItemType(), data.getData());
        }

        return data;
    }

    public static CropsData of(BlockState state) {
        MaterialData data = transformCropsData(state.getData());
        if (!(data instanceof Crops)) {
            return null;
        }

        return new CropsDataLegacy(state, (Crops) data);
    }
}
