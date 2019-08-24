package me.pjsph.inspectoruhc.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RulesManager {

    private final List<String> rules = new ArrayList<>();

    public RulesManager() {
        rules.addAll(Arrays.asList(ChatColor.GOLD + "--------- Règles ---------",
                                    "§7Les équipes des §3Inspecteurs §7et des §cCriminels §7s'affrontent.",
                                    "§7Les §cCriminels §7ont Weakness I et connaissent l'identité des §3Inspecteurs§7.",
                                    "§7Les §cCriminels §7peuvent activer leur §cfurie (/f) §7pour gagner Force I en dévoilant leur position aux §3Inspecteurs§7.",
                                    "§7La furie peut être désactivée à tout moment. Le §cCriminel §7n'est plus visible par les §3Inspecteurs §7mais regagne Weakness I.",
                                    "§7Les §3Inspecteurs §7ne connaissent pas les §cCriminels§7. Ils reçoivent cependant un kit au cours du jeu qui les aidera.",
                                    "§7Lorsque qu'un §cCriminel §7active sa §cfurie§7, les §3Inspecteurs §7peuvent le voir se déplacer.",
                                    "§6-----------------------"
        ));
    }

    public void displayRulesTo(CommandSender receiver) {
        for(String rule : rules) {
            if(rule.isEmpty()) {
                receiver.sendMessage("");
            } else {
                receiver.sendMessage(rule);
            }
        }
    }

    public void broadcastRules() {
        Bukkit.getOnlinePlayers().forEach(this::displayRulesTo);
        displayRulesTo(Bukkit.getConsoleSender());
    }
}
