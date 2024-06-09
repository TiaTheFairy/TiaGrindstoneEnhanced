package org.tiathefairyland.tiagrindstoneenhanced;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.tiathefairyland.tiagrindstoneenhanced.Commands.CommandsHandler;
import org.tiathefairyland.tiagrindstoneenhanced.Hookers.VaultHooker;
import org.tiathefairyland.tiagrindstoneenhanced.Listener.GrindstoneOpen;
import org.tiathefairyland.tiagrindstoneenhanced.Listener.GrindstoneClicks;

import java.util.ArrayList;
import java.util.List;

public final class TiaGrindstoneEnhanced extends JavaPlugin {
    VaultHooker vaultHooker;
    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

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
//        getServer().getPluginManager().re
    }

    public VaultHooker getVault(){
        return vaultHooker;
    }

    public TiaGrindstoneEnhanced getPlugin(){
        return this;
    }

    public String format(String string){
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public List<String> format(List<String> list){
        List<String> newList = new ArrayList<>();
        for(String string : list){
            newList.add(format(string));
        }
        return newList;
    }

    public void reloadPluginConfig(){

    }
}
