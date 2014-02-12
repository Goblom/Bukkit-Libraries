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

package org.goblom.bukkitlibs;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public class WorldGuardHelper {
    private static final WorldGuardPlugin wg = WGBukkit.getPlugin();
    
    public static RegionManager getRegionManager(World world) {
        return wg.getRegionManager(world);
    }
    
    public static RegionManager getRegionManager(String world) {
        return getRegionManager(Bukkit.getWorld(world));
    }
    
    public static boolean isPlayerPartOfRegion(String world, String region_id, String player) {
        return getRegionManager(world).getRegion(region_id).isMember(player);
    }
    
    public static boolean isPlayerPartOfRegion(String world, String region_id, Player player) {
        return getRegionManager(world).getRegion(region_id).isMember(player.getName());
    }
    
    public static boolean isPlayerPartOfRegion(World world, String region_id, String player) {
        return getRegionManager(world).getRegion(region_id).isMember(player);
    }
    
    public static boolean isPlayerPartOfRegion(World world, String region_id, Player player) {
        return getRegionManager(world).getRegion(region_id).isMember(player.getName());
    }
    
    /* isPlayerInAnyRegion(String world, Player player) */
    /* isPlayerInAnyRegion(String world, String player) */
    /* isPlayerInAnyRegion(World world, String player) */
    public static boolean isPlayerInAnyRegion(World world, Player player) {
        for (ProtectedRegion pr : getRegionManager(world).getRegions().values()) {
            Location loc = player.getLocation();
            if (pr.contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
                return true;
            }
        }
        return false;
    }
    
    /* isPlayerInRegion(World world, String region_id, String player) */
    /* isPlayerInRegion(String world, String region_id, String player) */
    /* isPlayerInRegion(String world, String region_id, Player palyer) */
    public static boolean isPlayerInRegion(World world, String region_id, Player player) {
        Location loc = player.getLocation();
        return getRegionManager(world).getRegion(region_id).contains(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }
}
