package me.pjsph.inspectoruhc.commands;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.commands.commands.IUCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.*;

public class CommandExecutor implements TabExecutor {
    private InspectorUHC plugin;

    private Map<String, AbstractCommand> mainCommands = new LinkedHashMap<>();

    private Map<String, String> mainCommandsPermissions = new LinkedHashMap<>();

    public CommandExecutor(InspectorUHC plugin) {
        this.plugin = plugin;

        registerCommand(new IUCommand(plugin));
    }

    private void registerCommand(AbstractCommand command) {
        Command commandAnnotation = command.getClass().getAnnotation(Command.class);

        if(commandAnnotation == null) throw new IllegalArgumentException("Cannot register a command without @Command annotation. Class: " + command.getClass().getCanonicalName() + ".");

        mainCommands.put(commandAnnotation.name(), command);

        String permission = commandAnnotation.permission();

        if(commandAnnotation.noPermission()) {
            permission = null;
        } else if(permission != null && permission.isEmpty()) {
            permission = commandAnnotation.name();
        }

        mainCommandsPermissions.put(commandAnnotation.name(), permission);
    }

    public void displayHelp(CommandSender sender, AbstractCommand command, boolean isAnError) {
        if(command.hasSubCommands()) {
            List<String> help = new LinkedList<>();

            List<String> rootHelp = command.help(sender);
            if(rootHelp != null) {
                help.addAll(rootHelp);
            }

            for(Map.Entry<String, AbstractCommand> subCommand : command.getSubCommands().entrySet()) {
                List<String> subHelp = subCommand.getValue().help(sender);
                String permission = command.getSubcommandsPermissions().get(subCommand.getKey());

                if(subHelp != null && subHelp.size() > 0 && (permission == null || sender.hasPermission(permission))) {
                    help.addAll(subHelp);
                }
            }

            displayHelp(sender, help, isAnError);
        } else {
            List<String> help = command.help(sender);
            if(help == null) help = Arrays.asList("Pas d'aide disponible pour cette commande.");

            displayHelp(sender, help, isAnError);
        }
    }

    public void displayHelp(CommandSender sender, List<String> help, boolean isAnError) {
        sender.sendMessage(ChatColor.YELLOW + "--------------------");

        if(!isAnError) {
            sender.sendMessage(ChatColor.YELLOW + plugin.getDescription().getDescription() + " - version " + plugin.getDescription().getVersion());
            sender.sendMessage("Utilisation : " + ChatColor.ITALIC + "/iu <command>");
            sender.sendMessage(" ");
        }

        if(help != null) {
            for(String line : help) {
                if(line != null && !line.isEmpty()) {
                    sender.sendMessage(line);
                }
            }
        }

        sender.sendMessage(ChatColor.YELLOW + "--------------------");

        if(isAnError) {
            sender.sendMessage(ChatColor.RED + "La commande executée n'existe pas.");
            sender.sendMessage(ChatColor.RED + "L'aide a été affichée ci-dessus.");
            sender.sendMessage(ChatColor.YELLOW + "--------------------");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        AbstractCommand abstractCommand = mainCommands.get(command.getName());
        if(abstractCommand == null) {
            return false;
        }

        try {
            String permission = mainCommandsPermissions.get(command.getName());
            if (permission != null && !sender.hasPermission(permission)) {
                throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED);
            }

            abstractCommand.routeCommand(sender, args);

        } catch(CannotExecuteCommandException e) {
            switch(e.getReason()) {
                case NOT_ALLOWED:
                    sender.sendMessage(ChatColor.RED + "Vous n'êtes pas autorisé à effectuer cette commande.");
                    break;

                case ONLY_AS_A_PLAYER:
                    sender.sendMessage(ChatColor.RED + "La commande doit être executée en tant que joueur.");
                    break;

                case BAD_USE:
                case NEED_DOC:
                    displayHelp(sender, e.getOrigin() != null ? e.getOrigin() : abstractCommand, e.getReason() == CannotExecuteCommandException.Reason.BAD_USE);
                    break;

                case UNKNOW:
                    break;
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        AbstractCommand abstractCommand = mainCommands.get(command.getName());

        return abstractCommand.routeTabComplete(sender, args);
    }

    public Map<String, AbstractCommand> getMainCommands() {
        return mainCommands;
    }
}
