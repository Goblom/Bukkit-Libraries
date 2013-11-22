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
    
    private final String boardName;
    private final Scoreboard scoreBoard;
    private final Objective mainObjective;
    
    private final List<String> players = new ArrayList<String>();
    private DisplaySlot slot;
    
    public BoardManager(String scoreBoardName, DisplaySlot slot) {
        this.boardName = scoreBoardName;
        this.slot = slot;
        this.scoreBoard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.mainObjective = scoreBoard.registerNewObjective("main", ObjectiveCriteria.DUMMY.getName());
        
        mainObjective.setDisplayName(scoreBoardName);
        mainObjective.setDisplaySlot(slot);
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
    
    public void checkPlayers() {
        for (String playerName : players) {
            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                if (!player.getScoreboard().equals(scoreBoard)) { // if (player.getScoreboard() != scoreBoard) {
                    players.remove(playerName);
                }
            } else players.remove(playerName);
        }
    }
    
    public List<String> getPlayers() {
        checkPlayers();
        return players;
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
        PLAYER_KILL_COUNT("playerKillCount"), TOTAL_KILL_COUNT("totalKillCount"),
        
        //post 1.7
        achievement_MAKE_BREAD("achievement.makeBread"), achievement_BAKE_CAKE("achievement.bakeCake"),  achievement_DIAMONDS_TO_YOU("achievement.diamondsToYou"), 
        achievement_KILL_COW("achievement.killCow"), achievement_PORTAL("achievement.portal"), achievement_BUILD_FURNACE("achievement.buildFurnace"), 
        achievement_BUILD_SWORD("achievement.buildSword"), achievement_COOK_FISH("achievement.cookFish"), achievement_ENCHANTMENTS("achievement.enchantments"), 
        achievement_MINE_WOOD("achievement.mineWood"), achievement_OPEN_INVENTORY("achievement.openInventory"), achievement_EXPLORE_ALL_BIOMES("achievement.exploreAllBiomes"), 
        achievement_BUILD_WORKBENCH("achievement.buildWorkBench"), achievement_THE_END("achievement.theEnd"), achievement_BLAZE_ROD("achievement.blazeRod"), 
        achievement_SPAWN_WITHER("achievement.spawnWither"), achievement_BUILD_BETTER_PICKAXE("achievement.buildBetterPickaxe"), achievement_ACQUIRE_IRON("achievement.acquireIron"), 
        achievement_THE_END2("achievement.theEnd2"), achievement_BOOKCASE("achievement.bookcase"), achievement_FLYING_PIG("achievement.flyPig"), 
        achievement_GHAST("achievement.ghast"), achievement_SNIPE_SKELETON("achievement.snipeSkeleton"), achievement_DIAMONDS("achievement.diamonds"), 
        achievement_KILL_WITHER("achievement.killWither"), achievement_FULL_BEACON("achievement.fullBeacon"), achievement_BUILD_HOE("achievement.buildHoe"), 
        achievement_BREED_COW("achievement.breedCow"), achievement_ON_A_RAIL("achievement.onARail"), achievement_OVERKILL("achievement.overkill"), 
        achievement_KILL_ENEMY("achievement.killEnemy"), achievement_POTION("achievement.potion"), achievement_BUILD_PICKAXE("achievement.buildPickaxe"),
        
        stat_DAMAGE_DEALT("stat.damageDealt"), stat_DAMAGE_TAKE("stat.damageTaken"), stat_LEAVE_GAME("stat.leaveGame"), 
        stat_MINECART_ONE_CM("stat.minecartOneCm"), stat_SWIM_ONE_CM("stat.swimOneCm"), stat_WALK_ONE_CM("stat.walkOneCm"), 
        stat_HORSE_ONE_CM("stat.horseOneCm"), stat_PIG_ONE_CM("stat.pigOneCm"), stat_FLY_ONE_CM("stat.flyOneCm"), 
        stat_BOAT_ONE_CM("stat.boatOneCm"), stat_FALL_ONE_CM("stat.fallOneCm"), stat_CLIMB_ONE_CM("stat.climbOneCm"), 
        stat_DIVE_ONE_CM("stat.diveOneCm"), stat_FISH_CAUGHT("stat.fishCaught"), stat_JUNK_FISHED("stat.junkFished"), 
        stat_TREASURE_FISHED("stat.treasureFished"), stat_PLAY_ONE_MINUE("stat.playOneMinute"), stat_PLAYER_KILLS("stat.playerKills"), 
        stat_MOB_KILLS("stat.mobKills"), stat_ANIMALS_BRED("stat.animalsBred"), stat_JUMP("stat.jump"), 
        stat_DROP("stat.drop"), stat_DEATHS("stat.deaths"),
        
        stat_killEntity_SILVERFISH("stat.killEntity.Silverfish"), stat_killEntity_ZOMBIE("stat.killEntity.Zombie"), stat_killEntity_BLAZE("stat.killEntity.Blaze"), 
        stat_killEntity_PIG("stat.killEntity.Pig"), stat_killEntity_CREEPER("stat.killEntity.Creeper"), stat_killEntity_COW("stat.killEntity.Cow"), 
        stat_killEntity_GHAS("stat.killEntity.Ghast"), stat_killEntity_WHICH("stat.killEntity.Witch"), stat_killEntity_SQUID("stat.killEntity.Squid"), 
        stat_killEntity_SPIDER("stat.killEntity.Spider"), stat_killEntity_VILLATER("stat.killEntity.Villager"), stat_killEntity_ENDERMAN("stat.killEntity.Enderman"), 
        stat_killEntity_LAVA_SLIME("stat.killEntity.LavaSlime"), stat_killEntity_PIG_ZOMBIE("stat.killEntity.PigZombie"), stat_killEntity_WOLF("stat.killEntity.Wolf"), 
        stat_killEntity_SHEEP("stat.killEntity.Sheep"), stat_killEntity_CHIKEN("stat.killEntity.Chicken"), stat_killEntity_SLIME("stat.killEntity.Slime"), 
        stat_killEntity_SLELETON("stat.killEntity.Skeleton"), stat_killEntity_BAT("stat.killEntity.Bat"), stat_killEntity_MUSHROOM_COW("stat.killEntity.MushroomCow"), 
        stat_killEntity_CAVE_SPIDER("stat.killEntity.CaveSpider"), stat_killEntity_HORSE("stat.killEntity.EntityHorse"), // Horse ??? Wiki might be mispelled
        stat_killEntity_OCELOT("stat.killEntity.Ozelot"), //Ocelot ??? Wiki might be mispelled
        
        stat_entityKilledBy_WOLF("stat.entityKilledBy.Wolf"), stat_entityKilledBy_ENDERMAN("stat.entityKilledBy.Enderman"), stat_entityKilledBy_SLIME("stat.entityKilledBy.Slime"), 
        stat_entityKilledBy_LAVA_SLIME("stat.entityKilledBy.LavaSlime"), stat_entityKilledBy_SPIDER("stat.entityKilledBy.Spider"), stat_entityKilledBy_CREEPER("stat.entityKilledBy.Creeper"), 
        stat_entityKilledBy_BAT("stat.entityKilledBy.Bat"), stat_entityKilledBy_SQUID("stat.entityKilledBy.Squid"), stat_entityKilledBy_PIG_ZOMBIE("stat.entityKilledBy.PigZombie"), 
        stat_entityKilledBy_SILVERHIST("stat.entityKilledBy.Silverfish"), stat_entityKilledBy_SKELETON("stat.entityKilledBy.Skeleton"), stat_entityKilledBy_WHICH("stat.entityKilledBy.Witch"), 
        stat_entityKilledBy_PIG("stat.entityKilledBy.Pig"), stat_entityKilledBy_BLAZE("stat.entityKilledBy.Blaze"), stat_entityKilledBy_SHEEP("stat.entityKilledBy.Sheep"), 
        stat_entityKilledBy_MUSHROOM_COW("stat.entityKilledBy.MushroomCow"), stat_entityKilledBy_CAVE_SPIDER("stat.entityKilledBy.CaveSpider"), stat_entityKilledBy_VILLAGER("stat.entityKilledBy.Villager"), 
        stat_entityKilledBy_ZOMBIE("stat.entityKilledBy.Zombie"), stat_entityKilledBy_CHICKEN("stat.entityKilledBy.Chicken"), stat_entityKilledBy_COW("stat.entityKilledBy.Cow"), 
        stat_entityKilledBy_GHAST("stat.entityKilledBy.Ghast"), stat_entityKilledBy_HORSE("stat.entityKilledBy.EntityHorse"), // Horse ??? Wiki might be mispelled
        stat_entityKilledBy_OCELOT("stat.entityKilledBy.Ozelot") //Ocelot ??? Wiki might be mispelled
        
        /**
         * Missing... http://minecraft.gamepedia.com/Scoreboard#Objectives
         * 
         * - stat.craftItem
         * - stat.useItem
         * - stat.breakItem
         * - stat.mineBlock
         */
        ;
        private final String name;
        private ObjectiveCriteria(String name) { this.name = name; }
        public String getName() { return name; }
        public String getStat() { return "stat." + name; }
        public String getStatWithID(int id) { return name + "." + id; }
        public String getStatWithID(String stat, int id) { return "stat." + stat + "." + id; }
        
    }
}
