package org.tiathefairyland.tiagrindstoneenhanced;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.tiathefairyland.tiagrindstoneenhanced.Commands.CommandsHandler;
import org.tiathefairyland.tiagrindstoneenhanced.Config.ConfigManager;
import org.tiathefairyland.tiagrindstoneenhanced.Hookers.AuraSkillsHooker;
import org.tiathefairyland.tiagrindstoneenhanced.Hookers.PlayerPointsHooker;
import org.tiathefairyland.tiagrindstoneenhanced.Hookers.VaultHooker;
import org.tiathefairyland.tiagrindstoneenhanced.Listener.GrindstoneOpen;
import org.tiathefairyland.tiagrindstoneenhanced.Listener.GrindstoneClicks;

import java.util.ArrayList;
import java.util.List;

public final class TiaGrindstoneEnhanced extends JavaPlugin {
    VaultHooker vaultHooker;
    AuraSkillsHooker auraSkillsHooker = null;
    PlayerPointsHooker playerPointsHooker = null;
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

        hookPlugins();
    }

    @Override
    public void onDisable() {
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

    public void hookPlugins(){
        if(getServer().getPluginManager().isPluginEnabled("Vault")){
            vaultHooker = new VaultHooker(this);
            vaultHooker.hookVault();
        }
        else{
            getServer().getLogger().warning("Vault is not installed. Plugin may not work as supposed.");
        }


        if(getConfig().getBoolean("hooks.AuraSkills.enabled")){
            if(getServer().getPluginManager().isPluginEnabled("AuraSkills")){
                auraSkillsHooker = new AuraSkillsHooker(this);
                auraSkillsHooker.hookAuraSkill();
            }
            else{
                getServer().getLogger().severe("AuraSkills hooks is enabled in config. But we can't find AuraSkills in server.");
            }
        }
        if(getConfig().getBoolean("hooks.PlayerPoints.enabled")){
            if(getServer().getPluginManager().isPluginEnabled("PlayerPoints")){
                playerPointsHooker = new PlayerPointsHooker(this);
                playerPointsHooker.hookPlayerPoints();
            }
            else{
                getServer().getLogger().severe("PlayerPoints hooks is enabled in config. But we can't find PlayerPoints in server.");
            }
        }
    }

    public VaultHooker getVault(){
        return vaultHooker;
    }

    public AuraSkillsHooker getAuraSkills() {
        return auraSkillsHooker;
    }

    public PlayerPointsHooker getPlayerPoints(){
        return playerPointsHooker;
    }
}
