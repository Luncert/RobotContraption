package com.luncert.robotcontraption.util;

import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LuaConverter {

    public static <T> List<String> tagsToList(@NotNull Supplier<Stream<TagKey<T>>> tags) {
        if (tags.get().findAny().isEmpty())
            return Collections.emptyList();
        return tags.get().map(LuaConverter::tagToString).collect(Collectors.toList());
    }

    public static <T> String tagToString(@NotNull TagKey<T> tag) {
        return tag.registry().location() + "/" + tag.location();
    }
}
