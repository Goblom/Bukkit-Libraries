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

package org.goblom.bukkitlibs.thread;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 *
 * @author Goblom
 */
public class TextReader {
    
    public static void read(String url, ReceiveHandler handler) throws MalformedURLException, IOException {
        read(new URL(url), handler);
    }
    
    public static void read(final URL url, final ReceiveHandler handler) {
        final List<String> lines = Lists.newArrayList();
        
        new Thread(
            new Runnable() {
                public void run() {
                    BufferedReader reader = null;
                    
                    try {
                         reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    if (reader != null) {
                        try {
                            while (reader.readLine() != null) {
                                lines.add(reader.readLine());
                            }
                            
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    handler.onRecieve(lines);
                }
            }
        ).start();
    }
    
    public static interface ReceiveHandler {
        public void onRecieve(List<String> lines);
    }
}
