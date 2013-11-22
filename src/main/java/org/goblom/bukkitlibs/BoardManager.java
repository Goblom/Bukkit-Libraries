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

package org.goblom.bukkitlibs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


/**
 *
 * @author Goblom
 */
public class BoardManager {
    
    private final Plugin plugin;
    private BukkitTask task;
    
    private final String boardName;
    private final Scoreboard scoreBoard;
    private final Objective mainObjective;
    
    private final List<String> players = new ArrayList<String>();
    private DisplaySlot slot;
    
    public BoardManager(Plugin plugin, String scoreBoardName, DisplaySlot slot) {
        this.plugin = plugin;
        this.boardName = scoreBoardName;
        this.slot = slot;
        this.scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.mainObjective = scoreBoard.registerNewObjective("main", ObjectiveCriteria.DUMMY.getName());
        
        mainObjective.setDisplayName(scoreBoardName);
        mainObjective.setDisplaySlot(slot);
        
        startChecking();
    }
    
    public void Testing() {
        
    }
    
    public String getBoardName() {
        return boardName;
    }
    
    public DisplaySlot getDisplaySlot() {
        return slot;
    }
    
    public void setDisplaySlot(DisplaySlot slot) {
        this.slot = slot;
    }
    
    public List<String> getPlayers() {
        return players;
    }
    
    public final void startChecking() {
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, new BoardChecker(), 0, 20); //Check every second
    }
    
    public void stopChecking() {
        task.cancel();
        this.task = null;
    }
    
    public Set<Team> getTeams() {
        return scoreBoard.getTeams();
    }
    
    public Team getTeam(String teamName) {
        return scoreBoard.getTeam(teamName);
    }
    
    public void registerTeam(String teamName) {
        scoreBoard.registerNewTeam(teamName);
    }
    
    public Set<Objective> getObjectives() {
        return scoreBoard.getObjectives();
    }
    
    public Objective getObjective(String objectiveName) {
        return scoreBoard.getObjective(objectiveName);
    }
    
    public void registerObjective(String objective, ObjectiveCriteria criteria) {
        scoreBoard.registerNewObjective(objective, criteria.getName());
    }
    
    public Objective getObjectiveInSlot(DisplaySlot slot) {
        return scoreBoard.getObjective(slot);
    }
    
    public void setScoreboard(Player player) {
        player.setScoreboard(scoreBoard);
        players.add(player.getName());
    }
    
    public void removeScoreboard(Player player, boolean blank) {
        if (blank) player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        else player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }
    
    public void addObjectiveScore(String score) {
        mainObjective.getScore(Bukkit.getOfflinePlayer(score));
    }
    
    public void addObjectiveScore(Objective objective, String score) {
        objective.getScore(Bukkit.getOfflinePlayer(score));
    }
    
    public Score getObjectiveScore(String score) {
        return mainObjective.getScore(Bukkit.getOfflinePlayer(score));
    }
    
    public Score getObjectiveScore(Objective objective, String score) {
        return objective.getScore(Bukkit.getOfflinePlayer(score));
    }
    
    public void setObjectiveScore(String scoreName, int setScore) {
        getObjectiveScore(scoreName).setScore(setScore);
    }
    
    public void setObjectiveScore(Objective objective, String scoreName, int setScore) {
        getObjectiveScore(objective, scoreName).setScore(setScore);
    }
    
    public Set<Score> getScores(String score) {
        return scoreBoard.getScores(Bukkit.getOfflinePlayer(score));
    }
    
    public static enum ObjectiveCriteria {
        //pre 1.7
        DUMMY("dummy"), DEATH_COUNT("deathCount"), HEALTH("health"),
        PLAYER_KILL_COUNT("playerKillCount"), TOTAL_KILL_COUNT("totalKillCount");
        private final String name;
        private ObjectiveCriteria(String name) { this.name = name; }
        public String getName() { return name; }
        public String getNameWithID(int id) { return name + "." + id; }
    }
    
    private final class BoardChecker implements Runnable {
        public void run() {
            for (String playerName : players) {
                Player player = Bukkit.getPlayer(playerName);
                if (player != null) {
                    if (!player.getScoreboard().equals(scoreBoard)) { // if (player.getScoreboard() != scoreBoard) {
                        players.remove(playerName);
                    }
                } else players.remove(playerName);
            }
        }
    }
}
