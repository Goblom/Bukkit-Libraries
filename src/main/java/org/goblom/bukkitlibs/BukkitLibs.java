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

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.goblom.bukkitlibs.command.CommandRegistrationFactory;
import org.goblom.bukkitlibs.thread.QueryPool;

/**
 * Just a Holder Class. Stuff will be added to this sometime in the future
 * 
 * @author Goblom
 */
public class BukkitLibs { 
    
    public void QueryThreadTesting() {
        Connection connection = null; //Nulling for example, you should never do this
        
        QueryPool.scheduleQuery(connection, "SELECT * FROM `stats` WHERE `player`='Goblom';", new QueryPool.DataHandler() {
            private SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, K:mm a");
            @Override
            public void onQuery(String sql) {
                System.out.println("Query Started On: " + sdf.format(new Date(getStartTime())));
                System.out.println("Query Being Made: " + sql);
            }
            
            @Override
            public void onDataRecieved(boolean failed, ResultSet rs) {
                if (failed) {
                    //lets throw the exception
                    throw new RuntimeException("The Query Failed At: " + sdf.format(new Date(getEndTime())), getException());
                }
                
                //lets do stuff with the data
                System.out.println("The Query Finished On: " + sdf.format(new Date(getEndTime())));
                
                int kills = -1, deaths = -1;
                
                try {
                    kills = rs.getInt("kills");
                    deaths = rs.getInt("deaths");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                //do stuff with that data
                System.out.println("Kills: " + kills);
                System.out.println("Deaths: " + deaths);
            }
        });
    }
    
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
