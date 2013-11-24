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

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Goblom
 */
public class Item {
    
    private final Material material;
    private final short damageValue;
    private final int amount;
    private final String displayName;
    private final List<String> lore;
    
    private ItemStack item;
//    private ShapelessRecipe shapeless;
//    private ShapedRecipe shaped;
//    private FurnaceRecipe furnace;
    
    public Item(Material material, int amount) {
        this(material, (short) 0, amount, null, null);
    }
    
    public Item(Material material, short damageValue) {
        this(material, damageValue, 1, null, null);
    }
    
    public Item(Material material, short damageValue, int amount) {
        this(material, damageValue, amount, null, null);
    }
    
    public Item(Material material, String displayName) {
        this(material, (short) 0, 1, displayName, null);
    }
    
    public Item(Material material, List<String> lore) {
        this(material, (short) 0, 1, null, lore);
    }
    
    public Item(Material material, String displayName, List<String> lore) {
        this(material, (short) 0, 1, displayName, lore);
    }
    
    public Item(Material material, short damageValue, String displayName, List<String> lore) {
        this(material, damageValue, 1, displayName, lore);
    }
    
    public Item(Material material, int amount, String displayName, List<String> lore) {
        this(material, (short) 0, amount, displayName, lore);
    }
    
    public Item(Material material, short damageValue, int amount, String displayName) {
        this(material, damageValue, amount, displayName, null);
    }
    
    public Item(Material material, short damageValue, int amount, List<String> lore) {
        this(material, damageValue, amount, null, lore);
    }
    
    public Item(Material material, Short damageValue, int amount, String displayName, List<String> lore) {
        this.material = material;
        this.damageValue = damageValue;
        this.amount = amount;
        this.displayName = displayName;
        this.lore = lore;
        
        createItem();
    }
    
    private final void createItem() {
        if (damageValue != 0) {
            item = new ItemStack(material, amount, damageValue);
        } else item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        
        if ((displayName != null) || (!displayName.equals(""))) meta.setDisplayName(displayName);
        if (lore != null) meta.setLore(lore); 
        
        item.setItemMeta(meta);
    }
    
    public void give(Player player) {
        player.getInventory().addItem(item);
    }
    
    public void give(PlayerInventory inv) {
        inv.addItem(item);
    }
    
    public void add(Inventory inv) {
        inv.addItem(item);
    }
    
    public void set(Player player, int slot) {
        player.getInventory().setItem(slot, item);
    }
    
    public void set(Inventory inv, int slot) {
        inv.setItem(slot, item);
    }
    
    public void set(PlayerInventory inv, int slot) {
        inv.setItem(slot, item);
    }
    
    public void makeHelmet(Player player) {
        player.getInventory().setHelmet(item);
    }
    
    public void makeChestplate(Player player) {
        player.getInventory().setChestplate(item);
    }
    
    public void makeLeggings(Player player) {
        player.getInventory().setLeggings(item);
    }
    
    public void makeBoots(Player player) {
        player.getInventory().setBoots(item);
    }
    
    public void setHand(Player player) {
        player.getInventory().setItemInHand(item);
    }
    
    public void drop(Location loc) {
        loc.getWorld().dropItem(loc, item);
    }
    
    public void dropNaturally(Location loc) {
        loc.getWorld().dropItemNaturally(loc, item);
    }
}
