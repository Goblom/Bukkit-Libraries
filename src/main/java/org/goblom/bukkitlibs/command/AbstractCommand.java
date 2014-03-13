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

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 *
 * @author Goblom
 */
public abstract class AbstractCommand implements CommandExecutor {
    
    protected final String command;
    protected final String description;
    protected final List<String> alias;
    protected final String usage;
    protected final String permMessage;

    protected static CommandMap cmap;
    
    public AbstractCommand(String command) {
        this(command, null, null, null, null);
    }
    
    public AbstractCommand(String command, String usage) {
        this(command, usage, null, null, null);
    }
    
    public AbstractCommand(String command, String usage, String description) {
        this(command, usage, description, null, null);
    }
    
    public AbstractCommand(String command, String usage, String description, String permissionMessage) {
        this(command, usage, description, permissionMessage, null);
    }
    
    public AbstractCommand(String command, String usage, String description, List<String> aliases) {
        this(command, usage, description, null, aliases);
    }
    
    public AbstractCommand(String command, String usage, String description, String permissionMessage, List<String> aliases) {
        this.command = command.toLowerCase();
        this.usage = usage;
        this.description = description;
        this.permMessage = permissionMessage;
        this.alias = aliases;
    }
    
    public void register() {
        ReflectCommand cmd = new ReflectCommand(this.command);
        if (this.alias != null) cmd.setAliases(this.alias);
        if (this.description != null) cmd.setDescription(this.description);
        if (this.usage != null) cmd.setUsage(this.usage);
        if (this.permMessage != null) cmd.setPermissionMessage(this.permMessage);
        getCommandMap().register("", cmd);
        cmd.setExecutor(this);
    }
    
    final CommandMap getCommandMap() {
        if (cmap == null) {
            try {
                final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                cmap = (CommandMap) f.get(Bukkit.getServer());
                return getCommandMap();
            } catch (Exception e) { e.printStackTrace(); }
        } else if (cmap != null) { return cmap; }
        return getCommandMap();
    }
    
    private final class ReflectCommand extends Command {
        private CommandExecutor exe = null;
        protected ReflectCommand(String command) { super(command); }
        public void setExecutor(CommandExecutor exe) { this.exe = exe; }
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (exe != null) { exe.onCommand(sender, this, commandLabel, args); }
            return false;
        }
    }
    
    public boolean isPlayer(CommandSender sender) { return (sender instanceof Player); }
    public boolean isAuthorized(CommandSender sender, String permission) { return sender.hasPermission(permission); }
    public boolean isAuthorized(Player player, String permission) { return player.hasPermission(permission); }
    public boolean isAuthorized(CommandSender sender, Permission perm) { return sender.hasPermission(perm); }
    public boolean isAuthorized(Player player, Permission perm) { return player.hasPermission(perm); }
    
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
    
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return null;
    }
}
