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

package org.goblom.bukkitlibs.theories;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * #Failure
 * 
 * @author Goblom
 */
public class PermHandler {
    private final String pluginName;
    private final FileConfiguration config;
    
    public PermHandler(Plugin plugin) {
        this.pluginName = plugin.getName();
        this.config = plugin.getConfig();
    }
    
    public List<String> getGroups() {
        List<String> g = new ArrayList();
        for (String string : getConfig().getConfigurationSection("Groups").getKeys(false)) {
            g.add(string);
        }
        return g;
    }
    
    public List<String> getGroupPermissions(String groupName) {
        List<String> g = new ArrayList();
        for (String string : getConfig().getStringList("Groups." + groupName)) {
            g.add(string);
        }
        return g;
    }
    
    private FileConfiguration getConfig() {
        return config;
    }
    
    private Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(pluginName);
    }
}
