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
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Wool;
/**
 *
 * @author Goblom
 */
public class ItemFactory {
    
    private final Material mat;
    
    private String displayName;
    private int amount;
    private short durability;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;
    private DyeColor dyeColor;
    private Color color;
    
    public ItemFactory(Material mat) {
        this.mat = mat;
    }
    
    public ItemFactory(int id) {
        this(Material.getMaterial(id));
    }
    
    public ItemFactory withDisplayName(String name) {
        this.displayName = name;
        return this;
    }
    
    public ItemFactory withDurability(short value) {
        this.durability = value;
        return this;
    }
    
    public ItemFactory withAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public ItemFactory withLore(List<String> lore) {
        this.lore = lore;
        return this;
    }
    
    public ItemFactory withLore(String... lore) {
        if (this.lore != null) {
            this.lore.addAll(Arrays.asList(lore));
        } else {
            this.lore = Arrays.asList(lore);
        }
        return this;
    }
    
    public ItemFactory withEnchantment(Enchantment enchant, int level) {
        if (this.enchants == null) {
            this.enchants = Maps.newHashMap();
        }
        this.enchants.put(enchant, level);
        return this;
    }
    
    public ItemFactory withColor(DyeColor color) {
        this.dyeColor = color;
        return this;
    }
    
    public ItemFactory withColor(Color color) {
        this.color = color;
        return this;
    }
    
    public ItemStack build() {
        ItemStack item = new ItemStack(mat);
        
        switch (mat) {
            case WOOL:
                Wool wool = (Wool) item.getItemMeta();
                
                if (dyeColor != null) wool.setColor(dyeColor);
//                item = wool.toItemStack();
                break;
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
            case LEATHER_LEGGINGS:
            case LEATHER_BOOTS:
                LeatherArmorMeta leatherMeta = (LeatherArmorMeta) item.getItemMeta();
                if (color != null) leatherMeta.setColor(this.color);
                item.setItemMeta(leatherMeta);
                break;
        }
        
        if (this.amount != 0) item.setAmount(this.amount);
        if (this.durability != 0) item.setDurability(this.durability);
        
        ItemMeta meta = item.getItemMeta();
        
        if (this.lore != null) meta.setLore(this.lore);
        if (this.displayName != null) meta.setDisplayName(displayName);
        
        if (this.enchants != null) {
            for (Enchantment enchant : this.enchants.keySet()) {
                item.addUnsafeEnchantment(enchant, this.enchants.get(enchant));
            }
        }
        
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemFactory item(Material mat) {
        return new ItemFactory(mat);
    }
    
    public static ItemFactory item(int id) {
        return new ItemFactory(id);
    }
}
