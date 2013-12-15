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

package org.goblom.bukkitlibs.thread;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Goblom
 */
public class Download implements Runnable {
    private final String link, path;
    private final boolean print;
    
    public Download(String link, String path, boolean printStatus) {
        this.link = link;
        this.path = path;
        this.print = printStatus;
    }
    
    @Override
    public void run() {
        try {
            if (print) System.out.println("[Download] File: " + link);
            if (print) System.out.println("[Download] Save To: " + path);
            
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            FileOutputStream fos = new FileOutputStream(path);
            BufferedOutputStream bos = new BufferedOutputStream(fos, 1024);
            
            float dataRead = 0;
            byte[] b = new byte[1024];
            int i = 0;
            
            while ((i = bis.read(b, 0, 1024)) >= 0) {
                dataRead++; //dataRead = dataRead + 1;
                if (print) System.out.println("[Download] Downloaded: " + dataRead);
                bos.write(b, 0, i);
            }
            
            bos.close();
            fos.close();
            bis.close();
            
            if (print) System.out.println("[Download] File Downloaded!");
        } catch (MalformedURLException e) { e.printStackTrace(); 
        } catch (IOException e) { e.printStackTrace(); }       
    }
}
