package org.tiathefairyland.tiagrindstoneenhanced.Listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.tiathefairyland.tiagrindstoneenhanced.GrindStoneMenu;
import org.tiathefairyland.tiagrindstoneenhanced.TiaGrindstoneEnhanced;

import java.util.*;

public class GrindstoneClicks implements Listener {
    public TiaGrindstoneEnhanced plugin;

    public GrindstoneClicks(TiaGrindstoneEnhanced plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String name = event.getView().getTitle();
        String title = plugin.format(plugin.getConfig().getString("i18n.gui.title"));
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != null && name.equalsIgnoreCase(title)) {
            if(event.getClick().isShiftClick()){
                event.setCancelled(true);
            }

            int slot = event.getRawSlot();


            //      Slot to place item
            if(slot == 4){
                ItemStack currentItem = event.getCurrentItem();
                ItemStack cursorItem = event.getCursor();

                if(currentItem == null){
                    if(!checkItemAllow(cursorItem, player)){
                        event.setCancelled(true);
                        player.sendMessage(plugin.format(plugin.getConfig().getString("i18n.message.not-allow")));
                    }
                    else{
                        GrindStoneMenu.setupEnchantments(cursorItem, player, event.getInventory());
                    }
                }
                else if(cursorItem.getType() == Material.AIR){
                    GrindStoneMenu.clearEnchantments(event.getInventory());
                }
                else{
                    event.setCancelled(true);
                }
            }

            //      Remove all slot
            else if (slot == 8){
                if(event.getCurrentItem().getType() == null){
                    event.setCancelled(true);
                }
                if(event.getCurrentItem().getType() == Material.GRINDSTONE){
                    GrindStoneMenu.removeAllEnchantments(player, event.getInventory());
                    event.setCancelled(true);
                }
            }

            //      Holder slot
            else if ((slot < 18 || slot >= 45 || slot % 9 == 0 || (slot + 1) % 9 == 0) && slot <= 53) {
                event.setCancelled(true);
            }

            //      Enchanted Book slot
            else if(slot <= 53){
                ItemStack currentItem = event.getCurrentItem();
                ItemStack cursorItem = event.getCursor();

                if(cursorItem.getType() == Material.AIR && currentItem != null && currentItem.getType() == Material.ENCHANTED_BOOK){
                    GrindStoneMenu.removeEnchantments(currentItem, player, event.getInventory());
                }
                else{
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event){
        String name = event.getView().getTitle();
        String title = plugin.format(plugin.getConfig().getString("i18n.gui.title"));

        event.getInventory();
        if (name.equalsIgnoreCase(title)) {
            event.setCancelled(true);
        }
    }

    public boolean checkItemAllow(ItemStack item, Player player){
        if(item.getType() == Material.ENCHANTED_BOOK){
            return plugin.getConfig().getBoolean("limits.books.allow");
        }
        else {
            List<String> allowedList = plugin.getConfig().getStringList("limits.items.global-list");
            List<String> specialList = plugin.getConfig().getStringList("limits.items.special-list");
            String itemType = item.getType().toString().toLowerCase();
            boolean isAllowed = allowedList.stream().anyMatch(itemType::contains);
            boolean isSpecial = specialList.stream().anyMatch(itemType::contains);
            boolean permission = player.hasPermission("tiagrindstone.item.bypass");

            return isAllowed || (isSpecial && permission);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event){
        String name = event.getView().getTitle();
        String title = plugin.format(plugin.getConfig().getString("i18n.gui.title"));

        if(name.equalsIgnoreCase(title)){
            ItemStack itemStack = event.getInventory().getItem(4);
            if(itemStack != null){
                Player player = (Player) event.getPlayer();
                HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(itemStack);
                if(!remainingItems.isEmpty()){
                    player.sendMessage(plugin.format(plugin.getConfig().getString("i18n.message.item-dropped")));
                    for(ItemStack remainingItem : remainingItems.values()){
                        player.getWorld().dropItem(player.getLocation(), remainingItem);
                    }
                }
            }
        }
    }
}
