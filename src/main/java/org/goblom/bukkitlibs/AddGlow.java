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

import java.lang.reflect.Field;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

/**
 * Make items glow without having those nasty Enchantment Names on the items
 * lore.
 *
 * @author Goblom
 */
public class AddGlow {
    private final GlowEffect effect = new GlowEffect(100);
    
    public AddGlow(ItemStack item) {
        item.addUnsafeEnchantment(effect, 1);
    }
    
    private final class GlowEffect extends CustomEnchantment {
        public GlowEffect(int id) { super(id); }
        @Override
        public boolean canEnchantItem(ItemStack itemstack) { return false; }
        @Override
        public boolean conflictsWith(Enchantment enchantment) { return false; }
        @Override
        public EnchantmentTarget getItemTarget() { return EnchantmentTarget.ALL; }
        @Override
        public int getMaxLevel() { return 1; }
        @Override
        public int getStartLevel() { return 1; }
        @Override
        public int getWeight() { return 1000; }
    }
    
    private abstract class CustomEnchantment extends Enchantment {
        public CustomEnchantment(int id) {
            super(id);
            if (id > 256) throw new IllegalArgumentException("A enchantment id has to be lower then 256!");
            try {
                Field f = Enchantment.class.getDeclaredField("acceptingNew");
                f.setAccessible(true);
                boolean bool = f.getBoolean(null);
                f.set(null, bool);
                Enchantment.registerEnchantment(this);
                f.set(null, bool);
            } catch (Exception e) { e.printStackTrace(); }
        }
        public abstract boolean canEnchantItem(ItemStack item);
        public abstract boolean conflictsWith(Enchantment enchant);
        public abstract EnchantmentTarget getItemTarget();
        public abstract int getMaxLevel();
        public abstract int getStartLevel();
        public abstract int getWeight();
        @Override
        public String getName() { return "Usages" + this.getId(); }
    }
}
