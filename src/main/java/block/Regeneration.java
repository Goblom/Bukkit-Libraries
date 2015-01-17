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
package block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Regeneration v1.0
 *
 * A simple block regeneration class that allows a person to request a location
 * to be regenerated after a certain amount of time.
 *
 * @author Goblom
 */
public class Regeneration {

    private final Plugin plugin;
    private static final Map<Location, BlockState> toRegen = Maps.newHashMap();
    private static final List<BlockRegen> tasks = Lists.newArrayList();

    /**
     * @see Regeneration
     * @param plugin 
     */
    public Regeneration(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Checks to see if the task list is not empty and need to run tasks
     *
     * @return true if there are tasks that need to run
     */
    public boolean hasTasks() {
        return !tasks.isEmpty();
    }

    /**
     * Forces all tasks to run
     */
    public void forceTasks() {
        for (BlockRegen task : tasks) {
            //i overwrote the cancel task
            task.cancel();
        }
    }

    /**
     * Checks if the location you want to regen already has a regen task running
     * on it
     *
     * @param location
     * @return true if that location already has a {@link BlockRegen} task
     */
    public boolean alreadyScheduled(Location location) {
        return toRegen.containsKey(location);
    }

    /**
     * Request the location for a {@link BlockRegen} task
     *
     * @param block the block to regen back to
     * @param ticksLater ticks later for regen task to run
     * @return true if task was started, false if a task is {@link Regeneration#alreadyScheduled(org.bukkit.Location)
     * }
     */
    public boolean request(Block block, long ticksLater) {
        return request(block.getState(), ticksLater);
    }

    /**
     * Request the location for a {@link BlockRegen} task
     *
     * @param state the state to regen back to
     * @param ticksLater ticks later for regen task to run
     * @return true if task was started, false if a task is {@link Regeneration#alreadyScheduled(org.bukkit.Location)
     * }
     */
    public boolean request(BlockState state, long ticksLater) {
        if (alreadyScheduled(state.getLocation())) {
            return false;
        }

        this.toRegen.put(state.getLocation(), state);

        BlockRegen regenTask = new BlockRegen(state.getLocation(), state);
        regenTask.runTaskLater(plugin, ticksLater);

        return tasks.add(regenTask);
    }

    /**
     * Does the event task for you, just pass the {@link BlockBreakEvent} to
     * this
     *
     * @param event
     * @param ticksLater
     */
    public void onBlockBreak(BlockBreakEvent event, long ticksLater) {
        request(event.getBlock(), ticksLater);
    }

    /**
     * Does the event task for you, just pass the {@link BlockPlaceEvent} to
     * this
     *
     * @param event
     * @param ticksLater
     */
    public void onBlockPlace(BlockPlaceEvent event, long ticksLater) {
        request(event.getBlockReplacedState(), ticksLater);
    }

    /**
     * BlockRegen
     *
     * The Regeneration task that is scheduled whenever a regen is requested
     */
    public class BlockRegen extends BukkitRunnable {

        private final BlockState state;
        private final Location location;
        private boolean hasRun = false;

        protected BlockRegen(Location location, BlockState state) {
            this.state = state;
            this.location = location;
        }

        public Location getLocation() {
            return location;
        }

        public BlockState getState() {
            return state;
        }

        @Override
        public void cancel() {
            if (!hasRun) {
                run();
            }

            super.cancel();
        }

        @Override
        public void run() {
            getState().update(true, false);

            finish();
        }

        public void finish() {
            this.hasRun = true;
            if (toRegen.containsKey(getLocation())) {
                toRegen.remove(getLocation());
            }

            if (tasks.contains(this)) {
                tasks.remove(this);
            }
        }
    }
}
