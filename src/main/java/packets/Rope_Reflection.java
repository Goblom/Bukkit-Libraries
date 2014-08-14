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

package packets;

import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public class Rope_Reflection {
    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    
    private Map<Type, Location> locations = Maps.newHashMap();
    private Map<Type, Object> witherSkulls = Maps.newHashMap();
    private Map<Type, Object> bats = Maps.newHashMap();
        
    private enum Type {
        Start, End;
    }
    
    public Rope_Reflection(Location start, Location end) {
        this(start, end, true);
    }
    
    public Rope_Reflection(Location start, Location end, boolean spawn) {
        this.locations.put(Type.Start, start);
        this.locations.put(Type.End, end);
        
        if (spawn) {
            spawn();
        }
    }
    
    public void setStart(Location loc) {
        this.locations.put(Type.Start, loc);
        spawn();
    }
    
    public void setEnd(Location loc) {
        this.locations.put(Type.End, loc);
        spawn();
    }
    
    public void spawn() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            spawn(player);
        }
    }
    
    public void deSpawn() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            deSpawn(player);
        }
    }

    public void spawn(Player player) {
        makeEntity();
        
        try {
            Class<?> PacketPlayOutSpawnEntity = getNMSClass("PacketPlayOutSpawnEntity");
            Class<?> PacketPlayOutAttachEntity = getNMSClass("PacketPlayOutAttachEntity");
            Class<?> PacketPlayOutSpawnEntityLiving = getNMSClass("PacketPlayOutSpawnEntityLiving");
            Class<?> Entity = getNMSClass("Entity");
            Class<?> EntityLiving = getNMSClass("EntityLiving");
            
            Object skullStart, skullEnd, batStart, batEnd, attachStart, attachEnd;
            
            skullStart = PacketPlayOutSpawnEntity.getConstructor(Entity, int.class).newInstance(getSkull(Type.Start), 66);
            skullEnd = PacketPlayOutSpawnEntity.getConstructor(Entity, int.class).newInstance(getSkull(Type.End), 66);
            batStart = PacketPlayOutSpawnEntityLiving.getConstructor(EntityLiving).newInstance(getBat(Type.Start));
            batEnd = PacketPlayOutSpawnEntityLiving.getConstructor(EntityLiving).newInstance(getBat(Type.End));
            attachStart = PacketPlayOutAttachEntity.getConstructor(int.class, Entity, Entity).newInstance(0, skullStart, batStart);
            attachEnd = PacketPlayOutAttachEntity.getConstructor(int.class, Entity, Entity).newInstance(0, skullEnd, batEnd);
            
            sendPackets(player, skullStart, skullEnd, batStart, batEnd, attachStart, attachEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void deSpawn(Player player) {
        try {
            Class<?> PacketPlayOutEntityDestroy = getNMSClass("PacketPlayOutEntityDestroy");
            int batStartId, batEndId, skullStartId, skullEndId;
            
            batStartId = (Integer) getBat(Type.Start).getClass().getMethod("getId").invoke(getBat(Type.Start));
            batEndId = (Integer) getBat(Type.End).getClass().getMethod("getId").invoke(getBat(Type.End));
            skullStartId = (Integer) getSkull(Type.Start).getClass().getMethod("getId").invoke(getBat(Type.Start));
            skullEndId = (Integer) getSkull(Type.End).getClass().getMethod("getId").invoke(getBat(Type.End));
            
            Object packet = PacketPlayOutEntityDestroy.getConstructor(int[].class).newInstance(new int[] { batStartId, batEndId, skullStartId, skullEndId});
            sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Location getLocation(Type type) {
        return this.locations.get(type);
    }
        
    private Object getSkull(Type type) {
        return this.witherSkulls.get(type);
    }
    
    private Object getBat(Type type) {
        return this.bats.get(type);
    }
    
    private void makeEntity() {
        try {
            Class<?> World = getNMSClass("World");
            Class<?> WorldServer = getNMSClass("WorldServer");
            Class<?> CraftWorld = getCraftClass("CraftWorld");
            Class<?> EntityBat = getNMSClass("EntityBat");
            Class<?> EntityWitherSkull = getNMSClass("EntityWitherSkull");
            
            Object worldStart = getHandle(CraftWorld.cast(getLocation(Type.Start)));
            Object worldEnd = getHandle(CraftWorld.cast(getLocation(Type.End)));
            
            Object witherStart, witherEnd, batStart, batEnd;
            
            witherStart = getSkull(Type.Start);
            witherEnd = getSkull(Type.End);
            batStart = getBat(Type.Start);
            batEnd = getBat(Type.End);
            
            if (witherStart == null) {
                witherStart = EntityWitherSkull.getConstructor(World).newInstance(worldStart);
            }
            
            if (witherEnd == null) {
                witherEnd = EntityWitherSkull.getConstructor(World).newInstance(worldEnd);
            }
            
            if (batStart == null) {
                batStart = EntityBat.getConstructor(World).newInstance(worldStart);
            }
            
            if (batEnd == null) {
                batEnd = EntityBat.getConstructor(World).newInstance(worldEnd);
            }
            
            setLocation(witherStart, getLocation(Type.Start));
            setLocation(witherEnd, getLocation(Type.End));
            setLocation(batStart, getLocation(Type.Start));
            setLocation(batEnd, getLocation(Type.End));
            
            setLeashHolder(batStart, batEnd, true); // try with false
            
            this.witherSkulls.put(Type.Start, witherStart);
            this.witherSkulls.put(Type.End, witherEnd);
            this.bats.put(Type.Start, batStart);
            this.bats.put(Type.End, batEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //**********************************************
    // Reflection based methods...
    //**********************************************
    
    private static void sendPackets(Player player, Object... packets) {
        for (Object packet : packets) {
            sendPacket(player, packet);
        }
    }
    
    private static Class<?> getCraftClass(String name) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit." + VERSION + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return clazz;
    }
    
    private static Class<?> getNMSClass(String name) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("net.minecraft.server." + VERSION + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }
    
    private static Object getHandle(Object obj) {
        try {
            return obj.getClass().getMethod("getHandle").invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static Object getPlayerConnection(Player player) {
        try {
            Object handle = getHandle(player);
            return handle.getClass().getField("playerConnection").get(handle);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    private static void setLeashHolder(Object EntityInsentient, Object Entity, boolean xxx) {
        try {
            Class<?> EntityClass = getNMSClass("Entity");
            Method setLeashHolder = EntityInsentient.getClass().getMethod("setLeashHolder", new Class[] { EntityClass, boolean.class });
                   setLeashHolder.invoke(EntityInsentient, new Object[] { Entity, xxx });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void setLocation(Object entity, Location loc) {
        try {
            Method setLocation = entity.getClass().getMethod("setLocation", new Class<?>[] { double.class, double.class, double.class, float.class, float.class });
                   setLocation.invoke(entity, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void sendPacket(Player player, Object packet) {
        try {
            Class<?> Packet = getNMSClass("Packet");
            Object playerConnection = getPlayerConnection(player);
            playerConnection.getClass().getMethod("sendPacket", Packet).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
