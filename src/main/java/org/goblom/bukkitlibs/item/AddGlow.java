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

package org.goblom.bukkitlibs.item;

import java.lang.reflect.Field;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.goblom.bukkitlibs.enchantment.AbstractEnchantment;

/**
 * Make items glow without having those nasty Enchantment Names on the items
 * lore.
 *
 * @author Goblom
 */
public class AddGlow {
    
    public static void makeGlow(ItemStack item) {
        item.addUnsafeEnchantment(new GlowEffect(100), 1);
    }
    
    private static final class GlowEffect extends AbstractEnchantment {
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

}
