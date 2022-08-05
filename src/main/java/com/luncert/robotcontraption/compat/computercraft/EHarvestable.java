package com.luncert.robotcontraption.compat.computercraft;

import com.simibubi.create.AllTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;

public enum EHarvestable {

    ORE_COAL(Tags.Blocks.ORES_COAL),
    ORE_IRON(Tags.Blocks.ORES_IRON),
    ORE_GOLD(Tags.Blocks.ORES_GOLD),
    ORE_COPPER(Tags.Blocks.ORES_COPPER),
    ORE_ZINC(AllTags.forgeBlockTag("ores/zinc")),
    ORE_EMERALD(Tags.Blocks.ORES_EMERALD),
    ORE_REDSTONE(Tags.Blocks.ORES_REDSTONE),
    ORE_LAPIS(Tags.Blocks.ORES_LAPIS),
    ORE_DIAMOND(Tags.Blocks.ORES_DIAMOND),
    ORE_QUARTZ(Tags.Blocks.ORES_QUARTZ),
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
