package org.tiathefairyland.tiagrindstoneenhanced.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.tiathefairyland.tiagrindstoneenhanced.GrindStoneMenu;
import org.tiathefairyland.tiagrindstoneenhanced.TiaGrindstoneEnhanced;

public class GrindstoneOpen implements Listener {
    public TiaGrindstoneEnhanced plugin;

    public GrindstoneOpen(TiaGrindstoneEnhanced plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void openMenu(PlayerInteractEvent event){
        if(!plugin.getConfig().getBoolean("grindstone.override-grindstone")){
            return;
        }
        event.setCancelled(true);

        Player player = event.getPlayer();
        if(event.getClickedBlock().getType() == Material.GRINDSTONE){
            if(plugin.getConfig().getBoolean("grindstone.player-with-permission")){
                if(player.hasPermission("tiagrindstone.grindstone")){
                    GrindStoneMenu grindStoneMenu = new GrindStoneMenu(plugin, player);
                    grindStoneMenu.open();
                }
                else{
                    player.sendMessage(plugin.format(plugin.getConfig().getString("i18n.message.permission-grindstone")));
                }
            }
            else{
                GrindStoneMenu grindStoneMenu = new GrindStoneMenu(plugin, player);
                grindStoneMenu.open();
            }
        }
    }
}
