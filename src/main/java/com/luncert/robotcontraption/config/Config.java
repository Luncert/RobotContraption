package com.luncert.robotcontraption.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_ROBOT_CONTROLLER = "robot_controller";

    public static ForgeConfigSpec COMMON_CONFIG;
    private static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec.IntValue ROBOT_RPM_RANGE;

    static {
        COMMON_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        COMMON_BUILDER.comment("Robot Controller").push(CATEGORY_ROBOT_CONTROLLER);

        ROBOT_RPM_RANGE = COMMON_BUILDER.comment("Robot Controller min/max RPM.")
                .defineInRange("robot_controller_rpm_range", 64, 1, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, java.nio.file.Path path) {
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }
}
