package org.tiathefairyland.tiagrindstoneenhanced;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.tiathefairyland.tiagrindstoneenhanced.Commands.CommandsHandler;
import org.tiathefairyland.tiagrindstoneenhanced.Config.ConfigManager;
import org.tiathefairyland.tiagrindstoneenhanced.Hookers.VaultHooker;
import org.tiathefairyland.tiagrindstoneenhanced.Listener.GrindstoneOpen;
import org.tiathefairyland.tiagrindstoneenhanced.Listener.GrindstoneClicks;

import java.util.ArrayList;
import java.util.List;

public final class TiaGrindstoneEnhanced extends JavaPlugin {
    VaultHooker vaultHooker;
    ConfigManager configManager;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.configUpdate();

        CommandsHandler commandHandler = new CommandsHandler(this);
        getCommand("tiagrindstoneenhanced").setExecutor(commandHandler);
        getCommand("tge").setExecutor(commandHandler);

        getServer().getPluginManager().registerEvents(new GrindstoneClicks(this), this);
        getServer().getPluginManager().registerEvents(new GrindstoneOpen(this), this);

        vaultHooker = new VaultHooker(this);
        vaultHooker.registerVault();
    }

    @Override
    public void onDisable() {
    }

    public VaultHooker getVault(){
        return vaultHooker;
    }

    public TiaGrindstoneEnhanced getPlugin(){
        return this;
    }

    public String format(String string){
        if(string.contains("&")){
            return ChatColor.translateAlternateColorCodes('&', string);
        }
        return format("&f" + string);
    }

    public List<String> format(List<String> list){
        List<String> newList = new ArrayList<>();
        for(String string : list){
            newList.add(format(string));
        }
        return newList;
    }

    public String replacePlaceholder(String string, List<String> placeholders, List<String> targets){
        for(int i=0; i< placeholders.size(); i++){
            string = string.replace(placeholders.get(i), targets.get(i));
        }
        return string;
    }

    public List<String> replacePlaceholder(List<String> stringList, List<String> placeholders, List<String> targets){
        List<String> newStringList = new ArrayList<>();
        for(String string : stringList){
            for(int i=0; i< placeholders.size(); i++){
                string = string.replace(placeholders.get(i), targets.get(i));
            }
            newStringList.add(string);
        }
        return newStringList;
    }
}
