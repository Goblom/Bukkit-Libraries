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

package org.goblom.bukkitlibs;

/**
 * Command Items. Creates any item with the ability to run a command from anywhere in a world.
 * This is a class file for my upcoming plugin CommandItems.
 * 
 * @warning This class is experimental and still in its planning stages. Please refrain from using it until further notice.
 * @author Goblom
 */
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Goblom
 */
public class CommandItem implements Listener {

    private ItemStack item;

    private String command;

    private Plugin plugin;
    private UseEventHandler handler;
    private UseTypeEvents useType;

    public CommandItem(ItemStack item, String command, UseEventHandler handler, UseTypeEvents useType, Plugin plugin) {
        this.item = item;
        this.command = command;

        this.plugin = plugin;
        this.handler = handler;
        this.useType = useType;

        new TypeEvents(this.useType);
    }

    public CommandItem setCommand(String command) {
        this.command = command;
        return this;
    }

    public CommandItem setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public String getCommand() {
        return command;
    }

    public ItemStack getItem() {
        return item;
    }

    public void use(Player player) {
        player.performCommand(command);
    }

    public void giveItem(Player player) {
        player.getInventory().addItem(item);
    }

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

        private String name;
        private String command;

        private boolean destroy;

        public UseEvent(Player player, ItemStack item, String command) {
            this.player = player;
            this.name = name;
            this.item = item;
            this.command = command;
        }

        public Player getPlayer() {
            return player;
        }

        public String getName() {
            return name;
        }

        public String getCommand() {
            return command;
        }

        public ItemStack getItem() {
            return item;
        }

        public void destroy() {
            this.destroy = true;
        }

        public boolean willDestroy() {
            return destroy;
        }
    }

    public enum UseTypeEvents {

        InventoryClickEvent,
        PlayerInteractEvent,
        ALL
    }

    abstract class CommandItemListener implements Listener {

        private Plugin plugin;

        public CommandItemListener(Plugin plugin) {
            this.plugin = plugin;
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    class TypeEvents {

        public TypeEvents(UseTypeEvents type) {
            if (type.equals(UseTypeEvents.PlayerInteractEvent)) {
                new PlayerInteract(plugin);
            }
            if (type.equals(UseTypeEvents.InventoryClickEvent)) {
                new InventoryClick(plugin);
            }
            if (type.equals(UseTypeEvents.ALL)) {
                new PlayerInteract(plugin);
                new InventoryClick(plugin);
            }
        }

        class PlayerInteract extends CommandItemListener {

            public PlayerInteract(Plugin plugin) {
                super(plugin);
            }
            
            @EventHandler
            void onPlayerInteract(PlayerInteractEvent event) {
                if (!event.getAction().equals(Action.RIGHT_CLICK_AIR)) return;
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
                    ItemStack handItem = event.getItem();
                    if (handItem.isSimilar(item)) {
                        event.setCancelled(true);
                        UseEvent ue = new UseEvent(
                                event.getPlayer(),
                                event.getItem(),
                                command
                        );
                        handler.onUseEvent(ue);
                    }
                }
            }
        }

        class InventoryClick extends CommandItemListener {

            public InventoryClick(Plugin plugin) {
                super(plugin);
            }

            @EventHandler
            void onInventoryClick(final InventoryClickEvent event) {
                if (event.getCursor().isSimilar(item)) {
                    event.setCancelled(true);

                    UseEvent ue = new UseEvent(
                            (Player) event.getWhoClicked(),
                            event.getCursor(),
                            command
                    );
                    handler.onUseEvent(ue);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        public void run() {
                            ((Player) event.getWhoClicked()).closeInventory();
                        }
                    }, 1);
                    if (ue.willDestroy()) {
                        destroy();
                    }
                }
            }
        }
    }
}
