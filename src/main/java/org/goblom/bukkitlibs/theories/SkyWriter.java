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

package org.goblom.bukkitlibs.theories;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Print out a message into the sky with blocks!
 * 
 * @deprecated Warning: Not Tested, Not Completed
 * @author Goblom
 */
public class SkyWriter {
    private final String[] message;
    
    public SkyWriter(String... message) throws NotACharacterException {
        for (String string : message) {
            if (string.length() >= 2) {
                throw new NotACharacterException("'" + string + "' is longer then 1.");
            }
        }
        this.message = message;
    }
    
    public void print(Location startCorner, Direction dir, Material mat) {
        switch (dir) {
            case North:
                break;
            case South:
                break;
            case West:
                break;
            case East:
                break;
        }
    }
    
    public String[] getCharacters() {
        return message;
    }
    
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (String chara : getCharacters()) {
            sb.append(chara);
        }
        return sb.toString();
    }
    
    public enum Character {
        A("A", 010,101,111,101,101),
        B("B", 110,101,110,101,110),
        C("C", 011,100,100,100,001),
        D("D", 110,101,101,101,110),
        E("E", 111,100,110,100,111),
        F("F", 111,100,110,100,100),
        G("G", 0111,1000,1010,1001,0111),
        H("H", 101,101,111,101,101),
        I("I", 111,010,010,010,111),
        J("J", 001,001,001,101,010),
        K("K", 101,101,110,101,101),
        L("L", 100,100,100,100,111),
        M("M", 10001,11011,10101,10001,10001),
        N("N", 10001,11001,10101,10011,10001),
        O("O", 0110,1001,10001,1001,0110),
        P("P", 110,101,110,100,100),
        Q("Q", 0110,1001,1001,1001,0111),
        R("R", 1110,1001,1100,1010,1001),
        S("S", 011,100,010,001,110),
        T("T", 111,010,010,010,010),
        U("U", 1001,1001,1001,1001,0110),
        V("V", 101,101,101,101,010),
        W("W", 10001,10001,10101,11011,11011),
        X("X", 101,101,010,101,101),
        Y("Y", 101,101,010,010,010),
        Z("Z", 111,001,010,100,111),
        SPACE(" ", 00,00,00,00,00);
        
        private final int[] bin;
        private final String chr;
        
        Character(String chr, int... bin) { 
            this.chr = chr;
            this.bin = bin; 
        }
        
        public String getCharacter() {
            return chr;
        }
        
        public int[] get() { 
            return bin; 
        }
        
        public static Character getCharacter(String character) {
            for (Character chara : values()) {
                if (chara.getCharacter().equalsIgnoreCase(character)) {
                    return chara;
                }
            }
            return null;
        }
    }
    
    public enum Direction {
        North, South, East, West
    }
    
    public class NotACharacterException extends Exception {
        public NotACharacterException(String message) {
            super(message);
        }
    }
}
