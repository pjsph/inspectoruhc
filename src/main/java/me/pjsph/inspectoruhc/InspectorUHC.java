package me.pjsph.inspectoruhc;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.pjsph.com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import me.pjsph.inspectoruhc.borders.BorderManager;
import me.pjsph.inspectoruhc.commands.CommandExecutor;
import me.pjsph.inspectoruhc.events.UpdatePrefixEvent;
import me.pjsph.inspectoruhc.game.GameManager;
import me.pjsph.inspectoruhc.game.IUPlayer;
import me.pjsph.inspectoruhc.listeners.BeforeGameListener;
import me.pjsph.inspectoruhc.listeners.ChatListener;
import me.pjsph.inspectoruhc.listeners.GameListener;
import me.pjsph.inspectoruhc.listeners.KitsListener;
import me.pjsph.inspectoruhc.misc.MOTDManager;
import me.pjsph.inspectoruhc.misc.RulesManager;
import me.pjsph.inspectoruhc.spawns.SpawnsManager;
import me.pjsph.inspectoruhc.spectators.SpectatorsManager;
import me.pjsph.inspectoruhc.timer.TimerManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class InspectorUHC extends JavaPlugin {
    private static InspectorUHC instance;
    private FileConfiguration config = null;

    private TimerManager timerManager = null;
    private GameManager gameManager = null;
    private BorderManager borderManager = null;
    private SpawnsManager spawnsManager = null;
    private SpectatorsManager spectatorsManager = null;
    private RulesManager rulesManager = null;
    private MOTDManager motdManager = null;

    @Override
    public void onEnable() {
        instance = this;

        this.config = getConfig();
        saveDefaultConfig();

        this.gameManager = new GameManager(this);
        this.timerManager = new TimerManager();
        this.borderManager = new BorderManager(this);
        this.spawnsManager = new SpawnsManager(this);
        this.spectatorsManager = new SpectatorsManager();
        this.rulesManager = new RulesManager();
        this.motdManager = new MOTDManager(this);

        motdManager.updateMOTDBeforeStart();

        CommandExecutor executor = new CommandExecutor(this);
        for(String commandName : getDescription().getCommands().keySet()) {
            getCommand(commandName).setExecutor(executor);
            getCommand(commandName).setTabCompleter(executor);
        }

        getServer().getPluginManager().registerEvents(new BeforeGameListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(this), this);
        getServer().getPluginManager().registerEvents(new KitsListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);

        final ItemStack goldenHeadItem = new ItemStack(Material.GOLDEN_APPLE, 1);
        final ItemMeta meta = goldenHeadItem.getItemMeta();
        meta.setDisplayName("§bGolden Head");
        goldenHeadItem.setItemMeta(meta);
        final ShapedRecipe goldenHead = new ShapedRecipe(goldenHeadItem);
        goldenHead.shape("GGG", "GHG", "GGG");
        goldenHead.setIngredient('G', Material.GOLD_INGOT);
        goldenHead.setIngredient('H', Material.SKULL_ITEM, SkullType.PLAYER.ordinal());
        getServer().addRecipe(goldenHead);
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

    public SpawnsManager getSpawnsManager() {
        return spawnsManager;
    }

    public SpectatorsManager getSpectatorsManager() {
        return spectatorsManager;
    }

    public RulesManager getRulesManager() {
        return rulesManager;
    }

    public MOTDManager getMOTDManager() {
        return motdManager;
    }

    public static InspectorUHC get() {
        return instance;
    }
}
