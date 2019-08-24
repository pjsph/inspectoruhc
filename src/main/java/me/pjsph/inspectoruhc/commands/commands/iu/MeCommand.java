package me.pjsph.inspectoruhc.commands.commands.iu;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(name = "me")
public class MeCommand extends AbstractCommand {

    private InspectorUHC plugin;

    public MeCommand(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(!(sender instanceof Player)) throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.ONLY_AS_A_PLAYER, this);
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NEED_DOC, this);
        } else {
            if(!plugin.getGameManager().hasStarted()) {
                sender.sendMessage(ChatColor.RED + "Le jeu n'a pas encore démarré.");
            } else if(!plugin.getGameManager().isRolesActivated()) {
                sender.sendMessage(ChatColor.RED + "Vous n'avez ni d'équipe ni de kit pour l'instant.");
            } else if(!plugin.getGameManager().isKitsActivated()) {
                sender.sendMessage("Déjà oublié ? Vous êtes dans l'équipe : " + (Team.getTeamForPlayer((Player) sender) != null ? Team.getTeamForPlayer((Player) sender).getName() : ""));
            } else {
                String teamName = Team.getTeamForPlayer((Player) sender) != null ? Team.getTeamForPlayer((Player) sender).getName() : "";
                sender.sendMessage("Déjà oublié ? Vous êtes dans l'équipe : " + teamName);
                if(teamName == "Inspecteurs") {
                    sender.sendMessage("Votre kit est le suivant : " + Kit.getKit(((Player) sender).getUniqueId()).getName());
                    sender.sendMessage(Kit.getKit(((Player) sender).getUniqueId()).getDescription());
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList(ChatColor.YELLOW + "------- En jeu -------",
                ChatColor.ITALIC + "/iu me" + ChatColor.GRAY + " pour connaître son équipe et/ou son kit."
        );
    }
}
