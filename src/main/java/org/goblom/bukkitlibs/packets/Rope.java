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

package org.goblom.bukkitlibs.packets;

import net.minecraft.server.v1_7_R3.EntityBat;
import net.minecraft.server.v1_7_R3.EntityWitherSkull;
import net.minecraft.server.v1_7_R3.PacketPlayOutAttachEntity;
import net.minecraft.server.v1_7_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_7_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_7_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Credits to @Desile for the original resource. (Uses entities, not packets)
 *
 * @author Goblom
 */
public class Rope {
 
    private Location loc_start, loc_end;
    private EntityWitherSkull ent_start, ent_end;
    private EntityBat ent_bat_start, ent_bat_end;
 
    public Rope(Location start, Location end) {
        this(start, end, true);
    }
 
    public Rope(Location start, Location end, boolean spawn) {
        this.loc_start = start;
        this.loc_end = end;
        
        if (spawn) {
            spawn();
        }
    }
    
    public void setStart(Location start) {
        this.loc_start = start;
        spawn();
    }
 
    public void setEnd(Location end) {
        this.loc_end = end;
        spawn();
    }
 
    public Location getStart() {
        return loc_start;
    }
 
    public Location getEnd() {
        return loc_end;
    }
 
    private void makeEnt() {
        WorldServer world_start = ((CraftWorld) loc_start.getWorld()).getHandle();
        WorldServer world_end = ((CraftWorld) loc_end.getWorld()).getHandle();
 
        if (ent_start == null) {
            this.ent_start = new EntityWitherSkull(world_start);
        }
 
        if (ent_end == null) {
            this.ent_end = new EntityWitherSkull(world_end);
        }
 
        if (ent_bat_start == null) {
            this.ent_bat_start = new EntityBat(world_start);
        }
 
        if (ent_bat_end == null) {
            this.ent_bat_end = new EntityBat(world_end);
        }
        
        this.ent_start.setLocation(loc_start.getX(), loc_start.getY(), loc_start.getZ(), 0, 0);
        this.ent_end.setLocation(loc_end.getX(), loc_end.getY(), loc_end.getZ(), 0, 0);
        this.ent_bat_start.setLocation(loc_start.getX(), loc_start.getY(), loc_start.getZ(), 0, 0);
        this.ent_bat_end.setLocation(loc_end.getX(), loc_end.getY(), loc_end.getZ(), 0, 0);
        
        this.ent_bat_start.setLeashHolder(ent_bat_end, true); //Try with false
    }
 
    public void spawn() {
        makeEnt();
        PacketPlayOutSpawnEntity skull_start = new PacketPlayOutSpawnEntity(ent_start, 66);
        PacketPlayOutSpawnEntity skull_end = new PacketPlayOutSpawnEntity(ent_end, 66);
        PacketPlayOutSpawnEntityLiving bat_start = new PacketPlayOutSpawnEntityLiving(ent_bat_start);
        PacketPlayOutSpawnEntityLiving bat_end = new PacketPlayOutSpawnEntityLiving(ent_bat_start);
        PacketPlayOutAttachEntity attach_start = new PacketPlayOutAttachEntity(0, ent_start, ent_bat_start);
        PacketPlayOutAttachEntity attach_end = new PacketPlayOutAttachEntity(0, ent_end, ent_bat_end);
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawn(player, skull_start, skull_end, bat_start, bat_end, attach_start, attach_end);
        }
    }
    
    protected void spawn(Player player, PacketPlayOutSpawnEntity skull_start, PacketPlayOutSpawnEntity skull_end, PacketPlayOutSpawnEntityLiving bat_start, PacketPlayOutSpawnEntityLiving bat_end, PacketPlayOutAttachEntity attach_start, PacketPlayOutAttachEntity attach_end) {
        CraftPlayer cP = (CraftPlayer) player;
        
        cP.getHandle().playerConnection.sendPacket(skull_start);
        cP.getHandle().playerConnection.sendPacket(skull_end);
        cP.getHandle().playerConnection.sendPacket(bat_end);
        cP.getHandle().playerConnection.sendPacket(bat_start);
        cP.getHandle().playerConnection.sendPacket(attach_start);
        cP.getHandle().playerConnection.sendPacket(attach_end);
    }
    
    public void spawn(Player player) {
        makeEnt();
        PacketPlayOutSpawnEntity skull_start = new PacketPlayOutSpawnEntity(ent_start, 66);
        PacketPlayOutSpawnEntity skull_end = new PacketPlayOutSpawnEntity(ent_end, 66);
        PacketPlayOutSpawnEntityLiving bat_start = new PacketPlayOutSpawnEntityLiving(ent_bat_start);
        PacketPlayOutSpawnEntityLiving bat_end = new PacketPlayOutSpawnEntityLiving(ent_bat_start);
        PacketPlayOutAttachEntity attach_start = new PacketPlayOutAttachEntity(0, ent_start, ent_bat_start);
        PacketPlayOutAttachEntity attach_end = new PacketPlayOutAttachEntity(0, ent_end, ent_bat_end);
        
        spawn(player, skull_start, skull_end, bat_start, bat_end, attach_start, attach_end);
    }
    
    public void deSpawn() {
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(new int[] { ent_start.getId(), ent_end.getId(), ent_bat_start.getId(), ent_bat_end.getId() });
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            deSpawn(player, destroy);
        }
    }
    
    public void deSpawn(Player player) {
        deSpawn(player, new PacketPlayOutEntityDestroy(new int[] { ent_start.getId(), ent_end.getId(), ent_bat_start.getId(), ent_bat_end.getId() }));
    }
    
    protected void deSpawn(Player player, PacketPlayOutEntityDestroy destroy) {
        CraftPlayer cP = (CraftPlayer) player;
        cP.getHandle().playerConnection.sendPacket(destroy);
    }
}
