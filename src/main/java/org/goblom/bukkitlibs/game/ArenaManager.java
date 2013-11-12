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
package org.goblom.bukkitlibs.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

/**
 * Arena Manager v0.1
 *
 * Easily Create, Manager, Edit & Delete Arenas for any game that you have in
 * mind. Arena Manager comes with Teams, Inventory saving/deleting/restoring and
 * custom equipment for teams.
 *
 * @author Goblom
 */
public class ArenaManager {

    private Plugin plugin;
    private Map<String, Arena> arenas = new HashMap();
    private Map<String, Team> teams = new HashMap();

    public ArenaManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }

    public Arena getArena(String arenaName) {
        return arenas.get(arenaName);
    }

    public Arena createArena(String arenaName) {
        Arena arena = new Arena(arenaName);
        return arenas.put(arenaName, arena);
    }

    public class Arena implements Listener {

        private String arenaName;
        private int minPlayers, maxPlayers;
        private ArenaData.ArenaPhase currentPhase;

        private ArenaHandler handler;
        private List<String> players = new ArrayList<String>();
        private List<Team> teams = new ArrayList<Team>();

        private Map<String, PlayerInventory> playerInv = new HashMap();

        private Map<ArenaData.LocationType, String> locationNames = new HashMap();
        private Map<String, Location> locations = new HashMap();
        private List<String> locationsUsed = new ArrayList<String>();

        public Arena(String arenaName) {
            this(arenaName, 1);
        }

        public Arena(String arenaName, int maxPlayers) {
            this(arenaName, 1, maxPlayers, null);
        }

        public Arena(String arenaName, ArenaHandler handler) {
            this(arenaName, 1, 1, handler);
        }
        
        public Arena(String arenaName, int minPlayers, int maxPlayers, ArenaHandler handler) {
            this.currentPhase = ArenaData.ArenaPhase.SETUP;
            this.arenaName = arenaName;
            this.maxPlayers = maxPlayers;
            this.minPlayers = minPlayers;

            this.handler = handler;
            
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }

        public void delete() {
            HandlerList.unregisterAll(this);
            this.currentPhase = null;
            this.arenaName = null;
            this.playerInv = null;
            this.players = null;
            this.teams = null;
            this.locationNames = null;
            this.locations = null;
            this.locationsUsed = null;
            this.handler = null;
        }

        public String getName() {
            return arenaName;
        }

        public List<String> getPlayerList() {
            return players;
        }

        public List<Player> getPlayersAsPlayers() {
            List<Player> stringAsPlayers = new ArrayList<Player>();
            for (String playerString : getPlayerList()) {
                Player player = Bukkit.getPlayer(playerString);
                if (player != null) {
                    stringAsPlayers.add(player);
                }
            }
            return stringAsPlayers;
        }

        public int getMinPlayers() {
            return minPlayers;
        }

        public int getMaxPlayers() {
            return maxPlayers;
        }

        public Map<ArenaData.LocationType, String> getLocationNames() {
            return locationNames;
        }

        public Map<String, Location> getLocations() {
            return locations;
        }

        public List<String> getLocationsUsed() {
            return locationsUsed;
        }

        public Map<String, PlayerInventory> getPlayerInventory() {
            return playerInv;
        }

        public void addPlayer(String playerName) {
            players.add(playerName);
        }

        public void remPlayer(String playerName) {
            if (players.contains(playerName)) {
                players.remove(playerName);
            }
        }

        public void addLocation(ArenaData.LocationType locType, String locName, Location loc) {
            locationNames.put(locType, locName);
            locations.put(locName, loc);
        }

        public boolean remLocation(ArenaData.LocationType locType, String locName) {
            if (locationNames.containsKey(locType)) {
                for (ArenaData.LocationType locTypes : locationNames.keySet()) {
                    if (locTypes.equals(locType)) {
                        String keyName = locationNames.get(locType);
                        if (keyName.equals(locName) && locations.containsKey(locName)) {
                            locationNames.remove(locType);
                            locations.remove(locName);
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public void setMaxPlayers(int maxPlayers) {
            this.maxPlayers = maxPlayers;
        }

        public void setMinPlayers(int minPlayers) {
            this.minPlayers = minPlayers;
        }

        public List<Team> getTeams() {
            return teams;
        }

        public Arena addTeam(Team team) {
            if (!teams.contains(team)) {
                teams.add(team);
            }
            return this;
        }

        public Arena remTeam(Team team) {
            if (teams.contains(team)) {
                teams.remove(team);
            }
            return this;
        }

        public ArenaHandler getHandler() {
            return handler;
        }
        
        public void setHandler(ArenaHandler handler) {
            this.handler = handler;
        }
        
        @EventHandler
        void onFriendlyFire(EntityDamageByEntityEvent event) {
            if ((currentPhase.equals(ArenaData.ArenaPhase.GAME_START)) || (currentPhase.equals(ArenaData.ArenaPhase.IN_SESSION))) {
                Entity r = event.getDamager();
                Entity d = event.getEntity();

                if ((d instanceof Player) && (r instanceof Player)) {
                    Player damager = (Player) r;
                    Player damaged = (Player) d;

                    for (Team team : teams) {
                        if ((team.hasPlayer(damager.getName())) && team.hasPlayer(damaged.getName())) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    public class Team {

        private String teamName;
        private boolean friendlyFire = false;

        private List<String> players = new ArrayList<String>();

        private List<Arena> arenasForTeam = new ArrayList<Arena>();
        private Map<Arena, Location> arenaSpawn = new HashMap();

        public Team(String teamName, Arena arena, Location arenaSpawn) {
            this.arenasForTeam.add(arena);
            this.teamName = teamName;
            this.arenaSpawn.put(arena, arenaSpawn);
        }

        public void remove() {
            this.arenaSpawn = null;
            this.teamName = null;
            this.arenasForTeam = null;
            this.players = null;
        }

        public String getName() {
            return teamName;
        }

        public boolean getFriendlyFire() {
            return friendlyFire;
        }

        public List<String> getPlayers() {
            return players;
        }

        public List<Arena> getArenasForTeam() {
            return arenasForTeam;
        }

        public Map<Arena, Location> getArenaSpawnsForTeam() {
            return arenaSpawn;
        }

        public Location getSpawnForArena(Arena arena) {
            return arenaSpawn.get(arena);
        }

        public void setFriendlyFire(boolean friendlyFire) {
            this.friendlyFire = friendlyFire;
        }

        public boolean setSpawnForArena(Arena arena, Location loc) {
            if (!arenaSpawn.containsKey(arena)) {
                arenaSpawn.put(arena, loc);
                return true;
            }
            return false;
        }

        public boolean remSpawnForArena(Arena arena) {
            if (arenaSpawn.containsKey(arena)) {
                arenaSpawn.remove(arena);
                return true;
            }
            return false;
        }

        public void addPlayers(List<String> players) {
            this.players = players;
        }

        public boolean addPlayer(String playerName) {
            if (!players.contains(playerName)) {
                players.add(playerName);
                return true;
            }
            return false;
        }

        public boolean hasPlayer(String playerName) {
            return players.contains(playerName);
        }

        public boolean remPlayer(String playerName) {
            if (players.contains(playerName)) {
                players.remove(playerName);
                return true;
            }
            return false;
        }

        public boolean addArena(Arena arena) {
            if (!arenasForTeam.contains(arena)) {
                arenasForTeam.add(arena);
                return true;
            } else {
                return false;
            }
        }

        public boolean remArena(Arena arena) {
            if (arenasForTeam.contains(arena)) {
                arenasForTeam.remove(arena);
                return true;
            }
            return false;
        }
    }

    public static class ArenaChangePhaseEvent {
        private static Arena arena;
        private static ArenaData.ArenaPhase currentPhase;
        private static ArenaData.ArenaPhase previousPhase;
        
        public ArenaChangePhaseEvent(Arena arena, ArenaData.ArenaPhase changedToPhase, ArenaData.ArenaPhase changedFromPhase) {
            this.arena = arena;
            this.currentPhase = changedToPhase;
            this.previousPhase = changedFromPhase;
        }
        
        public Arena getArena() {
            return arena;
        }
        
        public ArenaData.ArenaPhase getCurrentPhase() {
            return currentPhase;
        }
        
        public ArenaData.ArenaPhase getPreviousPhase() {
            return previousPhase;
        }
    }
    
    public static class ArenaData {

        public static enum LocationType {

            END,
            OTHER
        }

        public static enum ArenaPhase {

            SETUP,
            PRE_GAME,
            GAME_START,
            IN_SESSION,
            END_GAME,
            POST_GAME
        }
        
        public interface ArenaHandlerInterface {
            
            void onArenaPhaseChange(ArenaChangePhaseEvent event);
            
            void onLoad();
            void start();
            void end();
        }
    }
    
    public abstract class ArenaHandler implements ArenaData.ArenaHandlerInterface {
        private Arena arena;
        
        public ArenaHandler(Arena arena) {
            this.arena = arena;
        }
        
        public abstract void onLoad();
        public abstract void start();
        public abstract void end();
    }
}
