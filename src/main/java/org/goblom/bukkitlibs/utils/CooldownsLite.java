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

package org.goblom.bukkitlibs.utils;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Goblom
 */
public class CooldownsLite {
    
    private static final JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(CooldownsLite.class);
    private static final Map<UUID, Map<String, Cooldown>> cooldown_holder = Maps.newHashMap();
    
    public static Cooldown prepare(Player player, String cooldownName, int seconds) {
        Map<String, Cooldown> player_cooldowns = (cooldown_holder.get(player.getUniqueId()) == null ? Maps.<String, Cooldown>newHashMap() : cooldown_holder.get(player.getUniqueId()));
        
        Cooldown cooldown = new Cooldown();
                 cooldown.secondsLeft = seconds;
                 cooldown.uuid = player.getUniqueId();
                 cooldown.name = cooldownName;
                 cooldown.started = false;
                 
        player_cooldowns.put(cooldownName, cooldown);
        cooldown_holder.put(player.getUniqueId(), player_cooldowns);
        
        return cooldown;
    }
    
    public static void removeCooldown(Player player, String cooldownName) {
        Map<String, Cooldown> player_cooldowns = (cooldown_holder.get(player.getUniqueId()) == null ? Maps.<String, Cooldown>newHashMap() : cooldown_holder.get(player.getUniqueId()));
        
        player_cooldowns.remove(cooldownName);
        cooldown_holder.put(player.getUniqueId(), player_cooldowns);
    }
    
    public static Cooldown getCooldown(Player player, String cooldownName) {
        Map<String, Cooldown> player_cooldowns = (cooldown_holder.get(player.getUniqueId()) == null ? Maps.<String, Cooldown>newHashMap() : cooldown_holder.get(player.getUniqueId()));
        
        return player_cooldowns.get(cooldownName);
    }
    
    public static List<Cooldown> getCooldowns(Player player) {
        List<Cooldown> list = Lists.newArrayList();
        Map<String, Cooldown> player_cooldowns = (cooldown_holder.get(player.getUniqueId()) == null ? Maps.<String, Cooldown>newHashMap() : cooldown_holder.get(player.getUniqueId()));
        list.addAll(player_cooldowns.values());
        return Collections.unmodifiableList(list);
    }
    
    public static List<Cooldown> getCooldownsWithSecondsLeft(Player player) {
        List<Cooldown> list = Lists.newArrayList();
        for (Cooldown cd : getCooldowns(player)) {
            if (cd.hasTimeLeft()) {
                list.add(cd);
            }
        }
        
        return Collections.unmodifiableList(list);
    }
    
    public static class Cooldown {
        private int secondsLeft;
        private UUID uuid;
        private boolean started;
        private String name;
        private CooldownRunner runner;
        
        private Cooldown() {}
        
        public String getName() {
            return this.name;
        }
        
        public Player getPlayer() {
            return Bukkit.getPlayer(uuid);
        }
        
        public int getSecondsLeft() {
            return this.secondsLeft;
        }
        
        public boolean hasTimeLeft() {
            return getSecondsLeft() != 0;
        }
        
        public boolean hasStarted() {
            return this.started;
        }
        
        public void setSecondsLeft(int secondsLeft) {
            this.secondsLeft = secondsLeft;
        }
        
        public Cooldown start() {
            if (!hasStarted() && this.runner == null) {
                this.runner = new CooldownRunner(this);
                this.runner.runTaskTimerAsynchronously(PLUGIN, -1, 20);
            }
            
            return this;
        }
        
        public void forceStop() {
            if (hasStarted() && this.runner != null) {
                this.runner.cancel();
                this.runner = null;
            }
        }
    }
    
    private static class CooldownRunner extends BukkitRunnable {

        private final Cooldown cooldown;
        
        CooldownRunner(Cooldown cooldown) {
            this.cooldown = cooldown;
        }
        
        public void run() {
            if (cooldown.secondsLeft == 0) {
                cancel();
                return;
            }
            
            cooldown.secondsLeft--;
        }
    
    }
}
