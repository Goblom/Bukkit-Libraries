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

package org.goblom.bukkitlibs.command;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Goblom
 */
public abstract class AbstractCommand implements CommandExecutor {
    
    protected final Plugin plugin;
    
    protected final String command;
    protected final String description;
    protected final List<String> alias;
    protected final String usage;
    protected final String permMessage;
    
    protected PluginCommand plgCMD;
    
    public AbstractCommand(Plugin plugin, String command) {
        this(plugin, command, null, null, null, null);
    }
    
    public AbstractCommand(Plugin plugin, String command, String usage) {
        this(plugin, command, usage, null, null, null);
    }
    
    public AbstractCommand(Plugin plugin, String command, String usage, String description) {
        this(plugin, command, usage, description, null, null);
    }
    
    public AbstractCommand(Plugin plugin, String command, String usage, String description, String permissionMessage) {
        this(plugin, command, usage, description, permissionMessage, null);
    }
    
    public AbstractCommand(Plugin plugin, String command, String usage, String description, List<String> aliases) {
        this(plugin, command, usage, description, null, aliases);
    }
    
    public AbstractCommand(Plugin plugin, String command, String usage, String description, String permissionMessage, List<String> aliases) {
        this.plugin = plugin;
        this.command = command.toLowerCase();
        this.usage = usage;
        this.description = description;
        this.permMessage = permissionMessage;
        this.alias = aliases;
        
        init();
    }
    
    final void init() {
        plgCMD = Bukkit.getServer().getPluginCommand(this.command);
        if (this.usage != null) plgCMD.setUsage(this.usage);
        if (this.description != null) plgCMD.setDescription(this.description);
        if (this.permMessage != null) plgCMD.setPermissionMessage(this.permMessage);
        if (this.alias != null) plgCMD.setAliases(this.alias);
    }
    
    private boolean isPlayer(CommandSender sender) { return (sender instanceof Player); }
    private boolean isAuthorized(CommandSender sender, String permission) { return sender.hasPermission(permission); }
    private boolean isAuthorized(Player player, String permission) { return player.hasPermission(permission); }
    private boolean isAuthorized(CommandSender sender, Permission perm) { return sender.hasPermission(perm); }
    private boolean isAuthorized(Player player, Permission perm) { return player.hasPermission(perm); }
    
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
}
