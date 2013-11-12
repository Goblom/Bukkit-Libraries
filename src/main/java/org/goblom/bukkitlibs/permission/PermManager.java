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
package org.goblom.bukkitlibs.permission;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Permissions Manager v0.1
 * 
 * Easily Create & Manage Permissions; not string permissions, real permissions. 
 * 
 * @warning EXPERIMENTAL CLASS SO YOU ARE WARNED IF SOMETHING GOES WRONG
 * @author Goblom
 */
public class PermManager {

    public static Map<String, Permission> permissions = new HashMap();
    
    public static void createPerm(String permTitle, String perm) {
        Permission permission = new Permission(perm);
        permissions.put(permTitle, permission);
    }

    public static void createPerm(String permTitle, String name, PermissionDefault defaultValue) {
        Permission permission = new Permission(name, defaultValue);
        permissions.put(permTitle, permission);
    }

    public static void createPerm(String permTitle, String name, String description, PermissionDefault defaultValue) {
        Permission permission = new Permission(name, description, defaultValue);
        permissions.put(permTitle, permission);
    }

    public static void createPerm(String permTitle, String name, Map<String, Boolean> children) {
        Permission permission = new Permission(name, null, null, children);
        permissions.put(permTitle, permission);
    }

    public static void createPerm(String permTitle, String name, String description, Map<String, Boolean> children) {
        Permission permission = new Permission(name, description, children);
        permissions.put(permTitle, permission);
    }

    public static void createPerm(String permTitle, String name, PermissionDefault defaultValue, Map<String, Boolean> children) {
        Permission permission = new Permission(name, defaultValue, children);
        permissions.put(permTitle, permission);
    }

    public static void createPerm(String permTitle, String name, String description, PermissionDefault defaultValue, Map<String, Boolean> children) {
        Permission permission = new Permission(name, description, defaultValue, children);
        permissions.put(permTitle, permission);
    }
    
    public static boolean containsPermission(String permTitle) {
        return permissions.containsKey(permTitle);
    }
    
    public Permission getPermission(String permTitle) {
        return permissions.get(permTitle);
    }
    
    public static boolean isAuthorized(CommandSender sender, String perm) {
        return sender.hasPermission(perm);
    }   
    
    public static boolean isAuthorized(CommandSender sender, Permission perm) {
        return sender.hasPermission(perm);
    }
    
    public static boolean isAuthorized(Player player, String perm) {
        return player.hasPermission(perm);
    }
    
    public static boolean isauthorized(Player player, Permission perm) {
        return player.hasPermission(perm);
    }
}