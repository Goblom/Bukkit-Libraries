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

package org.goblom.bukkitlibs.config;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Goblom
 */
public class LocationParser {
    private final FileConfiguration config;
    
    public LocationParser(FileConfiguration config) {
        this.config = config;
    }
    
    
    public Location getFromString(String path, String splitBy, boolean yawPitch) {
        String[] parse = config.getString(path).split(splitBy);
        if (!yawPitch) {
            return new Location(Bukkit.getWorld(parse[0]), Double.valueOf(parse[1]), Double.valueOf(parse[2]), Double.valueOf(parse[3]));
        } else return new Location(Bukkit.getWorld(parse[0]), Double.valueOf(parse[1]), Double.valueOf(parse[2]), Double.valueOf(parse[3]), Float.valueOf(parse[4]), Float.valueOf(parse[5]));
    }
    
    public String convertToString(Location loc, String splitBy, boolean yawPitch) {
        String world = loc.getWorld().getName();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        
        if (!yawPitch) {
            return world + splitBy + x + splitBy + y + splitBy + z;
        } else return world + splitBy + x + splitBy + y + splitBy + z + splitBy + yaw + splitBy + pitch;
    }
    
    public boolean isInside(Location checkInside, Location corner1, Location corner2) {
        int x1 = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int y1 = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int z1 = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int x2 = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int y2 = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int z2 = Math.max(corner1.getBlockZ(), corner2.getBlockZ());
 
        return checkInside.getBlockX() >= x1 && checkInside.getBlockX() <= x2 && 
               checkInside.getBlockY() >= y1 && checkInside.getBlockY() <= y2 && 
               checkInside.getBlockZ() >= z1 && checkInside.getBlockZ() <= z2;
    }
}
