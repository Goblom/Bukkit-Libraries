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
import org.bukkit.scheduler.BukkitTask;

import org.goblom.bukkitlibs.game.ArenaManager.*; //Import Myself ?
import org.goblom.bukkitlibs.game.ArenaManager.ArenaData.*; //Import Myself?

/**
 * Arena Manager v0.1
 *
 * Easily Create, Manager, Edit & Delete Arenas for any game that you have in
 * mind. Arena Manager comes with Teams, Inventory saving/deleting/restoring and
 * custom equipment for teams.
 *
 * @TODO - Implement Min/Max Players
 * 
 * @author Goblom
 */
public class ArenaManager {

    private Plugin plugin;
    
    private Map<String, Arena> arenas = new HashMap();
    private Map<String, Team> teams = new HashMap();
    private Map<Arena, BukkitTask> arenaTimers = new HashMap();
    
    public ArenaManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }

    public Arena getArena(String arenaName) {
        return arenas.get(arenaName);
    }
    
    public Arena getArena(Arena arena) {
        return arenas.get(arena.getName());
    }

    public Arena createArena(String arenaName) {
        return arenas.put(arenaName, new Arena(arenaName));
    }
    
    public Arena createArena(String arenaName, int maxPlayers) {
        return arenas.put(arenaName, new Arena(arenaName, maxPlayers));
    }
    
    public Arena createArena(String arenaName, int minPlayers, int maxPlayers) {
        return arenas.put(arenaName, new Arena(arenaName, minPlayers, maxPlayers));
    }
    
    public Arena createArena(String arenaName, ArenaHandler handler) {
        return arenas.put(arenaName, new Arena(arenaName, handler));
    }

    public Arena createArena(String arenaName, int minPlayers, int maxPlayers, ArenaHandler handler) {
        return arenas.put(arenaName, new Arena(arenaName, minPlayers, maxPlayers, handler));
    }

    public Map<String, Team> getTeams() {
        return teams;
    }
    
    public Team createTeam(String teamName, Arena arena, Location spawnLocation) {
        return teams.put(teamName, new Team(teamName, arena, spawnLocation));
    }
    
    public class Arena implements Listener {

        private String arenaName;
        private int minPlayers, maxPlayers;
        private ArenaPhase currentPhase;

        private ArenaHandler handler;
        private List<String> players = new ArrayList<String>();
        private List<Team> teams = new ArrayList<Team>();

        private Map<String, PlayerInventory> playerInv = new HashMap();

        private Map<LocationType, String> locationNames = new HashMap();
        private Map<String, Location> locations = new HashMap();
        private List<String> locationsUsed = new ArrayList<String>();

        private int timer;
        private BukkitTask timerTask;
        
        public Arena(String arenaName) {
            this(arenaName, 1, 1, null);
        }

        public Arena(String arenaName, int maxPlayers) {
            this(arenaName, 1, maxPlayers, null);
        }

        public Arena(String arenaName, int minPlayers, int maxPlayers) {
            this(arenaName, minPlayers, maxPlayers, null);
        }
        
        public Arena(String arenaName, ArenaHandler handler) {
            this(arenaName, 1, 1, handler);
        }
        
        public Arena(String arenaName, int minPlayers, int maxPlayers, ArenaHandler handler) {
            craftEvent(ArenaPhase.CREATE, null);
            preventItemLoss();
            
            this.arenaName = arenaName;
            this.maxPlayers = maxPlayers;
            this.minPlayers = minPlayers;

            this.handler = handler;
            
            load();
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
            this.timerTask = null;
        }
        
        protected final void load() {
            craftEvent(ArenaPhase.LOAD, null);
            handler.onLoad();
        }
        
        public void start() {
            craftEvent(ArenaPhase.GAME_START, currentPhase);
            handler.start();
        }
        public void end() {
            craftEvent(ArenaPhase.GAME_END, currentPhase);
            handler.end();
        }
        
        public String getName() {
            load();
            return arenaName;
        }

        public List<String> getPlayerList() {
            load();
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

        public Map<LocationType, String> getLocationNames() {
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

        public void addLocation(LocationType locType, String locName, Location loc) {
            locationNames.put(locType, locName);
            locations.put(locName, loc);
        }

        public boolean remLocation(LocationType locType, String locName) {
            if (locationNames.containsKey(locType)) {
                for (LocationType locTypes : locationNames.keySet()) {
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
        
        public int getTimer() {
            return timer;
        }
        
        public void setTimer(int time) {
            this.timer = time;
        }
        
        public boolean startTimer() {
            if (timer == 0) return false;
            if (timerTask != null) return false;
            else if (timerTask == null) {
                BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, new Runnable() { 
                    public void run() {
                        handler.end();
                    }
                }, timer);
                
                if (task != null) {
                    arenaTimers.put(this, task);
                    return true;
                }
            }
            return false;
        }
        
        public boolean endTimer() {
            if (timerTask != null) {
                timerTask.cancel();
                return true;
            }
            return false;
        }
        
        protected final void craftEvent(ArenaPhase changedToPhase, ArenaPhase changedFromPhase) {
            this.currentPhase = changedToPhase;
            ArenaChangePhaseEvent acpe = new ArenaChangePhaseEvent(this, changedToPhase, changedFromPhase);
            handler.onArenaPhaseChange(acpe);
        }
        
        @EventHandler
        void onFriendlyFire(EntityDamageByEntityEvent event) {
            if (currentPhase.equals(ArenaPhase.GAME_START)) {
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
        
        private final void preventItemLoss() {
            Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() { 
                public void run() {
                    for (Team team : teams) {
                        for (String playerName : team.getPlayers()) {
                            Player player = Bukkit.getPlayer(playerName);
                            if (player != null) {
                                PlayerInventory pi = player.getInventory();
                                if (playerInv.containsKey(playerName)) playerInv.remove(playerName);
                                playerInv.put(playerName, pi);
                            }
                        }
                    }
                }
            }, 0L, 5);
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
        private final Arena arena;
        private final ArenaPhase currentPhase;
        private final ArenaPhase previousPhase;
        
        public ArenaChangePhaseEvent(Arena arena, ArenaPhase changedToPhase, ArenaPhase changedFromPhase) {
            this.arena = arena;
            this.currentPhase = changedToPhase;
            this.previousPhase = changedFromPhase;
        }
        
        public Arena getArena() {
            return arena;
        }
        
        public ArenaPhase getCurrentPhase() {
            return currentPhase;
        }
        
        public ArenaPhase getPreviousPhase() {
            return previousPhase;
        }
    }
    
    public static class ArenaData {

        public static enum LocationType {

            END,
            OTHER
        }

        public static enum ArenaPhase {

            CREATE,
            LOAD,
            GAME_START,
            GAME_END
        }
        
        public interface ArenaHandlerInterface {
            
            void onArenaPhaseChange(ArenaChangePhaseEvent event);
            
            void onLoad();
            void start();
            void end();
        }
    }
    
    public abstract class ArenaHandler implements ArenaHandlerInterface {
        private Arena arena;
        
        public ArenaHandler(Arena arena) {
            this.arena = arena;
        }
        
        public abstract void onLoad();
        public abstract void start();
        public abstract void end();
    }
}
