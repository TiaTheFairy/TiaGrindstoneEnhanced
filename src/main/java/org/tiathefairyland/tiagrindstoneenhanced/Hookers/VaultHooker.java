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

    public void hookVault(){
        RegisteredServiceProvider<Economy> registeredServiceProvider = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if(registeredServiceProvider != null){
            economy = registeredServiceProvider.getProvider();
            plugin.getLogger().info("Vault hooked successfully!");
        }
        else{
            plugin.getLogger().warning("Vault found. But no economy provider found. Plugin may not work as supposed");
        }
    }

    public Economy getEconomy(){
        return economy;
    }

}
