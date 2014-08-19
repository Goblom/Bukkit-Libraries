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

import com.google.common.collect.Maps;
import static command.AbstractCommand.cmap;
import command.AnnotationCommand.Annotations.AnonCommand;
import command.AnnotationCommand.Annotations.AnonCommandFull;
import command.AnnotationCommand.Annotations.AnonDescription;
import command.AnnotationCommand.Annotations.AnonPermission;
import command.AnnotationCommand.Annotations.AnonPermissionMessage;
import command.AnnotationCommand.Annotations.AnonUsage;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Goblom
 */
public class AnnotationCommand {

    protected static final String PERMISSION_MESSAGE = "&cI'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is an error.";
            
    private static CommandMap COMMAND_MAP;
    
    static {
        try {
            final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            f.setAccessible(true);
            cmap = (CommandMap) f.get(Bukkit.getServer());
        } catch (Exception e) {
            System.out.println("ERRORS WILL STRIKE EVERYWHERE");
            e.printStackTrace();
        }
    }

    public static void registerListener(CommandListener listener) {
        for (Method method : listener.getClass().getMethods()) {
            AnonCommandFull full = method.getAnnotation(AnonCommandFull.class);
            
            if (full != null) {
                registerCommand(method, listener, full.command(), full.description(), full.usage(), full.permission(), full.permissionMessage());
            } else {
                AnonCommand command = method.getAnnotation(AnonCommand.class);
                AnonDescription description = method.getAnnotation(AnonDescription.class);
                AnonUsage usage = method.getAnnotation(AnonUsage.class);
                AnonPermission permission = method.getAnnotation(AnonPermission.class);
                AnonPermissionMessage permissionMessage = method.getAnnotation(AnonPermissionMessage.class);
                
                if (command == null) return;
                String desc = (description == null ? "Command handled by AnonCommand" : description.value());
                String us = (usage == null ? "" : usage.value());
                String perm = (permission == null ? "" : permission.value());
                String permMessage = (permissionMessage == null ? PERMISSION_MESSAGE : permissionMessage.value());
                
                registerCommand(method, listener, command.value(), desc, us, perm, permMessage);
            }
        }
    }

    private static void registerCommand(Method method, CommandListener listener, String command, String description, String usage, String permission, String permissionMessage) {
        ReflectCommand cmd = new ReflectCommand(new CommandData(method, listener, command));
                       cmd.setUsage(usage);
                       cmd.setDescription(description);
                       cmd.setPermission(permission);
                       cmd.setPermissionMessage(permissionMessage);
                       
        COMMAND_MAP.register("", cmd);
    }
    
    public static interface CommandListener { }

    public static class CommandEvent {

        public final CommandSender sender;
        public final Command command;
        public final String[] args;

        private CommandEvent(CommandSender sender, Command command, String[] args) {
            this.sender = sender;
            this.args = args;
            this.command = command;
        }

        public String getArg(int index) {
            return args[index];
        }

        public boolean isNumerical(String str) {
            try {
                Integer.parseInt(str);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static class Annotations {

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public static @interface AnonCommand {
            public String value();
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public static @interface AnonDescription {
            public String value();
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public static @interface AnonUsage {
            public String value();
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public static @interface AnonPermission {
            public String value();
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public static @interface AnonPermissionMessage {
            public String value();
        }

        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        public static @interface AnonCommandFull {

            public String command();

            public String description() default "Command handled by AnonCommand";

            public String usage() default "";

            public String permission() default "";

            public String permissionMessage() default PERMISSION_MESSAGE;
        }
    }
    
    private static class CommandData implements CommandExecutor {
        private Method method;
        private CommandListener listener;
        private String command;
        
        private CommandData(Method method, CommandListener listener, String command) {
            this.method = method;
            this.listener = listener;
            this.command = command;
        }
        
        public String getCommand() {
            return this.command;
        }
        
        public Method getMethod() {
            return this.method;
        }
        
        public CommandListener getListener() {
            return this.listener;
        }
        @Override
        public int hashCode() {
            return (method.getName() + listener.getClass().getCanonicalName()).hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CommandData) {
                return this.hashCode() == obj.hashCode();
            }

            return false;
        }

        @Override
        public String toString() {
            return listener.getClass().getCanonicalName() + ":" + method.getName();
        }

        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            try {
                getMethod().invoke(getListener(), new CommandEvent(sender, command, args));
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return true;
        }
    }
    
    private static class ReflectCommand extends Command {
        private CommandData data;
        
        private ReflectCommand(CommandData data) {
            super(data.getCommand());
            this.data = data;
        }
        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args) {
            return data.onCommand(sender, this, commandLabel, args);
        }
        
    }
}
