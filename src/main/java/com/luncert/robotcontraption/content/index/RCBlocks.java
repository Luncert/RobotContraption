package com.luncert.robotcontraption.content.index;

import com.luncert.robotcontraption.RobotContraption;
import com.luncert.robotcontraption.config.Config;
import com.luncert.robotcontraption.content.groups.ModGroup;
import com.luncert.robotcontraption.content.robot.RobotStationBlock;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;


public class RCBlocks {

    private static final CreateRegistrate REGISTRATE = RobotContraption.registrate()
            .creativeModeTab(() -> ModGroup.MAIN);

    public static final BlockEntry<RobotStationBlock> ROBOT_STATION = REGISTRATE.block("robot_station", RobotStationBlock::new)
            .initialProperties(SharedProperties::stone)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag) //Dono what this tag means (contraption safe?).
            .transform(BlockStressDefaults.setImpact(512 / 256d))
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {
        Create.registrate().addToSection(ROBOT_STATION, AllSections.KINETICS);
    }
}
