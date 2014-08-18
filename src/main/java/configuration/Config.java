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

package configuration;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Goblom
 */
public class Config {
    private final Plugin plugin;
    private final File file;
    private FileConfiguration config;
    
    public Config(Plugin plugin, String file) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), (file.endsWith(".yml") ? file : file + ".yml"));
        
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }
    
    public FileConfiguration getFileConfiguration() {
        return this.config;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public void set(String path, Object value) {
        set(path, value, false);
    }
    
    public void set(String path, Object value, boolean save) {
        this.config.set(path, value);
        
        if (save) {
            save();
        }
    }
    
    public <T> T get(String path, T def) {
        if (!contains(path)) {
            set(path, def, true);
        }
        
        return (T) this.config.get(path);
    }
    
    public void save() {
        try {
            this.config.save(getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }
    
    public boolean contains(String path) {
        return this.config.contains(path);
    }
}
