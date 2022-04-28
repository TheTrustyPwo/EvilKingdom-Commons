package net.evilkingdom.commons.border.objects;

/*
 * Made with love by https://kodirati.com/.
 */

import net.evilkingdom.commons.border.enums.BorderColor;
import net.evilkingdom.commons.menu.MenuImplementor;
import net.evilkingdom.commons.menu.objects.MenuItem;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.world.level.border.WorldBorder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Optional;

public class Border {

    private final JavaPlugin plugin;

    private double size;
    private Location center;
    private BorderColor color;
    private final Player player;
    private Optional<WorldBorder> currentBorder;

    /**
     * Allows you to create a border for a plugin.
     *
     * @param plugin ~ The plugin the border is for.
     * @param player ~ The player the border is for.
     * @param center ~ The center of the border.
     * @param size ~ The size of the border.
     * @param color ~ The color of the border.
     */
    public Border(final JavaPlugin plugin, final Player player, final Location center, final double size, final BorderColor color) {
        this.plugin = plugin;

        this.player = player;
        this.center = center;
        this.size = size;
        this.color = color;
        this.currentBorder = Optional.empty();
    }

    /**
     * Allows you to set the border's color.
     *
     * @param color ~ The color that will be set.
     */
    public void setBorderColor(final BorderColor color) {
        this.color = color;
    }

    /**
     * Allows you to retrieve the border's color.
     *
     * @return The border's color.
     */
    public BorderColor getColor() {
        return this.color;
    }

    /**
     * Allows you to set the border's center.
     *
     * @param center ~ The center that will be set.
     */
    public void setCenter(final Location center) {
        this.center = center;
    }

    /**
     * Allows you to retrieve the border's center.
     *
     * @return The border's center.
     */
    public Location getCenter() {
        return this.center;
    }

    /**
     * Allows you to set the border's size.
     *
     * @param size ~ The size that will be set.
     */
    public void setSize(final double size) {
        this.size = size;
    }

    /**
     * Allows you to retrieve the border's size.
     *
     * @return The border's size.
     */
    public Double getSize() {
        return this.size;
    }

    /**
     * Allows you to retrieve the border's player.
     *
     * @return The border's player.
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Allows you to retrieve the border's plugin.
     *
     * @return The border's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to show the border for the player.
     */
    public void show() {
        if (this.currentBorder.isPresent()) {
            return;
        }
        final WorldBorder border = new WorldBorder();
        border.world = ((CraftWorld) this.player.getWorld()).getHandle();
        border.setCenter(this.center.getX(), this.center.getZ());
        border.setSize(this.size);
        switch (this.color) {
            case BLUE -> border.lerpSizeBetween(border.getLerpTarget(), border.getLerpTarget(), Long.MAX_VALUE);
            case GREEN -> border.lerpSizeBetween(border.getLerpTarget(), border.getLerpTarget() + 0.1, Long.MAX_VALUE);
            case RED -> border.lerpSizeBetween(border.getLerpTarget(), border.getLerpTarget() - 0.1, Long.MAX_VALUE);
        }
        this.currentBorder = Optional.of(border);
        ((CraftPlayer) this.player).getHandle().connection.send(new ClientboundInitializeBorderPacket(border));
    }

    /**
     * Allows you to update the border for the player.
     */
    public void update() {
        if (this.currentBorder.isEmpty()) {
            return;
        }
        final WorldBorder border = new WorldBorder();
        border.world = ((CraftWorld) this.player.getWorld()).getHandle();
        border.setCenter(this.center.getX(), this.center.getY());
        border.setSize(this.size);
        switch (this.color) {
            case BLUE -> border.lerpSizeBetween(border.getLerpTarget(), border.getLerpTarget(), Long.MAX_VALUE);
            case GREEN -> border.lerpSizeBetween(border.getLerpTarget(), border.getLerpTarget() + 0.1, Long.MAX_VALUE);
            case RED -> border.lerpSizeBetween(border.getLerpTarget(), border.getLerpTarget() - 0.1, Long.MAX_VALUE);
        }
        this.currentBorder = Optional.of(border);
        ((CraftPlayer) this.player).getHandle().connection.send(new ClientboundInitializeBorderPacket(border));
    }

    /**
     * Allows you to hide the border for the player.
     */
    public void hide() {
        if (this.currentBorder.isEmpty()) {
            return;
        }
        final WorldBorder border = new WorldBorder();
        border.world = ((CraftWorld) this.player.getWorld()).getHandle();
        border.lerpSizeBetween(border.getLerpTarget(), border.getLerpTarget(), Long.MAX_VALUE);
        border.setCenter(this.player.getWorld().getWorldBorder().getCenter().getX(), this.player.getWorld().getWorldBorder().getCenter().getZ());
        border.setSize(this.player.getWorld().getWorldBorder().getSize());
        this.currentBorder = Optional.empty();
        ((CraftPlayer) this.player).getHandle().connection.send(new ClientboundInitializeBorderPacket(border));
    }

}
