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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

/**
 * Player Manager v0.1
 * 
 * I do not know what i am going to turn this into.
 * 
 * @author Goblom
 */
public class PlayerManager {

    private Plugin plugin;
    private Map<String, Player> players = new HashMap();

    public PlayerManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public Player getPlayer(String playerName) {
        return players.get(playerName);
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public Player addPlayer(String playerName) {
        Player player = new Player(playerName);
        return players.put(playerName, player);
    }

    public void remPlayer(String playerName) {
        getPlayer(playerName).remove();
    }

    protected class Player implements Listener {

        protected String playerName;
        protected PlayerInventory playerInventory;

        protected List<String> permissions = new ArrayList<String>();
        protected Map<Long, Location> pathTravelled = new HashMap();

        protected Location start;

        public Player(String playerName) {
            this(playerName, null, null);
        }

        public Player(String playerName, PlayerInventory inv) {
            this(playerName, inv, null);
        }

        public Player(String playerName, Location start) {
            this(playerName, null, start);
        }

        public Player(String playerName, PlayerInventory inv, Location start) {
            this.playerName = playerName;
            this.playerInventory = inv;
            this.start = start;
            
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }

        public void remove() {
            HandlerList.unregisterAll(this);
            this.playerName = null;
            this.permissions = null;
            this.pathTravelled = null;
            this.playerInventory = null;
            this.start = null;
        }

        public String getName() {
            return playerName;
        }

        public PlayerInventory getInventory() {
            return playerInventory;
        }

        public Location getStart() {
            return start;
        }

        public List<String> getPermissions() {
            return permissions;
        }

        public Map<Long, Location> getPathTravelled() {
            return pathTravelled;
        }

        public Player setInventory(PlayerInventory inv) {
            this.playerInventory = inv;
            return this;
        }

        public Player setStart(Location loc) {
            this.start = loc;
            return this;
        }

        public Player setPermission(List<String> perms) {
            this.permissions = perms;
            return this;
        }

        public Player setPathTravelled(Map<Long, Location> travelled) {
            this.pathTravelled = travelled;
            return this;
        }

        public void addPathTravelled(Location loc) {
            pathTravelled.put(System.currentTimeMillis(), loc);
        }

        public void addPermission(String perm) {
            if (!permissions.contains(perm)) {
                permissions.add(perm);
            }
        }

        public String toString() {
            return "{PlayerManager("
                    + "playerName=" + getName() + ", "
                    + "inv=" + getInventory() + ", "
                    + "startTime=" + getStart() + ", "
                    + "permissions=" + getPermissions() + ", "
                    + "pathTravelled=" + getPathTravelled()
                    + ")}";
        }
    }
}
