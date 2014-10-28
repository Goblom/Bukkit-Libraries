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


import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import org.bukkit.ChatColor;

/**
 *
 * @author Goblom
 */
public class Colorize {

    private static final Random RANDOM = new Random();

    private static final char[] COLORS = {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final char[] STYLES = {'l', 'n', 'o', 'k', 'm'}; //do not use r
    private static final char[] ALL_COLORS = {'1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'l', 'n', 'o', 'k', 'm'}; //do not use r

    public static String getRandomColorCode(boolean withExtra) {
        char[] toRandomize = (withExtra ? ALL_COLORS : COLORS);

        return "&" + String.valueOf(toRandomize[RANDOM.nextInt(toRandomize.length)]);
    }

    public static Stylize style(String toStyle) {
        return new Stylize(toStyle);
    }

    public static class Stylize {

        private final String toStyle;

        protected Stylize(String toStyle) {
            this.toStyle = toStyle;
        }

        public String toRainbow() {
            StringBuilder sb = new StringBuilder();

            for (char c : toStyle.toCharArray()) {
                sb.append(Colorize.getRandomColorCode(false) + String.valueOf(c));
            }

            return ChatColor.translateAlternateColorCodes('&', sb.toString());
        }

        public String toGarbage() {
            List<Character> characters = Lists.newArrayList();
            
            for (char c : toStyle.toCharArray()) {
                characters.add(c);
            }

            StringBuilder sb = new StringBuilder(toStyle.length());
            while (!characters.isEmpty()) {
                int randPicker = (int) (Math.random() * characters.size());
                sb.append(characters.remove(randPicker));
            }
            
            return sb.toString();
        }
        
        public String toStripe(ChatColor colorOne, ChatColor colorTwo) {
            StringBuilder sb = new StringBuilder();
            boolean a = true;
            for (char c : toStyle.toCharArray()) {
                sb.append(a ? colorOne : colorTwo);
                sb.append(c);
            }
            
            return sb.toString();
        }
    }
}
