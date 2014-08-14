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

package theories;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Goblom
 */
public class AwesomeLoader<V> {
    
    private transient Object[] v;
    private final Object[] EMPTY_VALUE = {};
    
    public AwesomeLoader() {
        v = EMPTY_VALUE;
    }
    
    public AwesomeLoader(Object v) {
        this.v = new Object[] { v };
    }
    
    public V loadFile(File file) {        
        V c = null;
        if (!file.getName().endsWith(".class")) {
            return c;
        }
        String name = file.getName().substring(0, file.getName().lastIndexOf("."));
        
        ClassLoader loader;
        
        try {
            loader = new URLClassLoader(new URL[] { file.toURI().toURL() }, ((V) v[0]).getClass().getClassLoader());
        } catch (MalformedURLException e) {
            log("Error while loading ClassLoader.");
            return c;
        }
        try {
            Class<?> clazz = loader.loadClass(name);
            Object obj = clazz.newInstance();
            
            //Need to check instanceof here but unable to tackle the problem.
            log("Loaded class: " + ((V) obj).getClass().getSimpleName());
            c = (V) obj;
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AwesomeLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(AwesomeLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AwesomeLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return c;
    }
    
    public V loadFile(String file) {
        return loadFile(new File(file));
    }
    
    public List<V> loadDirectory(File dir) {
        List<V> list = new ArrayList();
        
        if (!dir.exists()) {
            log("Unable to load files from directory. Does Directory exist?");
            return list;
        }
        
        if (dir.isDirectory()) {
            ClassLoader loader;
            try {
                loader = new URLClassLoader(new URL[] { dir.toURI().toURL() }, ((V) v[0]).getClass().getClassLoader());
            } catch (MalformedURLException e) {
                log("Error while loading ClassLoader.");
                return list;
            }
            
            for (File file : dir.listFiles()) {
                if (!file.getName().endsWith(".class")) {
                    continue; //We only want to load class files
                }
                String name = file.getName().substring(0, file.getName().lastIndexOf("."));
                
                try {
                    Class<?> clazz = loader.loadClass(name);
                    Object obj = clazz.newInstance();
                    
                    //Need to check instanceof here but unable to tackle the problem.
                    list.add((V) obj);
                    log("Loaded class: " + ((V) obj).getClass().getSimpleName());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(AwesomeLoader.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(AwesomeLoader.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(AwesomeLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return list;
    }
    
    public List<V> loadDirectory(String directory) {
        return loadDirectory(new File(directory));
    }
    
    private void log(String message) {
        Logger.getLogger("AwesomeLoader").warning(message);
    }
}
