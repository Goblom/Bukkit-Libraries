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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.goblom.bukkitlibs.command.CommandRegistrationFactory;

/**
 * Just a Holder Class. Stuff will be added to this sometime in the future
 * 
 * @author Goblom
 */
public class BukkitLibs { 
    
    public void CommandRegistrationFactoryTesting() {
        CommandRegistrationFactory factory = new CommandRegistrationFactory();
//                                           new CommandRegistrationFactory("command_name");
//                                           CommandRegistrationFactory.builder();
//                                           CommandRegistrationFactory.buildCommand("command_name");
                                   
                                   factory.withCommandLabel("command_name"); //Optional
                                   factory.withAliases("alias1", "alias2", "alias3");
                                   factory.withCommandExecutor(new CommandExecutor() {
                                        public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
                                            cs.sendMessage("This is a dynamic command");
                                            return true;
                                        }
                                   });
                                   factory.withDescription("This is a dynamic command.");
                                   factory.withPermission("test.permission");
                                   factory.withPermissionMessage(ChatColor.RED + "&lYou do not have permission to use this command.");
                                   factory.withPlugin(Bukkit.getPluginManager().getPlugin("some plugin name"));
                                   factory.withUsage("/<command> [args]");
                                   
                                   factory.register();
//                                        .build();
                                   
        /********************************
         * Lets write a command
         * 
         * Values:
         *      Command: /heal
         *      Aliases: /h
         *      Usage: /<command> <player> [amount]
         *      Permission: heal.use
         *      Permission Message: You do not have permission do /heal
         */
         CommandRegistrationFactory healCommand = CommandRegistrationFactory.buildCommand("heal");
                                    healCommand.withAliases("h");
                                    healCommand.withDescription("Heal a player");
                                    healCommand.withUsage("/<command> <player> [amount]");
                                    healCommand.withPermission("heal.use");
                                    healCommand.withPermissionMessage("You do not have permission do do /heal");
                                    healCommand.withCommandExecutor(new CommandExecutor() {
                                        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                                            if (!(sender instanceof Player)) return true;
                                            if (args.length >= 1) {
                                                Player toHeal = Bukkit.getPlayer(args[0]);
                                                if (toHeal != null) {
                                                    if (args.length >= 2) {
                                                        try {
                                                            toHeal.setHealth(Double.valueOf(args[1]));
                                                            sender.sendMessage("You have healed " + toHeal.getName());
                                                            toHeal.sendMessage("You have been healed!");
                                                        } catch (NumberFormatException e) {
                                                            sender.sendMessage("Health must be a number!");
                                                        }
                                                    } else {
                                                        toHeal.setHealth(20.0D);
                                                        sender.sendMessage("You have healed " + toHeal.getName());
                                                        toHeal.sendMessage("You have been healed!");
                                                    }
                                                } else sender.sendMessage("That player is not online!");
                                            } else {
                                                ((Player) sender).setHealth(20.0D);
                                                sender.sendMessage("You have been healed!");
                                            }
                                            return true;
                                        }
                                    });
                                    factory.build();
        
    }
}
