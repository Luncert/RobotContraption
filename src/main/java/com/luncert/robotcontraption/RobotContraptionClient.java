package com.luncert.robotcontraption;

import com.luncert.robotcontraption.index.RCBlockPartials;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RobotContraptionClient {

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        modEventBus.addListener(RobotContraptionClient::clientInit);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        RCBlockPartials.init();
    }
}
