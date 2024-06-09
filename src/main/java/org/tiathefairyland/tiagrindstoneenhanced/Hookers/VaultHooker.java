package org.tiathefairyland.tiagrindstoneenhanced.Hookers;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.tiathefairyland.tiagrindstoneenhanced.TiaGrindstoneEnhanced;

public class VaultHooker {
    public static TiaGrindstoneEnhanced plugin;
    private Economy economy = null;

    public VaultHooker(TiaGrindstoneEnhanced plugin){
        VaultHooker.plugin = plugin;
    }

    public void registerVault(){
        if(plugin.getServer().getPluginManager().getPlugin("Vault") == null){
            plugin.getLogger().warning(plugin.getConfig().getString("i18n.consoles.vault.not-found"));
        }
        else{
            RegisteredServiceProvider<Economy> registeredServiceProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
            if(registeredServiceProvider != null){
                economy = registeredServiceProvider.getProvider();
                plugin.getLogger().info(plugin.getConfig().getString("i18n.consoles.vault.hooked"));
            }
            else{
                plugin.getLogger().warning(plugin.getConfig().getString("i18n.consoles.vault.no-provider"));
            }

        }
    }

    public Economy getEconomy(){
        return economy;
    }

}
