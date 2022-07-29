package com.luncert.robotcontraption.groups;

import com.luncert.robotcontraption.Reference;
import com.luncert.robotcontraption.index.RCBlocks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;


public class ModGroup extends CreativeModeTab {

    public static ModGroup MAIN;;

    public ModGroup(String name) {
        super(Reference.MOD_ID + ":" + name);
        MAIN = this;
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RCBlocks.AIRCRAFT_STATION.get());
    }
}
