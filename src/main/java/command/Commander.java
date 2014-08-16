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

package command;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Goblom
 */
public class Commander {
    
    private static List<SubCommand> commands = Lists.newArrayList();
    
    public static SubCommand getSubCommand(String name) {
        for (SubCommand cmd : commands) {
            for (String alias : cmd.getAliases()) {
                if (alias.equalsIgnoreCase(name)) {
                    return cmd;
                }
            }
        }
        
        return null;
    }
    
    public static void handle(CommandSender sender, String[] args, int subCommandIndex, String... failedMessage) {
        try {
            SubCommand cmd = getSubCommand(args[subCommandIndex]);
                       cmd.handle(sender, args);
        } catch (Exception e) {
            sender.sendMessage(failedMessage);
        }
    }
    
    public static void registerSubCommand(SubCommand cmd) {
        commands.add(cmd);
    }
    
    public static void registerSubCommand(List<String> alias, String description, final CommandHandler handler) {
        registerSubCommand(new SubCommand(alias, description) {

            @Override
            public void handle(CommandSender sender, String[] args) {
                handler.handle(sender, args);
            }
        });
    }
    
    public static List<SubCommand> getRegisteredCommands() {
        return Collections.unmodifiableList(commands);
    }
    
    public static abstract class SubCommand implements CommandHandler {
        private final List<String> alias;
        private final String description;
        
        public SubCommand(List<String> alias, String description) {
            this.alias = alias;
            this.description = description;
        }
        
        public List<String> getAliases() {
            return this.alias;
        }
        
        public String getDescription() {
            return this.description;
        }
    }
    
    public interface CommandHandler {
        public void handle(CommandSender sender, String[] args);
    }
}
