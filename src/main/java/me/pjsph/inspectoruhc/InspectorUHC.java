package me.pjsph.inspectoruhc;

import me.pjsph.inspectoruhc.borders.BorderManager;
import me.pjsph.inspectoruhc.commands.CommandExecutor;
import me.pjsph.inspectoruhc.game.GameManager;
import me.pjsph.inspectoruhc.listeners.GameListener;
import me.pjsph.inspectoruhc.misc.RulesManager;
import me.pjsph.inspectoruhc.scoreboard.ScoreboardManager;
import me.pjsph.inspectoruhc.spectators.SpectatorsManager;
import me.pjsph.inspectoruhc.timer.TimerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Team;

import java.util.Iterator;

public class InspectorUHC extends JavaPlugin {
    private static InspectorUHC instance;
    private FileConfiguration config = null;

    private ScoreboardManager scoreboardManager = null;
    private TimerManager timerManager = null;
    private GameManager gameManager = null;
    private BorderManager borderManager = null;
    private SpectatorsManager spectatorsManager = null;
    private RulesManager rulesManager = null;

    @Override
    public void onEnable() {
        instance = this;

        this.config = getConfig();
        saveDefaultConfig();

        this.gameManager = new GameManager(this);
        this.timerManager = new TimerManager();
        this.scoreboardManager = new ScoreboardManager(this);
        this.borderManager = new BorderManager(this);
        this.spectatorsManager = new SpectatorsManager();
        this.rulesManager = new RulesManager();

        Iterator<Team> it = scoreboardManager.getScoreboard().getTeams().iterator();
        while(it.hasNext()) {
            it.next().unregister();
        }

        scoreboardManager.getScoreboard().registerNewObjective("PlayerHealth", "health").setDisplaySlot(DisplaySlot.PLAYER_LIST);

        CommandExecutor executor = new CommandExecutor(this);
        for(String commandName : getDescription().getCommands().keySet()) {
            getCommand(commandName).setExecutor(executor);
            getCommand(commandName).setTabCompleter(executor);
        }

        getServer().getPluginManager().registerEvents(new GameListener(this), this);
    }

    @Override
    public void onDisable() {

    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public BorderManager getBorderManager() {
        return borderManager;
    }

    public SpectatorsManager getSpectatorsManager() {
        return spectatorsManager;
    }

    public RulesManager getRulesManager() {
        return rulesManager;
    }

    public static InspectorUHC get() {
        return instance;
    }
}
