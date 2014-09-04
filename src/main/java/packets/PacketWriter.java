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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Goblom
 */
public class PacketWriter {
    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    
    public static class PacketNotFoundException extends ClassNotFoundException {
        private PacketNotFoundException(String string) {
            super(string);
        }
    }
    
    public static class InvalidPacketException extends Exception {
        private InvalidPacketException(String str) {
            super(str);
        }
    }
    
    private final Class<?> packetClass;
    private final Object packetObject;
    
    public PacketWriter(String packet) throws PacketNotFoundException, InvalidPacketException {        
        this.packetClass = getNMSClass(packet);
        
        if (this.packetClass == null) {
            throw new PacketNotFoundException(packet + " was unable to be found.");
        }
        
        Object obj = null;
        try {
            obj = packetClass.newInstance();
        } catch (Exception e) { }
        
        if (obj == null) {
            throw new InvalidPacketException("Packet was unable to be initialized");
        }
        
        this.packetObject = obj;
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
    
    private static Field getField(Class<?> clazz, String name) {
        Field field = null;
        
        try {
            field = clazz.getField(name);
        } catch (Exception e) { }
        
        if (field == null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception e) { }
        }
        
        if (field != null) {
            field.setAccessible(true);
        }
        
        return field;
    }
    
    public PacketWriter write(String field, Object value) {
        Field f = getField(packetClass, field);

        if (f == null) {
            throw new RuntimeException("Field " + field + " was not found for " + packetClass.getSimpleName());
        }

        if (!f.getType().equals(value.getClass())) {
            throw new RuntimeException("Field " + field + " uses a different object value");
        }
        
        try {
            f.set(packetObject, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Field " + field + " is not accessible.", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        
        return this;
    }
    
    public void send(Player... players) {
        Class<?> packetClass = getNMSClass("Packet");
        if (packetClass != null) {
            for (Player player : players) {
                try {
                    Object handle = player.getClass().getMethod("getHandle").invoke(player);
                    Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

                    playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packetObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
