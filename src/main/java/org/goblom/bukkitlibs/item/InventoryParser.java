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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Goblom
 */
public class InventoryParser {
    private final String pluginName;
    
    public InventoryParser(Plugin plugin) {
        this.pluginName = plugin.getName();
    }
    
    public Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }
    
    public File getFileForPlayer(Player player) {
        return new File(getPlugin().getDataFolder() + File.pathSeparator + "inv" + File.pathSeparator + "players" + File.pathSeparator + player.getName() + ".yml");
    }
    
    public FileConfiguration getConfigForPlayer(Player player) {
        return YamlConfiguration.loadConfiguration(getFileForPlayer(player));
    }
    
    public void saveInventory(Player player) {
        ParseForPlayer pfp = new ParseForPlayer(player);
        pfp.saveInventory(false);
    }
    
    public class ParseForPlayer {
        private final String playerName;
        private final File playerFile;
        
        public ParseForPlayer(Player player) {
            this.playerName = player.getName();
            this.playerFile = getFileForPlayer(player);
        }
        
        public Player getPlayer() {
            return Bukkit.getPlayer(playerName);
        }
        
        public File getFile() {
            return playerFile;
        }
        
        private FileConfiguration getFileConfig() {
            return YamlConfiguration.loadConfiguration(getFile());
        }
        
        public boolean saveInventory(boolean override) {
            if (getFile().exists() && override) getFile().delete();
            
            ItemStack[] armor = getPlayer().getInventory().getArmorContents();
            ItemStack[] contents = getPlayer().getInventory().getContents();
            Collection<PotionEffect> potionEffects = getPlayer().getActivePotionEffects();
            
            FileConfiguration playerConfig = getFileConfig();
            
            for (int i = 0; i < armor.length; i++) {
                ItemStack item = armor[i];
                if ((item != null) && (item.getType() != Material.AIR)) {
                    playerConfig.set("Armor." + i + ".Material", item.getType().name());
                    playerConfig.set("Armor." + i + ".Amount", item.getAmount());
                    playerConfig.set("Armor." + i + ".Durability", item.getDurability());
//                    playerConfig.set("Armor." + i + ".MaterialData", item.getData().getData());
                    if (item.hasItemMeta()) {
                        ItemMeta meta = item.getItemMeta();
                        if (meta.hasDisplayName()) playerConfig.set("Armor." + i + ".ItemMeta.Display-Name", meta.getDisplayName());
                        if (meta.hasLore()) playerConfig.set("Armor." + i + ".ItemMeta.Lore", meta.getLore());
                        if (meta.hasEnchants()) {
                            for (Enchantment enchant : meta.getEnchants().keySet()) {
                                playerConfig.set("Armor." + i + ".ItemMeta.Enchantment." + enchant.getName() + ".Level", meta.getEnchantLevel(enchant));
                            }
                        }
                    }
                } else playerConfig.set("Armor." + i + ".Material", Material.AIR.name());
            }
            
            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if ((item != null) && (item.getType() != Material.AIR)) {
                    playerConfig.set("Contents." + i + ".Material", item.getType().name());
                    playerConfig.set("Contents." + i + ".Amount", item.getAmount());
                    playerConfig.set("Contents." + i + ".Durability", item.getDurability());
//                    playerConfig.set("Contents." + i + ".MaterialData", item.getData().getData());
                    if (item.hasItemMeta()) {
                        ItemMeta meta = item.getItemMeta();
                        if (meta.hasDisplayName()) playerConfig.set("Contents." + i + ".ItemMeta.Display-Name", meta.getDisplayName());
                        if (meta.hasLore()) playerConfig.set("Contents." + i + ".ItemMeta.Lore", meta.getLore());
                        if (meta.hasEnchants()) {
                            for (Enchantment enchant : meta.getEnchants().keySet()) {
                                playerConfig.set("Contents." + i + ".ItemMeta.Enchantment." + enchant.getName() + ".Level", meta.getEnchantLevel(enchant));
                            }
                        }
                    }
                } else playerConfig.set("Contents." + i + ".Material", Material.AIR.name());
            }
            
            for (PotionEffect effect : potionEffects) {
                playerConfig.set("Effects." + effect.getType().getName() + ".Level", effect.getAmplifier());
                playerConfig.set("Effects." + effect.getType().getName() + ".Duration", effect.getDuration());
                playerConfig.set("Effects." + effect.getType().getName() + ".Ambience", effect.isAmbient());
            }
            
            try {
                playerConfig.save(getFile());
                return true;
            } catch (IOException e) { return false; }
        }
        
        public ItemStack[] getArmor() {
            ItemStack[] items = null;
            FileConfiguration config = getFileConfig();
            
            if (getFile().exists()) {
                if (config.contains("Armor")) {
                    if (config.isConfigurationSection("Armor")) {
                        int size = config.getInt("Armor", 4);
                        items = new ItemStack[size];
                        for (int i = 0; i < size; i++) {
                            if (config.contains("Armor." + i)) {
                                Material mat = Material.getMaterial(config.getString("Armor." + i + ".Material"));
                                if (mat != Material.AIR) {
                                    int amount = config.getInt("Armor." + i + ".Amount");
                                    short durability = (short) config.getInt("Armor." + i + ".Durability");
                                    ItemStack item = new ItemStack(mat, amount, durability);
                                    
                                    if (config.contains("Armor." + i + ".ItemMeta")) {
                                        ItemMeta meta = item.getItemMeta();
                                        if (config.contains("Armor." + i + ".ItemMeta.Lore")) meta.setLore(config.getStringList("Armor." + i + ".ItemMeta.Lore"));
                                        if (config.contains("Armor." + i + ".ItemMeta.Display-Name")) meta.setDisplayName(config.getString("Armor." + i + ".ItemMeta.Display-Name"));
                                        if (config.contains("Armor." + i + ".ItemMeta.Enchantment")) {
                                            for (String enchantName : config.getConfigurationSection("Armor." + i + ".ItemMeta.Enchantment").getKeys(false)) {
                                                meta.addEnchant(Enchantment.getByName(enchantName), config.getInt("Armor." + i + ".ItemMeta.Enchantment." + enchantName + ".Level"), true);
                                            }
                                        }
                                        item.setItemMeta(meta);
                                    }
                                    items[i] = item;
                                } else items[i] = new ItemStack(Material.AIR);                   
                            }
                        }
                    }
                }
                return items;
            } else {
                saveInventory(false);
                return getArmor();
            }
        }
        
        public ItemStack[] getContents() {
            ItemStack[] items = null;
            FileConfiguration config = getFileConfig();
            
            if (getFile().exists()) {
                if (config.contains("Contents")) {
                    if (config.isConfigurationSection("Contents")) {
                        int size = config.getInt("Contents", 4);
                        items = new ItemStack[size];
                        for (int i = 0; i < size; i++) {
                            if (config.contains("Contents." + i)) {
                                Material mat = Material.getMaterial(config.getString("Contents." + i + ".Material"));
                                if (mat != Material.AIR) {
                                    int amount = config.getInt("Contents." + i + ".Amount");
                                    short durability = (short) config.getInt("Contents." + i + ".Durability");
                                    ItemStack item = new ItemStack(mat, amount, durability);
                                    
                                    if (config.contains("Contents." + i + ".ItemMeta")) {
                                        ItemMeta meta = item.getItemMeta();
                                        if (config.contains("Contents." + i + ".ItemMeta.Lore")) meta.setLore(config.getStringList("Contents." + i + ".ItemMeta.Lore"));
                                        if (config.contains("Contents." + i + ".ItemMeta.Display-Name")) meta.setDisplayName(config.getString("Contents." + i + ".ItemMeta.Display-Name"));
                                        if (config.contains("Contents." + i + ".ItemMeta.Enchantment")) {
                                            for (String enchantName : config.getConfigurationSection("Contents." + i + ".ItemMeta.Enchantment").getKeys(false)) {
                                                meta.addEnchant(Enchantment.getByName(enchantName), config.getInt("Contents." + i + ".ItemMeta.Enchantment." + enchantName + ".Level"), true);
                                            }
                                        }
                                        item.setItemMeta(meta);
                                    }
                                    items[i] = item;
                                } else items[i] = new ItemStack(Material.AIR);                   
                            }
                        }
                    }
                }
                return items;
            } else {
                saveInventory(false);
                return getContents();
            }
        }
        
        public ItemStack[][] getAllItems() {
            return new ItemStack[][] { getArmor(), getContents() };
        }
        
        public Collection<PotionEffect> getPotionEffects() {
            Collection<PotionEffect> effects = new HashSet();            
            FileConfiguration config = getFileConfig();
            
            if (config.contains("Effects") && config.isConfigurationSection("Effects")) {
                for (String string : config.getConfigurationSection("Effects").getKeys(false)) {
                    int level = config.getInt("Effects." + string + ".Level");
                    int duration = config.getInt("Effects." + string + ".Duration");
                    boolean ambient = config.getBoolean("Effects." + string + ".Ambience");
                    effects.add(new PotionEffect(PotionEffectType.getByName(string), duration, level, ambient));
                }
            }
            return effects;
        }
    }
}
