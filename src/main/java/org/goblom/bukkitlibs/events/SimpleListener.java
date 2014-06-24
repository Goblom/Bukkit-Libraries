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

package org.goblom.bukkitlibs.events;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Make a listener extend this class then all you need to do is "new ExampleListener(myPlugin);"
 * @author Goblom
 */
public abstract class SimpleListener implements Listener {
    
    private static List<SimpleListener> listeners = Lists.newArrayList();
    
    public static void unregisterAll() {
        for (SimpleListener listener : listeners) {
            listener.unregister();
        }
    }
    
    public static List<SimpleListener> getRegistered() {
        return Collections.unmodifiableList(listeners);
    }
            
    public SimpleListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        SimpleListener.listeners.add(this);
    }
    
    public void unregister() {
        HandlerList.unregisterAll(this);
    }
}
