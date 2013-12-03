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

import org.bukkit.Bukkit;

/**
 * Reflection Utils for getting version dependent Classes... Currently a work in
 * progress
 *
 * @author Goblom
 */
public class ReflectionUtil {

    public static boolean doesClassExist(ClassType type, String name) {
        Class<?> clazz;

        try {
            clazz = Class.forName(type.getPackage() + "." + name);
        } catch (ClassNotFoundException e) { clazz = null; }

        return (clazz != null);
    }

    public static Class<?> getClass(ClassType type, String name) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(type.getPackage() + "." + name);
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return clazz;
    }

    public static Class<?> getCraftClass(String name) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("org.bukkit.craftbukkit." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return clazz;
    }

    public static Class<?> getNMSClass(String name) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("net.minecraft.server." + getVersion() + "." + name);
        } catch (ClassNotFoundException e) { e.printStackTrace(); }
        return clazz;
    }

    private static String getVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    }

    public enum ClassType {

        NMS("net.minecraft.server", getVersion()),
        CRAFT("org.bukkit.craftbukkit", getVersion());

        private final String pakage, version;

        private ClassType(String pakage, String version) {
            this.pakage = pakage;
            this.version = version;
        }

        private String getPackage() {
            return pakage + "." + version;
        }
    }
}
