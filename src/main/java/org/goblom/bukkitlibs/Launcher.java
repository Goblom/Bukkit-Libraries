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

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Boat;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Squid;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Vector;

/**
 *
 * @author Goblom
 */
public class Launcher {
    
    public static void launchProjectile(Player player, Type type) {
        player.launchProjectile(type.getClazz());
    }
    
    public static void launchProjectile(Player player, Type type, Vector velocity) {
        player.getWorld().spawn(player.getLocation(), type.getClazz()).setVelocity(velocity);

        switch (type) { //do nothing yet.
            case BAT:
            case ARROW:
            case BLAZE:
            case BOAT:
            case CAVE_SPIDER:
            case CHICKEN:
            case COW:
            case CREEPER:
            case EGG:
            case ENDER_PEARL:
            case ENDERMAN:
            case EXP_ORB:
            case FIREBALL:
            case FIREWORK:
            case GHAST:
            case GIANT:
            case HORSE:
    //        case HUMAN_ENTITY:
    //        case PLAYER:
            case IRON_GOLEM:
            case ITEM:
            case MAGMA_CUBE:
            case MINECART:
            case MUSHROOM:
            case OCELOT:
            case PIG:
            case ZOMBIE_PIGMAN:
            case SHEEP:
            case SILVERFISH:
            case SKELETON:
            case SLIME:
            case SNOWBALL:
            case SNOWMAN:
            case SPIDER:
            case SQUID:
            case PRIMED_TNT:
            case VILLAGER:
            case WITCH:
            case WITHER:
            case WITHER_SKELETON:
            case WOLF:
            case ZOMBIE: 
                break;
        }
    }
    
    public static void launchProjectile(LivingEntity entity, Type type) {
        entity.launchProjectile(type.getClazz());
    }
    
    public static void launchProjectile(LivingEntity entity, Type type, Vector velocity) {
        entity.launchProjectile(type.getClazz()).setVelocity(velocity);
    }
    
    public static void launchProjectileFromLocation(Location loc, Type type, Vector velocity) {
        loc.getWorld().spawn(loc, type.getClazz()).setVelocity(velocity);
    }
    
    public static Vector calculateVelocty() { //TODO: Complete
        return null;
    }
    
    public enum Type {
        BAT(Bat.class),
        ARROW(Arrow.class),
        BLAZE(Blaze.class),
        BOAT(Boat.class),
        CAVE_SPIDER(CaveSpider.class),
        CHICKEN(Chicken.class),
        COW(Cow.class),
        CREEPER(Creeper.class),
        EGG(Egg.class),
        ENDER_PEARL(EnderPearl.class),
        ENDERMAN(Enderman.class),
        EXP_ORB(ExperienceOrb.class),
        FIREBALL(Fireball.class),
        FIREWORK(Firework.class),
        GHAST(Ghast.class),
        GIANT(Giant.class),
        HORSE(Horse.class),
//        HUMAN_ENTITY(HumanEntity.class),
//        PLAYER(Player.class),
        IRON_GOLEM(IronGolem.class),
        ITEM(Item.class),
        MAGMA_CUBE(MagmaCube.class),
        MINECART(Minecart.class),
        MUSHROOM(MushroomCow.class),
        OCELOT(Ocelot.class),
        PIG(Pig.class),
        ZOMBIE_PIGMAN(PigZombie.class),
        SHEEP(Sheep.class),
        SILVERFISH(Silverfish.class),
        SKELETON(Skeleton.class),
        SLIME(Slime.class),
        SNOWBALL(Snowball.class),
        SNOWMAN(Snowman.class),
        SPIDER(Spider.class),
        SQUID(Squid.class),
        PRIMED_TNT(TNTPrimed.class),
        VILLAGER(Villager.class),
        WITCH(Witch.class),
        WITHER(Wither.class),
        WITHER_SKELETON(WitherSkull.class),
        WOLF(Wolf.class),
        ZOMBIE(Zombie.class); //Im sure there are more i can use... But this is just for testing.
        private final Class clazz;
        private final Vector velocity;
        private Type(Class clazz) { this(clazz, null); }
        private Type(Class clazz, Vector velocity) { this.clazz = clazz; this.velocity = velocity; }
        public Class getClazz() { return clazz; }
        public Vector getVelocity() { return velocity; }
    }
}
