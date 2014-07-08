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
package org.goblom.bukkitlibs.utils;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Goblom
 */
public class ChestUtils {

    private static Random RANDOM = new Random();

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    private static Class<?> getMCClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getVersion() + "." + name);
    }

    private static Class<?> getCraftClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
    }
    
    public static void fillChests(World world, ItemStack... items) {
        for (Chunk chunk : world.getLoadedChunks()) {
            for (BlockState state : chunk.getTileEntities()) {
                if (!(state instanceof Chest)) continue;
                fillChest((Chest) state, items);
            }
        }
    }

    public static void fillChest(Chest chest, ItemStack[] items) {
        List<Integer> filled = Lists.newArrayList();

        Inventory inv = chest.getInventory();
        inv.clear();

        int size = inv.getSize();
        
        for (int i = 0; i < items.length; i++) {
            int slot = RANDOM.nextInt(size);

            while (filled.contains(slot)) {
                slot = RANDOM.nextInt(size);
            }

            inv.setItem(slot, items[i]);
        }
    }
    
    public static void changeName(Inventory inv, String newName) throws Exception {
        Class<?> MinecraftInventory = getCraftClass("inventory.CraftInventoryCustom.MinecraftInventory");
        
        Field title = MinecraftInventory.getDeclaredField("title");
              title.setAccessible(true);
        
        title.set(inv, newName);
    }
}
