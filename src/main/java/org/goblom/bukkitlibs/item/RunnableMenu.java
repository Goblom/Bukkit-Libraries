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
package org.goblom.bukkitlibs.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Goblom
 */
public class RunnableMenu {

    private Plugin plugin;
    private Map<Integer, MenuOption> slots;
    private int size;
    private String name;
    private Listener listener;
    private List<String> viewers;
    
    public RunnableMenu(final Plugin plugin, int size, String name) {
        this.plugin = plugin;
        this.size = size;
        this.slots = Maps.newHashMap();
        this.name = ChatColor.translateAlternateColorCodes('&', name);
        this.viewers = Lists.newArrayList();
        
        this.listener = new Listener() {
            @EventHandler
            public void onPluginDisable(PluginDisableEvent event) {
                if (event.getPlugin().getName().equals(plugin.getName())) {
                    for (Player player : getViewers()) {
                        close(player);
                    }
                }
            }
            
            @EventHandler
            public void onInventoryOpen(InventoryOpenEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Player player = (Player) event.getPlayer();
                    
                    if (isViewing(player)) return;
                    
                    if (event.getInventory().getTitle().equals(getName())) {
                        viewers.add(player.getName());
                    }
                }
            }
            
            @EventHandler
            public void onInventoryClose(InventoryCloseEvent event) {
                if (event.getPlayer() instanceof Player) {
                    Player player = (Player) event.getPlayer();
                    
                    if (isViewing(player)) {
                        viewers.remove(player.getName());
                    }
                }
            }
            
            @EventHandler(priority = EventPriority.MONITOR)
            public void onInventoryClick(InventoryClickEvent event) {
                Inventory inv = event.getInventory();
                
                if (inv.getTitle().equals(getName())) {
                    event.setCancelled(true);
                    
                    int slot = event.getRawSlot();
                    MenuOption option = getOption(slot);
                    
                    if (slot >= 0 && slot < getSize() && option != null) {
                        if (event.getWhoClicked() instanceof Player) {
                            final Player player = (Player) event.getWhoClicked();
                            OptionRunner runner = option.getRunner();
                            
                            if (option.getViewPermission() != null && !option.getViewPermission().isEmpty()) {
                                if (!player.hasPermission(option.getViewPermission())) {
                                    return;
                                }
                            }
                            
                            if (runner != null) {
                                runner.onClick(player);
                                
                                if (runner.willClose()) {
                                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(
                                            plugin, 
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    player.closeInventory();
                                                }
                                            }, 1);
                                }
                                
                                if (runner.willDestroy()) {
                                    destroy();
                                }
                            }
                        }
                    }
                }
            }
        };
        
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    public void destroy() {
        HandlerList.unregisterAll(this.listener);
        this.plugin = null;
        this.size = 0;
        this.name = null;
        this.slots = null;
        this.listener = null;
        this.viewers = null;
    }
    
    public void open(Player player) {
        Inventory inv = plugin.getServer().createInventory(player, size, name);
        
        for (int slot : slots.keySet()) {
            MenuOption option = getOption(slot);
            
            if (option.getViewPermission() != null && !option.getViewPermission().isEmpty()) {
                if (!player.hasPermission(option.getViewPermission())) {
                    continue;
                }
            }
            
            inv.setItem(slot, option.toItem());
        }
        
        player.openInventory(inv);
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isViewing(Player player) {
        return viewers.contains(player.getName());
    }
    
    public boolean isViewing(String player) {
        return viewers.contains(player);
    }
    
    public List<Player> getViewers() {
        List<Player> list = Lists.newArrayList();
        
        for (String name : viewers) {
            Player player = plugin.getServer().getPlayer(name);
            
            if (player != null) {
                list.add(player);
            }
        }
        
        return list;
    }
    
    public int getSize() {
        return size;
    }
    
    public Map<Integer, MenuOption> getSlots() {
        return slots;
    }

    public MenuOption getOption(int slot) {
        if (slots.containsKey(slot)) {
            return slots.get(slot);
        }

        return null;
    }

    public void setOption(int slot, MenuOption option) {
        slots.put(slot, option);
    }
    
    public void setOption(int slot, ItemStack item, OptionRunner runner) {
        slots.put(slot, new MenuOption(item, runner));
    }
    
    public OptionBuilder buildOption(Material mat) {
        return new OptionBuilder(mat);
    }
    
    public boolean close(Player player) {
        if (!isViewing(player)) return false;
        InventoryView inv = player.getOpenInventory();
        
        if (inv != null) {
            if (inv.getTitle().equals(this.name)) {
                player.closeInventory();
                return true;
            }
                
            return false;
        }
        
        return false;
    }
    
    public void closeViewers() {
        for (Player player : getViewers()) {
            close(player);
        }
    }
    
    public static abstract class OptionRunner {
        private boolean close, destroy;
        
        public final void setWillClose(boolean bln) {
            this.close = bln;
        }
        
        public final void setWillDestroy(boolean bln) {
            this.destroy = bln;
        }
        
        public final boolean willClose() {
            return close;
        }
        
        public final boolean willDestroy() {
            return destroy;
        }
        
        public abstract void onClick(Player player);
    }

    public static class MenuOption {

        private String name;
        private List<String> lore;
        private Map<Enchantment, Integer> enchantments;
        private OptionRunner runner;
        private Material material;
        private int amount = 0;
        private short durability;
        private DyeColor dyeColor;
        private Color color;
        private String viewPermission;
        
        private MenuOption() { }

        public MenuOption(Material mat, OptionRunner runner) {
            this.material = mat;
            this.runner = runner;
        }

        public MenuOption(Material mat, String name, OptionRunner runner) {
            this.name = name;
            this.material = mat;
            this.runner = runner;
        }

        public MenuOption(ItemStack item, OptionRunner runner) {
            this.runner = runner;
            this.material = item.getType();
            this.amount = item.getAmount();
            this.durability = item.getDurability();
           
            if (item.getEnchantments() != null) {
                this.enchantments = item.getEnchantments();
            }
            
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                
                if (meta.hasLore()) {
                    this.lore = meta.getLore();
                }
                
                if (meta.hasDisplayName()) {
                    this.name = meta.getDisplayName();
                }
                
                if (meta instanceof LeatherArmorMeta) {
                    this.color = ((LeatherArmorMeta) meta).getColor();
                }
            }
            
            if (item.getData() instanceof Wool) {
                try {
                    Wool wool = (Wool) item.getData();
                    this.dyeColor = wool.getColor();
                } catch (Exception e) {};
            }
        }
        
        public Material getMaterial() {
            return material;
        }

        public String getName() {
            return name;
        }

        public List<String> getLore() {
            return lore;
        }

        public String getViewPermission() {
            return viewPermission;
        }
        
        public Map<Enchantment, Integer> getEnchantments() {
            return enchantments;
        }

        public OptionRunner getRunner() {
            return runner;
        }

        public int getAmount() {
            return amount;
        }

        public short getDurability() {
            return durability;
        }

        public DyeColor getDyeColor() {
            return dyeColor;
        }

        public Color getColor() {
            return color;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public void setDurability(short damageValue) {
            this.durability = damageValue;
        }

        public void setViewPermission(String permission) {
            this.viewPermission = permission;
        }
        
        public void setDyeColor(DyeColor color) {
            this.dyeColor = color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public void setLore(String... lore) {
            this.lore = Arrays.asList(lore);
        }

        public void setLore(List<String> lore) {
            this.lore = lore;
        }

        public void setMaterial(Material mat) {
            this.material = mat;
        }

        public void addEnchantment(Enchantment enchant, int level) {
            if (this.enchantments == null) {
                this.enchantments = Maps.newHashMap();
            }

            enchantments.put(enchant, level);
        }

        public void setRunner(OptionRunner runner) {
            this.runner = runner;
        }

        public ItemStack toItem() {
            ItemStack item = new ItemStack(material);

            switch (material) {
                case WOOL:
                    Wool wool = (Wool) item.getItemMeta();

                    if (dyeColor != null) {
                        wool.setColor(dyeColor);
                    }
                    item = wool.toItemStack();
                    break;
                case LEATHER_CHESTPLATE:
                case LEATHER_HELMET:
                case LEATHER_LEGGINGS:
                case LEATHER_BOOTS:
                    LeatherArmorMeta leatherMeta = (LeatherArmorMeta) item.getItemMeta();
                    if (color != null) {
                        leatherMeta.setColor(this.color);
                    }
                    item.setItemMeta(leatherMeta);
                    break;
            }

            if (this.amount != 0) {
                item.setAmount(this.amount);
            }
            
            if (this.durability != 0) {
                item.setDurability(this.durability);
            }

            ItemMeta meta = item.getItemMeta();

            if (this.lore != null && !this.lore.isEmpty()) {
                List<String> newLore = Lists.newArrayList();
                for (String line : this.lore) {
                    newLore.add(ChatColor.translateAlternateColorCodes('&', line));
                }
                
                meta.setLore(newLore);
            }
            
            if (this.name != null) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            if (this.enchantments != null && !this.enchantments.isEmpty()) {
                for (Enchantment enchant : this.enchantments.keySet()) {
                    item.addUnsafeEnchantment(enchant, this.enchantments.get(enchant));
                }
            }

            item.setItemMeta(meta);
            return item;
        }
    }

    public static class OptionBuilder {

        private MenuOption option;

        public OptionBuilder(Material mat) {
            this.option = new MenuOption();
            withMaterial(mat);
        }

        public OptionBuilder withMaterial(Material mat) {
            option.setMaterial(mat);
            return this;
        }

        public OptionBuilder withAmount(int amount) {
            option.setAmount(amount);
            return this;
        }

        public OptionBuilder withDurability(short damage) {
            option.setDurability(damage);
            return this;
        }

        public OptionBuilder withDyeColor(DyeColor color) {
            option.setDyeColor(color);
            return this;
        }

        public OptionBuilder withColor(Color color) {
            option.setColor(color);
            return this;
        }

        public OptionBuilder withName(String name) {
            option.setName(name);
            return this;
        }

        public OptionBuilder withLore(String... lore) {
            option.setLore(lore);
            return this;
        }

        public OptionBuilder withLore(List<String> lore) {
            option.setLore(lore);
            return this;
        }

        public OptionBuilder withViewPermission(String permission) {
            option.setViewPermission(permission);
            return this;
        }
        
        public OptionBuilder addEnchantment(Enchantment enchant, int level) {
            option.addEnchantment(enchant, level);
            return this;
        }

        public OptionBuilder withRunner(OptionRunner runner) {
            option.setRunner(runner);
            return this;
        }

        public MenuOption build() {
            return option;
        }
    }
}
