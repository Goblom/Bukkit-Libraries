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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Attempt at writing a cURL Like system in Java
 * @author Goblom
 */
public class cURL {
    
    private final String host;
    private final URL url;
    
    public cURL(String host) throws MalformedURLException {
        this.host = host;
        
        this.url = new URL(host);
    }
    
    public void StackOverflowTest() throws UnsupportedEncodingException, IOException { // http://stackoverflow.com/questions/2586975/how-to-use-curl-in-java
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            
            for (String line; (line = reader.readLine()) != null;) {
                System.out.println(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
    
    public InputStream POST(String whatToPost) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.getOutputStream().write(whatToPost.getBytes("UTF-8"));
        return conn.getInputStream();
    }
    
    public InputStream GET(String whatToPost) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.getOutputStream().write(whatToPost.getBytes("UTF-8"));
        return conn.getInputStream();
    }
    
    public InputStream customRequestMethod(String requestMethod, String whatToPost) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod);
        conn.getOutputStream().write(whatToPost.getBytes("UTF-8"));
        return conn.getInputStream();
    }
}
