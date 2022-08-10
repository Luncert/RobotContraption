package com.luncert.robotcontraption.content.blockreader;

import com.google.common.collect.ImmutableMap;
import com.luncert.robotcontraption.compat.aircraft.BaseAircraftComponent;
import com.luncert.robotcontraption.util.LuaConverter;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class BlockReaderComponent extends BaseAircraftComponent {

    @Override
    public String getComponentType() {
        return "BlockReader";
    }

    @LuaFunction
    public Map<String, Object> readBlock() {
        BlockState state = accessor.getComponentBlockState(name);
        BlockPos pos = accessor.getComponentPos(name);

        BlockPos targetPos = pos.relative(state.getValue(DirectionalBlock.FACING));
        BlockState targetState = accessor.world.getBlockState(targetPos);
        Block targetBlock = targetState.getBlock();
        ResourceLocation name = targetBlock.getRegistryName();
        return ImmutableMap.of(
                "x", targetPos.getX(),
                "y", targetPos.getY(),
                "z", targetPos.getZ(),
                "name", name == null ? "unknown" : name.toString(),
                "tags", LuaConverter.tagsToList(() -> targetBlock.builtInRegistryHolder().tags())
        );
    }
}
