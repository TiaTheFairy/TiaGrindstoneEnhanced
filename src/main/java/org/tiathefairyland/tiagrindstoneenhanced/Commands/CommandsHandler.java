package org.tiathefairyland.tiagrindstoneenhanced.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.tiathefairyland.tiagrindstoneenhanced.GrindStoneMenu;
import org.tiathefairyland.tiagrindstoneenhanced.TiaGrindstoneEnhanced;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandsHandler implements CommandExecutor, TabCompleter {
    public TiaGrindstoneEnhanced plugin;

    public CommandsHandler(TiaGrindstoneEnhanced plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String label, String[] args) {
        if(label.equalsIgnoreCase("tiagrindstoneenhanced") || label.equalsIgnoreCase("tge")){
            if(sender instanceof Player){
                if(args.length < 1){
                    sender.sendMessage("/tge open");
                    return true;
                }

                if(args[0].equalsIgnoreCase("open")){
                    if(sender.hasPermission("tiagrindstone.command.open")){
                        GrindStoneMenu grindStoneMenu = new GrindStoneMenu(plugin, (Player) sender);
                        grindStoneMenu.open();
                    }
                }

                else if(args[0].equalsIgnoreCase("reload")){
                    if(sender.isOp()){
                        File configFile = new File(plugin.getDataFolder(), "config.yml");
                        if(!configFile.exists()){
                            plugin.saveDefaultConfig();
                            sender.sendMessage(plugin.format(plugin.getConfig().getString("i18n.message.reload-new")));
                        }
                        plugin.reloadConfig();
                        sender.sendMessage(plugin.format(plugin.getConfig().getString("i18n.message.reload")));
                    }
                }
            }
            else{
                plugin.getLogger().info("This command can't be run in console.");
            }

            return true;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, @NotNull Command command, String s, String[] strings) {
        List<String> list = new ArrayList<>();
        list.add("open");
        if(commandSender.isOp()){
            list.add("reload");
        }

        return list;
    }
}
