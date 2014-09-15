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
package utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

/**
 * heavily based off of bukkits /spreadplayers command. Only difference is that we load chunk before teleportation and also make sure the player spawns in a AIR block.
 */
public class SpreadPlayers implements Runnable {
    private static final Random random = new Random();
    
    private final List<Player> players;
    private double x, z, spreadDistance, maxRange;
    private boolean respectTeams;
    
    public SpreadPlayers(List<Player> players) {
        this.players = players;
    }
    
    public SpreadPlayers setWillRespectTeams(boolean respectTeams) {
        this.respectTeams = respectTeams;
        return this;
    }
    
    public SpreadPlayers setX(double x) {
        this.x = getDouble(x, -30000000, 30000000);
        return this;
    }
    
    public SpreadPlayers setZ(double z) {
        this.z = getDouble(z, -30000000, 30000000);
        return this;
    }
    
    public SpreadPlayers setSpreadDistance(double spreadDistance) {
        this.spreadDistance = spreadDistance;
        return this;
    }
    
    public SpreadPlayers setMaxRange(double maxRange) {
        this.maxRange = maxRange;
        return this;
    }
    
    public void run() {        
        if (spreadDistance < 0.0D) {
            throw new RuntimeException("Distance is too small.");
        }
        
        if (maxRange < spreadDistance + 1.0D) {
            throw new RuntimeException("Max range is too small.");
        }
        
        if (players.isEmpty()) {
            throw new RuntimeException("There are no players to spread.");
        }
        
        World world = players.get(0).getLocation().getWorld();
        
        final double xRangeMin = x - maxRange;
        final double zRangeMin = z - maxRange;
        final double xRangeMax = x + maxRange;
        final double zRangeMax = z + maxRange;
        
        final int spreadSize = respectTeams ? getTeams(players) : players.size();

        final Location[] locations = getSpreadLocations(world, spreadSize, xRangeMin, zRangeMin, xRangeMax, zRangeMax);
        final int rangeSpread = range(world, spreadDistance, xRangeMin, zRangeMin, xRangeMax, zRangeMax, locations);

        if (rangeSpread == -1) {
            throw new RuntimeException(String.format("Could not spread %d %s around %s,%s (too many players for space - try using spread of at most %s)", spreadSize, respectTeams ? "teams" : "players", x, z));
        }

        spread(world, players, locations, respectTeams);        
    }
    
    private int range(World world, double distance, double xRangeMin, double zRangeMin, double xRangeMax, double zRangeMax, Location[] locations) {
        boolean flag = true;
        double max;

        int i;

        for (i = 0; i < 10000 && flag; ++i) {
            flag = false;
            max = Float.MAX_VALUE;

            Location loc1;
            int j;

            for (int k = 0; k < locations.length; ++k) {
                Location loc2 = locations[k];

                j = 0;
                loc1 = new Location(world, 0, 0, 0);

                for (int l = 0; l < locations.length; ++l) {
                    if (k != l) {
                        Location loc3 = locations[l];
                        double dis = loc2.distanceSquared(loc3);

                        max = Math.min(dis, max);
                        if (dis < distance) {
                            ++j;
                            loc1.add(loc3.getX() - loc2.getX(), 0, 0);
                            loc1.add(loc3.getZ() - loc2.getZ(), 0, 0);
                        }
                    }
                }

                if (j > 0) {
                    loc2.setX(loc2.getX() / j);
                    loc2.setZ(loc2.getZ() / j);
                    double d7 = Math.sqrt(loc1.getX() * loc1.getX() + loc1.getZ() * loc1.getZ());

                    if (d7 > 0.0D) {
                        loc1.setX(loc1.getX() / d7);
                        loc2.add(-loc1.getX(), 0, -loc1.getZ());
                    } else {
                        double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
                        double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
                        loc2.setX(x);
                        loc2.setZ(z);
                    }

                    flag = true;
                }

                boolean swap = false;

                if (loc2.getX() < xRangeMin) {
                    loc2.setX(xRangeMin);
                    swap = true;
                } else if (loc2.getX() > xRangeMax) {
                    loc2.setX(xRangeMax);
                    swap = true;
                }

                if (loc2.getZ() < zRangeMin) {
                    loc2.setZ(zRangeMin);
                    swap = true;
                } else if (loc2.getZ() > zRangeMax) {
                    loc2.setZ(zRangeMax);
                    swap = true;
                }
                if (swap) {
                    flag = true;
                }
            }

            if (!flag) {
                Location[] locs = locations;
                int i1 = locations.length;

                for (j = 0; j < i1; ++j) {
                    loc1 = locs[j];
                    if (world.getHighestBlockYAt(loc1) == 0) {
                        double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
                        double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
                        locations[i] = (new Location(world, x, 0, z));
                        loc1.setX(x);
                        loc1.setZ(z);
                        flag = true;
                    }
                }
            }
        }

        if (i >= 10000) {
            return -1;
        } else {
            return i;
        }
    }
    
    private double spread(World world, List<Player> list, Location[] locations, boolean teams) {
        double distance = 0.0D;
        int i = 0;
        Map<Team, Location> hashmap = Maps.newHashMap();

        for (int j = 0; j < list.size(); ++j) {
            Player player = list.get(j);
            Location location;

            if (teams) {
                Team team = player.getScoreboard().getPlayerTeam(player);

                if (!hashmap.containsKey(team)) {
                    hashmap.put(team, locations[i++]);
                }

                location = hashmap.get(team);
            } else {
                location = locations[i++];
            }

            player.teleport(new Location(world, Math.floor(location.getX()) + 0.5D, world.getHighestBlockYAt((int) location.getX(), (int) location.getZ()), Math.floor(location.getZ()) + 0.5D));
            double value = Double.MAX_VALUE;

            for (int k = 0; k < locations.length; ++k) {
                if (location != locations[k]) {
                    double d = location.distanceSquared(locations[k]);
                    value = Math.min(d, value);
                }
            }

            distance += value;
        }

        distance /= list.size();
        return distance;
    }
    
    private int getTeams(List<Player> players) {
        Set<Team> teams = Sets.newHashSet();
        
        for (Player player : players) {
            teams.add(player.getScoreboard().getPlayerTeam(player));
        }
        
        return teams.size();
    }
    
    private Location[] getSpreadLocations(World world, int size, double xRangeMin, double zRangeMin, double xRangeMax, double zRangeMax) {
        Location[] locations = new Location[size];

        for (int i = 0; i < size; ++i) {
            double x = xRangeMin >= xRangeMax ? xRangeMin : random.nextDouble() * (xRangeMax - xRangeMin) + xRangeMin;
            double z = zRangeMin >= zRangeMax ? zRangeMin : random.nextDouble() * (zRangeMax - zRangeMin) + zRangeMin;
            Location loc = new Location(world, x, 0, z);
            
            //special start
            while (loc.getBlock().getType() != Material.AIR) {
                loc = loc.add(0, 1, 0);
            }
            
            if (!loc.getChunk().isLoaded()) {
                loc.getChunk().load();
            }
            //special end
            
            locations[i] = loc;
        }

        return locations;
    }
    
    private static double getDouble(double result, double min, double max) {
        if (result < min) {
            result = min;
        } else if (result > max) {
            result = max;
        }

        return result;
    }
}
