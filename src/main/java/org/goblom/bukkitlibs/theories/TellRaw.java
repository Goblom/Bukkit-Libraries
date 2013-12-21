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

package org.goblom.bukkitlibs.theories;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.v1_7_R1.ChatSerializer;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;

//import com.comphenix.protocol.PacketType;
//import com.comphenix.protocol.ProtocolLibrary;
//import com.comphenix.protocol.ProtocolManager;
//import com.comphenix.protocol.events.PacketContainer;
//import com.comphenix.protocol.wrappers.WrappedChatComponent;
//import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Goblom
 */
public class TellRaw {
    private final List<String> messages = new ArrayList<String>();
    
    public TellRaw(String... message) {
        for (String m : message) {
            messages.add("text:'" + m + "',");
        }
    }
    
    public TellRaw addFormat(int messageIndex, Format format) {
        String oldMessage = messages.get(messageIndex);
        messages.set(messageIndex, oldMessage + format.getFormat() + ":" + true + ",");
        return this;
    }
    
    public TellRaw addColor(int messageIndex, ChatColor color) {
        String oldMessage = messages.get(messageIndex);
        messages.set(messageIndex, oldMessage + "color:" + color.name().toLowerCase() + ",");
        return this;
    }
    
    public TellRaw addClickEvent(int messageIndex, Action action, String value) {
        String oldMessage = messages.get(messageIndex);
        messages.set(messageIndex, oldMessage + "clickEvent:{action:" + action.getAction() + ",value:'" + value + "'}");
        return this;
    }
    
    public TellRaw addHoverEvent(int messageIndex, Action action, String value) {
        String oldMessage = messages.get(messageIndex);
        messages.set(messageIndex, oldMessage + "hoverEvent:{action:" + action.getAction() + ",value:'" + value + "'}");
        return this;
    }
    
    public String itemToJSON(ItemStack i) {
        String json = "{id:" + i.getTypeId() + ",";
        if (i.hasItemMeta()) {
            json = json + "tag:{display:{";
            if (i.getItemMeta().hasDisplayName()) {
                json = json + "Name:" + i.getItemMeta().getDisplayName() + ",";
            }
            if (i.getItemMeta().hasLore()) {
                String lores = "Lore:[";
                for (String lore : i.getItemMeta().getLore()) {
                    lores = "'" + lore + "'" + ",";
                }
                json = json + lores;
            }
            json = json + "]}}";
        }
        return json + "}";
    }
    
    public enum Format {
        BOLD("bold"), 
        UNDERLINED("underlined"), 
        ITALIC("italic"), 
        STRIKETHROUGH("strikethrough"), 
        OBFUSTICATED("obfusticated");
        private final String format;
        Format(String format) { this.format = format; }
        public String getFormat() { return format; }
    }
    
    public enum Action {
        RUN_COMMAND("run_command"),
        SHOW_TEXT("show_text"),
        SHOW_ITEM("show_item"),
        SHOW_ACHIEVEMENT("show_achievement"),
        SUGGEST_COMMAND("suggest_command"),
        OPEN_URL("open_url");
        
        private final String action;
        Action(String action) { this.action = action; }
        public String getAction() { return action; }
    }
    
    private String removeExcess(String message) { //Fixes extra commas that have been added
        return message.replaceAll(",]", "]").replaceAll(",}", "}");
    }
    
    public List<String> getMessages() {
        return messages;
    }
    
    public String[] getMessagesAsArray() {
        return messages.toArray(new String[0]);
    }
        
    public void send(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(toString()), true));
    }
    
//    public void send(Player player) {
//        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
//        PacketContainer messagePacket = manager.createPacket(PacketType.Play.Server.CHAT);
//        messagePacket.getChatComponents().write(0, WrappedChatComponent.fromJson(toString()));
//        try {
//            manager.sendServerPacket(player, messagePacket);
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//    }
    
    @Override
    public String toString() {
        String message = "{";
        for (String m : messages) {
            if (message.equals("{")) {
                message = message + m + ",extra:["; 
            } else {
                message = "{" + message + m + "},";
            }
        }
        return removeExcess(message + "}");
    }
}
