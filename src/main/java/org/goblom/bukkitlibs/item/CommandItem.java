/*
 * The MIT License
 *
 * Copyright 2013 Goblom.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.goblom.bukkitlibs.item;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 * Command Items. Creates any item with the ability to run a command from
 * anywhere in a world. This is a class file for my upcoming plugin
 * CommandItems.
 *
 * @author Goblom
 */
public class CommandItem implements Listener {

    private ItemStack item;
    private String command;

    private Plugin plugin;
    private UseEventHandler handler;

    /**
     * Create Command Item
     *
     * @param item ItemStack that the Command Item is attached to
     * @param command Command that is attached to the ItemStack
     * @param handler Handler to get data from the CommandItem
     * @param plugin Your plugin
     */
    public CommandItem(ItemStack item, String command, UseEventHandler handler, Plugin plugin) {
        this.item = item;
        this.command = command;

        this.plugin = plugin;
        this.handler = handler;
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Set the command for the command item
     *
     * @param command command to set
     * @return Command Item
     */
    public CommandItem setCommand(String command) {
        this.command = command;
        return this;
    }

    /**
     * Sets the ItemStack for the command item.
     *
     * @param item itemstack to set
     * @return this
     */
    public CommandItem setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    /**
     * gets the registered command
     *
     * @return command
     */
    public String getCommand() {
        return command;
    }

    /**
     * gets the item stack
     *
     * @return item
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * Perform the command for the given Command Item
     *
     * @param player Player to perform the command
     */
    public void use(Player player) {
        player.performCommand(command);
    }

    /**
     * Give command item to a player.
     *
     * @param player Player to give the command item to.
     */
    public void giveItem(Player player) {
        player.getInventory().addItem(item);
    }

    /**
     * Destroy the Command Item (Mostly)
     */
    public void destroy() {
        HandlerList.unregisterAll(this);
        this.command = null;
        this.item = null;
        this.plugin = null;
        this.handler = null;
    }

    public interface UseEventHandler {

        public void onUseEvent(UseEvent event);
    }

    public class UseEvent {

        private Player player;
        private ItemStack item;

        private String command;

        private boolean destroy;

        /**
         * @param player Player involved with this event
         * @param item Itemstack involved with this event
         * @param command Command from CommandItem
         */
        public UseEvent(Player player, ItemStack item, String command) {
            this.player = player;
            this.item = item;
            this.command = command;
        }

        /**
         * Player involved in this event
         * @return Player involved in event
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * Gets command associated with the item
         * @return Command to run
         */
        public String getCommand() {
            return command;
        }

        /**
         * Gets the itemstack from this event
         * @return ItemStack
         */
        public ItemStack getItem() {
            return item;
        }

        /**
         * Should we make the item act like a normal item now.
         * @param destroy 
         */
        public void setWillDstroy(boolean destroy) {
            this.destroy = destroy;
        }

        /**
         * Should we destroy the item?
         * @return destroy
         */
        public boolean willDestroy() {
            return destroy;
        }
        
        /**
         * Run the command associated with this event
         * @return true if successful, false if not
         */
        public boolean run() {
            return player.performCommand(command);
        }
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            return;
        }
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            ItemStack handItem = event.getItem();
            if (handItem.isSimilar(item)) {
                UseEvent ue = new UseEvent(
                        event.getPlayer(),
                        event.getItem(),
                        command
                );
                handler.onUseEvent(ue);
                if (ue.willDestroy()) destroy();
                event.setCancelled(true);
            }
        }
    }
}
