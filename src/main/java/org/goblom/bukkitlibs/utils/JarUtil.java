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
package org.goblom.bukkitlibs.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.bukkit.Bukkit;

public class JarUtil {
    public static void downloadJar(String link, String fileName) throws IOException {
        URL url = new URL(link);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream("lib/" + fileName);
        
        byte[] b = new byte[2048];
        int length;
        
        while ((length = is.read(b)) != -1){
            os.write(b, 0, length);
        }
        
        is.close();
        os.close();
    }
    
    public static boolean extractFromJar(final String fileName, final String dest) throws IOException {
        if (getRunningJar() == null) {
            return false;
        }
        final File file = new File(dest);
        if (file.isDirectory()) {
            file.mkdir();
            return false;
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }

        final JarFile jar = getRunningJar();
        final Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements()) {
            final JarEntry je = e.nextElement();
            if (!je.getName().contains(fileName)) {
                continue;
            }
            final InputStream in = new BufferedInputStream(jar.getInputStream(je));
            final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            copyInputStream(in, out);
            jar.close();
            return true;
        }
        jar.close();
        return false;
    }

    private final static void copyInputStream(final InputStream in, final OutputStream out) throws IOException {
        try {
            final byte[] buff = new byte[4096];
            int n;
            while ((n = in.read(buff)) > 0) {
                out.write(buff, 0, n);
            }
        } finally {
            out.flush();
            out.close();
            in.close();
        }
    }

    public static URL getJarUrl(final File file) throws IOException {
        return new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
    }

    public static JarFile getRunningJar() throws IOException {
        if (!RUNNING_FROM_JAR) {
            return null; // null if not running from jar
        }
        String path = new File(JarUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
        path = URLDecoder.decode(path, "UTF-8");
        return new JarFile(path);
    }

    private static boolean RUNNING_FROM_JAR = false;

    static {
        final URL resource = JarUtil.class.getClassLoader().getResource("plugin.yml");
        if (resource != null) {
            RUNNING_FROM_JAR = true;
        }
    }

    public static void addClassPath(final URL url) throws IOException {
        final URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        final Class<URLClassLoader> sysclass = URLClassLoader.class;
        try {
            final Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class } );
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{url});
        } catch (final Throwable t) {
            t.printStackTrace();
            throw new IOException("Error adding " + url + " to system classloader");
        }
    }

}
