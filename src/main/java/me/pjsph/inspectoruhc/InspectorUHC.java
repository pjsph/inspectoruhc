package me.pjsph.inspectoruhc;

import me.pjsph.inspectoruhc.borders.BorderManager;
import me.pjsph.inspectoruhc.commands.CommandExecutor;
import me.pjsph.inspectoruhc.game.GameManager;
import me.pjsph.inspectoruhc.listeners.BeforeGameListener;
import me.pjsph.inspectoruhc.listeners.GameListener;
import me.pjsph.inspectoruhc.misc.RulesManager;
import me.pjsph.inspectoruhc.spectators.SpectatorsManager;
import me.pjsph.inspectoruhc.timer.TimerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class InspectorUHC extends JavaPlugin {
    private static InspectorUHC instance;
    private FileConfiguration config = null;

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
        this.borderManager = new BorderManager(this);
        this.spectatorsManager = new SpectatorsManager();
        this.rulesManager = new RulesManager();

        CommandExecutor executor = new CommandExecutor(this);
        for(String commandName : getDescription().getCommands().keySet()) {
            getCommand(commandName).setExecutor(executor);
            getCommand(commandName).setTabCompleter(executor);
        }

        getServer().getPluginManager().registerEvents(new BeforeGameListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
    }

    @Override
    public void onDisable() {

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
