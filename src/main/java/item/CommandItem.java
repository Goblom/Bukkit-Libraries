/*
 * The MIT License
 *
 * Copyright 2014 Goblom.
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

package item;

import net.minecraft.util.org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Goblom
 */
public class CommandItem {    
    private ItemStack item;
    private UseRunner useRunner;
    private Listener listener;
    private Action[] runWith;
    private boolean isDestroyed = false;
    
    public CommandItem(Plugin plugin, ItemStack itemStack, UseRunner runner, Action... actions) {
        this(plugin, itemStack, runner, false, actions);
    }
    
    public CommandItem(Plugin plugin, ItemStack itemStack, UseRunner runner, final boolean checkLore, Action... actions) {
        Validate.notNull(actions, "You must have an action");
        
        this.item = itemStack;
        this.useRunner = runner;
        this.runWith = actions;
        
        this.listener = new Listener() {
            @EventHandler
            public void onPlayerInteract(PlayerInteractEvent event) {
                if (isDestroyed()) return;
                ItemStack handItem = event.getItem();
                if (handItem == null) return;
                CHECK: for (Action action : runWith) {
                    if (event.getAction().equals(action)) {
                        if (isSimilar(handItem, item, checkLore)) {
                            useRunner.onUse(event.getPlayer());
                            
                            if (useRunner.willDestroy()) {
                                destroy();
                            }
                            
                            event.setCancelled(true);
                            break CHECK;
                        }
                    }
                }
            }
        };
        
        Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
    }
    
    public void destroy() {
        HandlerList.unregisterAll(listener);
        this.listener = null;
        this.item = null;
        this.useRunner = null;
        this.runWith = null;
        this.isDestroyed = true;
    }
    
    public boolean isDestroyed() {
        return this.isDestroyed;
    }
    
    public ItemStack getItemStack() {
        return item;
    }
    
    public static abstract class UseRunner {
        private boolean destroy;
        
        public final void setWillDestroy(boolean bln) {
            this.destroy = bln;
        }
        
        public final boolean willDestroy() {
            return this.destroy;
        }
        
        public abstract void onUse(Player player);
    }
    
    //*******************************************************
    // Item Similarity Checking
    //*******************************************************
    private boolean isSimilarName(ItemStack one, ItemStack two) {
        try {
            String N1 = strip(one.getItemMeta().getDisplayName());
            String N2 = strip(two.getItemMeta().getDisplayName());
            return N1.equals(N1);
        } catch (Exception e) {}
        return false;
    }
    
    private String strip(String str) {
        return ChatColor.stripColor(str);
    }
    
    private boolean isSimilar(ItemStack one, ItemStack two, boolean checkLore) {
        boolean name = isSimilarName(one, two);
        boolean id = one.getTypeId() == two.getTypeId();
        boolean dura = one.getDurability() == two.getDurability();
        boolean lore = true;
        if (checkLore) {
            if (hasLore(one) && hasLore(two)) {
                if (hasLoreSize(one) && hasLoreSize(two)) {
                    if (getLoreSize(one) == getLoreSize(two)) {
                        int size = getLoreSize(one);
                        for (int i = 0; i < size; i++) {
                            String l1 = getLoreLine(one, i);
                            String l2 = getLoreLine(two, i);
                            if (!l1.equals(l2)) {
                                lore = false;
                                break;
                            }
                        }
                    } else {
                        lore = false;
                    }
                }
            }
        }
        return name && id && dura && lore;
    }
    
    private boolean hasLore(ItemStack item) {
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasLore()) {
                return true;
            }
        }
        return false;
    }
    
    private int getLoreSize(ItemStack item) {
        if (hasLore(item)) {
            return item.getItemMeta().getLore().size();
        }
        return 0;
    }
    
    private boolean hasLoreSize(ItemStack item) {
        return hasLore(item) && getLoreSize(item) >= 1;
    }
    
    private String getLoreLine(ItemStack item, int index) {
        try {
            return item.getItemMeta().getLore().get(index); 
        } catch (Exception e) {}
        return "";
    }
}
