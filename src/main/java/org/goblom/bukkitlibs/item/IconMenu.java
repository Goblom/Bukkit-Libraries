package org.goblom.bukkitlibs.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
 
public class IconMenu implements Listener {
 
    private String name;
    private int size;
    private OptionClickEventHandler handler;
    private Plugin plugin;
   
    private String[] optionNames;
    private ItemStack[] optionIcons;
    private String[] optionActions;
    
    private List<String> viewing = new ArrayList<String>();
    
    public IconMenu(String name, int size, OptionClickEventHandler handler, Plugin plugin) {
        this.name = name;
        this.size = size;
        this.handler = handler;
        this.plugin = plugin;
        this.optionNames = new String[size];
        this.optionActions = new String[size];
        this.optionIcons = new ItemStack[size];
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public IconMenu setOption(int position, ItemStack icon, String name, String action, String... info) {
        optionNames[position] = name;
        optionActions[position] = action;
        optionIcons[position] = setItemNameAndLore(icon, name, info);
        return this;
    }
    
    public IconMenu setOption(int position, ItemStack icon, String name, String action, List<String> info) {
        optionNames[position] = name;
        optionActions[position] = action;
        optionIcons[position] = setItemNameAndLore(icon, name, info);
        return this;
    }
    
    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(player, size, name);
        for (int i = 0; i < optionIcons.length; i++) {
            if (optionIcons[i] != null) {
                inventory.setItem(i, optionIcons[i]);
            }
        }
        player.openInventory(inventory);
    }
   
    public void destroy() {
        HandlerList.unregisterAll(this);
        handler = null;
        plugin = null;
        optionNames = null;
        optionIcons = null;
        optionActions = null;
    }
   
    @EventHandler(priority=EventPriority.MONITOR)
    void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(name)) {
            event.setCancelled(true);
            int slot = event.getRawSlot();
            if (slot >= 0 && slot < size && optionNames[slot] != null) {
                if (optionIcons[slot] != null) {
                    if ((optionActions[slot] != null) && (!optionActions[slot].equals(""))) {
                        Plugin plugin = this.plugin;
                        OptionClickEvent e = new OptionClickEvent(
                                (Player)event.getWhoClicked(), 
                                slot, 
                                optionNames[slot], 
                                optionIcons[slot],
                                optionActions[slot]
                        );
                        
                        handler.onOptionClick(e);
                        if (e.willClose()) {
                            final Player p = (Player)event.getWhoClicked();
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                public void run() {
                                    p.closeInventory();
                                }
                            }, 1);
                        }
                        if (e.willDestroy()) {
                            destroy();
                        }
                    }
                }
            }
        }
    }
    
    public interface OptionClickEventHandler {
        public void onOptionClick(OptionClickEvent event);       
    }
    
    public class OptionClickEvent {
        private Player player;
        private int position;
        private String name;
        private boolean close;
        private boolean destroy;
        private ItemStack item;
        private String action;
            
        public OptionClickEvent(Player player, int position, String name, ItemStack item, String action) {
            this.player = player;
            this.position = position;
            this.name = name;
            this.action = action;
            this.item = item;
        }
        
        public Player getPlayer() {
            return player;
        }
       
        public int getPosition() {
            return position;
        }
       
        public String getName() {
            return name;
        }
        
        public String getAction() {
            return action;
        }
        
        public ItemStack getMeta() {
            return item;
        }
       
        public boolean willClose() {
            return close;
        }
       
        public boolean willDestroy() {
            return destroy;
        }
       
        public void setWillClose(boolean close) {
            this.close = close;
        }
       
        public void setWillDestroy(boolean destroy) {
            this.destroy = destroy;
        }
    }
   
    private ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore) {
        ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            im.setLore(Arrays.asList(lore));
        item.setItemMeta(im);
        return item;
    }
    
    public ItemStack setItemNameAndLore(ItemStack item, String name, List<String> lore) {
        ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }
    
    public IconMenu resetOptions() {
        this.optionNames = new String[this.size];
        this.optionActions = new String[this.size];
        this.optionIcons = new ItemStack[this.size];
        return this;
    }

    public void updateName(String name) { 
        this.name = name; 
    }
    
    public void updateSize(int size) { 
        this.size = size; 
    }
    
    public void updateAction(int position, String action) {
        this.optionActions[position] = action;
    }
    
    public void updateIcon(int position, String optionName, ItemStack icon) {
        this.optionNames[position] = optionName;
        this.optionIcons[position] = icon;
    }
    
    public String getName() { 
        return this.name; 
    }
    
    public int getSize() { 
        return this.size; 
    }
    
    public String getAction(int position) {
        return this.optionActions[position];
    }
    
    public ItemStack getIcon(int position) {
        return this.optionIcons[position];
    }
    
    public String getIconName(int position) {
        return this.optionNames[position];
    }
    
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        for (Player player : this.getViewers()) {
            close(player);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (viewing.contains(event.getPlayer().getName())) viewing.remove(event.getPlayer().getName());
    }
    
    public IconMenu close(Player player) {
        if (player.getOpenInventory().getTitle().equals(this.name)) player.closeInventory();
        return this;
    }
    
    public List<Player> getViewers() {
        List<Player> viewers = new ArrayList<Player>();
        for (String s : viewing) {
            viewers.add(Bukkit.getPlayer(s));
        }
        return viewers;
    }
    
    public IconMenu closeViewers() {
        for (Player player : this.getViewers()) {
            close(player);
        }
        return this;
    }
}