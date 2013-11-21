/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.goblom.bukkitlibs.events;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.plugin.Plugin;

/**
 * A Semi 100% working EntityMoveEvent calculator ;) Honestly, this will work
 * but i do not know how well, its 3am and i haven't slept in a few days
 *
 * @TODO - JavaDocs
 * @author Goblom
 */
public class EntityMove {

    private Map<UUID, Entity> entity = new HashMap();
    private Map<UUID, Location> from = new HashMap();

    public EntityMove(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new DeadListener(), plugin);
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (LivingEntity ent : world.getLivingEntities()) {
                        if (!entity.containsKey(ent.getUniqueId())) {
                            entity.put(ent.getUniqueId(), ent);
                        }
                        if (!from.containsKey(ent.getUniqueId())) {
                            from.put(ent.getUniqueId(), ent.getLocation());
                        }

                        Location loc = from.get(ent.getUniqueId());
                        if (loc != ent.getLocation()) {
                            EntityMoveEvent event = new EntityMoveEvent(entity.get(ent.getUniqueId()), loc, ent.getLocation());
                            Bukkit.getPluginManager().callEvent(event);
                        }
                    }
                }
//                entity.clear();
//                from.clear();
            }
        }, 0, 1);
    }

    private final class DeadListener implements Listener {
        @EventHandler
        public void entityDeath(EntityDeathEvent event) {
            if (entity.containsKey(event.getEntity().getUniqueId())) {
                entity.remove(event.getEntity().getUniqueId());
            }
            if (from.containsKey(event.getEntity().getUniqueId())) {
                from.remove(event.getEntity().getUniqueId());
            }
        }
    }
    
    public static final class EntityMoveEvent extends EntityEvent implements Cancellable {

        private static final HandlerList handlers = new HandlerList();
        private boolean cancel = false;
        private Location from, to;

        public EntityMoveEvent(Entity what, Location from, Location to) {
            super(what);
            this.from = from;
            this.to = to;
        }

        @Override
        public HandlerList getHandlers() {
            return handlers;
        }

        public static HandlerList getHandlerList() {
            return handlers;
        }

        @Override
        public boolean isCancelled() {
            return cancel;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancel = cancel;
        }

        public Location getFrom() {
            return from;
        }

        public void setFrom(Location from) {
            this.from = from;
        }

        public Location getTo() {
            return to;
        }

        public void setTo(Location to) {
            this.to = to;
        }
    }
}
