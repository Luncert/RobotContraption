package com.luncert.robotcontraption.index;

import com.jozufozu.flywheel.core.layout.BufferLayout;
import com.jozufozu.flywheel.core.layout.CommonItems;

public class RCInstanceFormats {

    public static final BufferLayout TRANSLATING = kineticInstance()
            .addItems(CommonItems.NORMAL)
            .build();

    private static BufferLayout.Builder kineticInstance() {
        return BufferLayout.builder()
                .addItems(CommonItems.LIGHT, CommonItems.RGBA)
                .addItems(CommonItems.VEC3, CommonItems.FLOAT, CommonItems.FLOAT);
    }
}
