package com.luncert.robotcontraption;

import com.luncert.robotcontraption.config.Config;
import com.luncert.robotcontraption.groups.ModGroup;
import com.luncert.robotcontraption.index.RCBlocks;
import com.luncert.robotcontraption.index.RCEntityTypes;
import com.luncert.robotcontraption.index.RCItems;
import com.luncert.robotcontraption.index.RCTileEntities;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public class RobotContraption
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final NonNullSupplier<CreateRegistrate> REGISTRATE = CreateRegistrate.lazy(Reference.MOD_ID);

    public RobotContraption() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        ModGroup.init();
        RCBlocks.register();
        RCTileEntities.register();
        RCItems.register();
        RCEntityTypes.register();

        // register config and load config from file
        Config.loadConfig(Config.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve("robotcontraption-common.toml"));
        modLoadingContext.registerConfig(ModConfig.Type.COMMON, Config.COMMON_CONFIG);

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> RobotContraptionClient.onCtorClient(modEventBus, forgeEventBus));
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE.get();
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(Reference.MOD_ID, path);
    }
}
