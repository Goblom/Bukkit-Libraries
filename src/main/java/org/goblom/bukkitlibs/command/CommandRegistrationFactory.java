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
package org.goblom.bukkitlibs.command;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;

/**
 * TODO:
 *      Write documentation
 * 
 * @author Goblom
 */
public class CommandRegistrationFactory {

    private static CommandMap cmap;

    private String commandLabel;
    private String description;
    private List<String> aliases;
    private String usage;
    private String permission;
    private String permissionMessage;
    private String fromPlugin;
    private CommandExecutor commandExecutor;
    private TabExecutor tabExecutor;

    public CommandRegistrationFactory(String command) {
        this.commandLabel = command;
    }

    public void build() {
        register();
    }

    public CommandRegistrationFactory withCommandExecutor(CommandExecutor exec) {
        this.commandExecutor = exec;
        return this;
    }

    public CommandRegistrationFactory withPlugin(Plugin plugin) {
        this.fromPlugin = plugin.getName();
        return this;
    }

    public CommandRegistrationFactory withPermissionMessage(String message) {
        this.permissionMessage = ChatColor.translateAlternateColorCodes('&', message);
        return this;
    }

    public CommandRegistrationFactory withPermission(String permission) {
        this.permission = permission;
        return this;
    }

    public CommandRegistrationFactory withUsage(String usage) {
        this.usage = usage;
        return this;
    }

    public CommandRegistrationFactory withAliases(String... aliases) {
        this.aliases = Arrays.asList(aliases);
        return this;
    }

    public CommandRegistrationFactory withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public CommandRegistrationFactory withTabExecutor(TabExecutor tab) {
        this.tabExecutor = tab;
        return this;
    }
    
    public void register() {
        ReflectCommand command;
        if (this.commandLabel != null && !this.commandLabel.isEmpty()) {
            command = new ReflectCommand(this.commandLabel);
        } else {
            throw new CommandNotPreparedException("Command does not have a name.");
        }

        if (this.commandExecutor == null) {
            throw new CommandNotPreparedException(this.commandLabel + " does not have an executor.");
        }

        if (this.aliases != null) {
            command.setAliases(this.aliases);
        }

        if (this.description != null) {
            command.setDescription(this.description);
        }

        if (this.permission != null) {
            command.setPermission(this.permission);
        }

        if (this.permissionMessage != null) {
            command.setPermissionMessage(this.permissionMessage);
        }

        if (this.usage != null) {
            command.setUsage(this.usage);
        }

        if (this.tabExecutor != null) {
            command.setTabExecutor(this.tabExecutor);
        }
        
        getCommandMap().register((this.fromPlugin != null ? this.fromPlugin : ""), command);
        command.setExecutor(this.commandExecutor);
    }
        
    private final CommandMap getCommandMap() {
        if (cmap == null) {
            try {
                final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                cmap = (CommandMap) f.get(Bukkit.getServer());
                return getCommandMap();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (cmap != null) {
            return cmap;
        }
        return getCommandMap();
    }

    private final class ReflectCommand extends Command {

        private CommandExecutor exe = null;
        private TabExecutor tab = null;
        
        protected ReflectCommand(String command) {
            super(command);
        }

        private void setExecutor(CommandExecutor exe) {
            this.exe = exe;
        }
        
        private void setTabExecutor(TabExecutor tab) {
            this.tab = tab;
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            if (exe != null) {
                return exe.onCommand(sender, this, commandLabel, args);
            }
            return false;
        }
        
        @Override
        public List<String> tabComplete(CommandSender sender, String commandLabel, String[] args) {
            if (tab != null) {
                return tab.onTabComplete(sender, this, usage, args);
            }
            return null;
        }
    }

    public class CommandNotPreparedException extends RuntimeException {

        public CommandNotPreparedException(String message) {
            super(message);
        }
    }

    public static CommandRegistrationFactory buildCommand(String command) {
        return new CommandRegistrationFactory(command);
    }
}
