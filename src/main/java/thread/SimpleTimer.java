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
package thread;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Goblom
 */
public class SimpleTimer {

    private final int runTime;
    private final Plugin plugin;

    private Runner runner;
    private BukkitTask runnerTask, endTask;

    public SimpleTimer(Plugin plugin, int runTime) {
        this(plugin, runTime, null);
    }

    public SimpleTimer(Plugin plugin, int runTime, Runner runner) {
        this(plugin, runTime, runner, false);
    }

    public SimpleTimer(Plugin plugin, int runTime, Runner runner, boolean start) {
        this.runTime = runTime * 20;
        this.runner = runner;
        this.plugin = plugin;

        if (start) {
            try {
                startRunning();
            } catch (InvalidRunnerException e) {
                e.printStackTrace();
            }
        }
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void startRunning() throws InvalidRunnerException {
        if (runner != null) {
            if (runnerTask != null) {
                getRunningTask().cancel();
                this.runnerTask = null;
            }
            if (endTask != null) {
                endTask.cancel();
                this.endTask = null;
            }
            
            this.runnerTask = Bukkit.getScheduler().runTaskTimer(plugin,
                new Runnable() {
                    public void run() {
                        getRunner().onRun();
                    }
                },
                -1, 20);
            this.endTask = Bukkit.getScheduler().runTaskLater(plugin, 
                new Runnable() {
                    public void run() {
                        getRunningTask().cancel();
                        getRunner().onEnd();
                    }
                }, runTime);

        } else {
            throw new InvalidRunnerException("No runner detected, unable to start running!");
        }
    }

    public BukkitTask getRunningTask() {
        return runnerTask;
    }

    public Runner getRunner() {
        return runner;
    }

    public void cancelRunning() {
        getRunningTask().cancel();
        endTask.cancel();
    }

    public interface Runner {

        void onRun();

        void onEnd();
    }

    public class InvalidRunnerException extends Exception {

        public InvalidRunnerException(String message) {
            super(message);
        }
    }
}
