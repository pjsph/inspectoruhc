package me.pjsph.inspectoruhc.commands;

import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

public abstract class AbstractCommand {

    private Map<String, AbstractCommand> subCommands = new LinkedHashMap<>();

    private Map<String, String> permissions = new LinkedHashMap<>();

    private AbstractCommand parent = null;

    public abstract void run(CommandSender sender, String[] args) throws CannotExecuteCommandException;

    public abstract List<String> tabComplete(CommandSender sender, String[] args);

    public abstract List<String> help(CommandSender sender);

    public void setParent(AbstractCommand command) {
        if(this.parent != null) {
            throw new IllegalArgumentException("Cannot define a parent twice.");
        }

        this.parent = command;
    }

    public AbstractCommand getParent() {
        return parent;
    }

    public void registerSubCommand(AbstractCommand command) {
        Command commandAnnotation = command.getClass().getAnnotation(Command.class);

        if(commandAnnotation == null) throw new IllegalArgumentException("Cannot register a command without @Command annotation. Class: " + command.getClass().getCanonicalName() + ".");

        command.setParent(this);

        String name = commandAnnotation.name();
        String permission = commandAnnotation.permission();

        if(permission == null && !commandAnnotation.inheritPermission()) {
            permission = commandAnnotation.name();
        }

        if(permission != null && permission.isEmpty() || commandAnnotation.noPermission()) {
            permission = null;
        }

        if(commandAnnotation.inheritPermission()) {
            AbstractCommand parent = this;
            while(parent != null) {
                Command parentAnnotation = parent.getClass().getAnnotation(Command.class);

                if(parentAnnotation.permission() != null && !parentAnnotation.permission().isEmpty()) {
                    permission = parentAnnotation.permission();

                    if(permission != null && !permission.isEmpty()) {
                        permission += "." + permission;
                    }
                }

                parent = parent.getParent();
            }
        }

        subCommands.put(name, command);
        permissions.put(name, permission);
    }

    public void routeCommand(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length == 0) {
            run(sender, new String[0]);
        } else {
            AbstractCommand cmd = subCommands.get(args[0]);

            if(cmd != null) {
                String permission = permissions.get(args[0]);

                if(permission == null || sender.hasPermission(permission)) {
                    cmd.routeCommand(sender, args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
                } else {
                    throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED);
                }
            } else {
                run(sender, args);
            }
        }
    }

    public List<String> routeTabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> suggestions = new LinkedList<>();

            for(String command : subCommands.keySet()) {
                String permission = permissions.get(command);

                if(permission == null || sender.hasPermission(permission)) {
                    suggestions.add(command);
                }
            }

            List<String> list = new ArrayList<>();
            for(String rawSuggestion : suggestions) {
                if(rawSuggestion.toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(rawSuggestion);
                }
            }

            suggestions = list;
            List<String> suggestionsFromThisCommand = tabComplete(sender, args);
            if(suggestionsFromThisCommand != null) {
                suggestions.addAll(suggestionsFromThisCommand);
            }

            return suggestions;
        } else {
            AbstractCommand subcommand = subCommands.get(args[0]);
            if(subcommand != null) {
                return subcommand.routeTabComplete(sender, args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
            } else {
                return tabComplete(sender, args);
            }
        }
    }

    public Map<String, AbstractCommand> getSubCommands() {
        return subCommands;
    }

    public Map<String, String> getSubcommandsPermissions() {
        return permissions;
    }

    public boolean hasSubCommands() {
        return subCommands.size() > 0;
    }

}
