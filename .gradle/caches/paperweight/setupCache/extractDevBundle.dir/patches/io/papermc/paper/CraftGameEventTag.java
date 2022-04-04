package io.papermc.paper;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.bukkit.GameEvent;
import org.bukkit.craftbukkit.v1_18_R2.tag.CraftTag;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CraftGameEventTag extends CraftTag<net.minecraft.world.level.gameevent.GameEvent, GameEvent> {

    public CraftGameEventTag(net.minecraft.core.Registry<net.minecraft.world.level.gameevent.GameEvent> registry, TagKey<net.minecraft.world.level.gameevent.GameEvent> tag) {
        super(registry, tag);
    }

    @Override
    public boolean isTagged(@NotNull GameEvent item) {
        return registry.getHolderOrThrow(ResourceKey.create(Registry.GAME_EVENT_REGISTRY, CraftNamespacedKey.toMinecraft(item.getKey()))).is(tag);
    }

    @Override
    public @NotNull Set<GameEvent> getValues() {
        return getHandle().stream().map((nms) -> Objects.requireNonNull(GameEvent.getByKey(CraftNamespacedKey.fromMinecraft(Registry.GAME_EVENT.getKey(nms.value()))), nms + " is not a recognized game event")).collect(Collectors.toUnmodifiableSet());
    }
}
