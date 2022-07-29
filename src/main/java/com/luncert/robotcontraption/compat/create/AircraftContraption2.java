// package com.luncert.robotcontraption.compat.create;
//
// import com.luncert.robotcontraption.content.aircraft.AircraftStationBlock;
// import com.luncert.robotcontraption.content.index.RCBlocks;
// import com.simibubi.create.content.contraptions.components.structureMovement.AssemblyException;
// import com.simibubi.create.content.contraptions.components.structureMovement.BlockMovementChecks;
// import com.simibubi.create.content.contraptions.components.structureMovement.mounted.CartAssemblerTileEntity;
// import com.simibubi.create.content.contraptions.components.structureMovement.mounted.MountedContraption;
// import net.minecraft.core.BlockPos;
// import net.minecraft.core.Direction;
// import net.minecraft.world.level.Level;
// import net.minecraft.world.level.LevelAccessor;
// import net.minecraft.world.level.block.entity.BlockEntity;
// import net.minecraft.world.level.block.state.BlockState;
// import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
// import org.apache.commons.lang3.tuple.Pair;
//
// public class AircraftContraption extends MountedContraption {
//
//     public AircraftContraption(CartAssemblerTileEntity.CartMovementMode mode) {
//         super(mode);
//     }
//
//     @Override
//     public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
//         if (!searchMovedStructure(world, pos, null))
//             return false;
//
//         addBlock(pos, Pair.of(new StructureTemplate.StructureBlockInfo(
//                 pos, RCBlocks.AIRCRAFT_ANCHOR.getDefaultState(), null), null));
//
//         return blocks.size() != 1;
//     }
//
//     @Override
//     protected Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
//         Pair<StructureTemplate.StructureBlockInfo, BlockEntity> pair = super.capture(world, pos);
//         StructureTemplate.StructureBlockInfo capture = pair.getKey();
//         if (!RCBlocks.AIRCRAFT_STATION.has(capture.state))
//             return pair;
//
//         return Pair.of(new StructureTemplate.StructureBlockInfo(
//                 pos, AircraftStationBlock.createAnchor(capture.state), null), pair.getValue());
//     }
//
//     @Override
//     protected boolean movementAllowed(BlockState state, Level world, BlockPos pos) {
//         return BlockMovementChecks.isMovementAllowed(state, world, pos);
//     }
//
//     @Override
//     protected boolean customBlockPlacement(LevelAccessor world, BlockPos pos, BlockState state) {
//         return RCBlocks.AIRCRAFT_ANCHOR.has(state);
//     }
//
//
//     @Override
//     protected boolean customBlockRemoval(LevelAccessor world, BlockPos pos, BlockState state) {
//         return RCBlocks.AIRCRAFT_ANCHOR.has(state);
//     }
//
//     public boolean canBeStabilized(Direction facing, BlockPos localPos) {
//         return false;
//     }
// }
