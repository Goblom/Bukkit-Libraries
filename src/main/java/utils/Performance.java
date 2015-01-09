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

package utils;

import com.google.common.collect.Lists;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author Goblom
 */
public class Performance {
    
    private static JavaPlugin PLUGIN = JavaPlugin.getProvidingPlugin(Performance.class);
    private static final double TICKS_PER_SECOND = 20;
    private static final long MEMORY_BYTES = 1048576;
    private static final PerformanceHolder STORED = new PerformanceHolder();
    private static final PerformancePoll POLLER = new PerformancePoll();
    private static boolean started = false;
    
    public static int POLL_INTERVAL = 40;
    
    public static void startChecking() {
        if (started) return;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(PLUGIN, POLLER, 0, POLL_INTERVAL);
        started = true;
    }
    
    public static long getTimesPolled() {
        return Performance.POLLER.getTimesPolled();
    }
    
    public enum Stats {
        Max {
            public double get() {
                return Runtime.getRuntime().maxMemory() / MEMORY_BYTES;
            }
        },
        Used {
            public double get() {
                long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                return mem / MEMORY_BYTES;
            }
        },
        Percentage_Free {
            public double get() {
                return (100 / Stats.Max.get()) * Stats.Free.get();
            }
        },
        Free {
            public double get() {
                return Stats.Max.get() - Stats.Used.get();
            }
        },
        TICKS_PER_SECOND {
            public double get() {
                return TPS;
            }
        };
        
        private static double TPS = Performance.TICKS_PER_SECOND;
        public abstract double get();
    }
    
    private static class PerformanceHolder {
        private static int MAX_SIZE = 50;
        
        private LinkedList<Double> list = Lists.newLinkedList();
        
        private Double poll() {
            return list.poll();
        }
        
        private void add(double item) {
            if (item <= 20) {
                this.list.add(item);
                
                if (size() > MAX_SIZE) {
                    poll();
                }
            }
        }
        
        public int size() {
            return this.list.size();
        }
        
        public double getAverage() {
            double total = 0;
            for (double d : this.list) {
                total += d;
            }
            
            return total / this.list.size();
        }
    }
    
    private static class PerformancePoll implements Runnable {
        private long last = System.currentTimeMillis() - 3000;
        private long count;
        
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            long spent = (now - last) / 1000;
            if (spent == 0) {
                spent = 1;
            }
            
            double tps = POLL_INTERVAL / spent;
            Performance.Stats.TPS = tps;
            STORED.add(tps);
            
            last = now;
            count++;
        }
        
        public long getTimesPolled() {
            return this.count;
        }
    }
}
