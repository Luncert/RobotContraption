package com.luncert.robotcontraption.compat.computercraft;

import com.simibubi.create.AllTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public enum EHarvestable {

    COAL(Tags.Blocks.ORES_COAL),
    IRON(Tags.Blocks.ORES_IRON),
    GOLD(Tags.Blocks.ORES_GOLD),
    COPPER(Tags.Blocks.ORES_COPPER),
    ZINC(AllTags.forgeBlockTag("ores/zinc")),
    EMERALD(Tags.Blocks.ORES_EMERALD),
    REDSTONE(Tags.Blocks.ORES_REDSTONE),
    LAPIS(Tags.Blocks.ORES_LAPIS),
    DIAMOND(Tags.Blocks.ORES_DIAMOND),
    QUARTZ(Tags.Blocks.ORES_QUARTZ),
    ANCIENT_DEBRIS(Tags.Blocks.ORES_NETHERITE_SCRAP)
    ;

    private final TagKey<Block> tag;

    EHarvestable(TagKey<Block> tag) {
        this.tag = tag;
    }

    public boolean test(BlockState state) {
        Block block = state.getBlock();
        return block.builtInRegistryHolder().is(tag);
    }
}
