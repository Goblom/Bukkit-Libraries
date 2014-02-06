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
package Things_That_Spawn_When_Im_Bored;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Goblom
 */
public class Adfly {

    private final String apikey, uid;
    private String site;
    private AdvertType type;
    private Map<String, String> shrunken = new HashMap();

    /*
     * You can get your apiKey & uid from http://adf.ly/publisher/tools#tools-api
     */
    public Adfly(String apiKey, String uid) {
        this.apikey = apiKey;
        this.uid = uid;

        this.type = AdvertType.Interstitial_Advertisement;
    }

    public Adfly setSite(Site site) {
        this.site = site.get();
        return this;
    }

    public Adfly setSite(String custom_domain) {
        this.site = custom_domain;
        return this;
    }

    public Adfly setAdvertisementType(AdvertType type) {
        this.type = type;
        return this;
    }

    public Adfly shrink(final String url) {
        new Thread(
                new Runnable() {
                    public void run() {
                        try {
                            System.out.println("Attempting to shink url: " + url);
                            String api_url = "http://api.adf.ly/api.php?key=" + apikey + "&uid=" + uid + "&advert_type=" + type.get() + "&domain=" + site + "&url=" + url;
                            //System.out.print("[URL] " + api_url); //Uncomment this if you want to see what the url that is sent to Adfly looks like

                            URL api = new URL(api_url);
                            InputStream is = api.openStream();

                            int c = 0;
                            StringBuilder buffer = new StringBuilder();
                            while ((c = is.read()) != -1) {
                                buffer.append((char) c);
                            }

                            shrunken.put(url, buffer.toString());
                        } catch (MalformedURLException ex) {
                            Logger.getLogger(Adfly.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(Adfly.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        ).start();
        return this;
    }

    public Map<String, String> getShrunkenURLs() {
        return shrunken;
    }

    public String getShortenedUrlFrom(String large_url) {
        if (!shrunken.containsKey(large_url)) {
            shrink(large_url);
        }
        return shrunken.get(large_url);
    }

    public void massShrink(String... urls) {
        for (String url : urls) {
            shrink(url);
        }
    }

    public enum Site {

        adf_ly, q_gs, j_gs;

        private String get() {
            return name().replace("_", ".");
        }
    }

    public enum AdvertType {

        Interstitial_Advertisement("int"),
        Framed_Banner("banner"),
        No_Advertisement("none");

        private final String val;

        AdvertType(String val) {
            this.val = val;
        }

        private String get() {
            return val;
        }
    }
    
    /*
     * You can find your adfly_id @ http://adf.ly/publisher/tools#tools-easy... The id is located in the the tan-ish box after adf.ly and before www
     */
    public static String easyLink(int adfly_id, String url) {
        return "http://adf.ly/" + adfly_id + "/url";
    }
}
