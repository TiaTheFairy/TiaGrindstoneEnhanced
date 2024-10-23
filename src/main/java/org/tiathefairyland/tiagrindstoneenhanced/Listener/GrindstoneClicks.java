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
            int slot = event.getRawSlot();

            if(event.getClick().isShiftClick()){
                ItemStack currentItem = event.getCurrentItem();
                //   Get item back with shift click
                if(slot == 4) {
                    GrindStoneMenu.clearEnchantments(event.getInventory());
                }
                //   Place item with shift click
                else if(slot > 53){
                    if(checkItemAllow(currentItem, player)){
                        if(player.getOpenInventory().getItem(4) == null || player.getOpenInventory().getItem(4).getType() == Material.AIR){
                            GrindStoneMenu.setupEnchantments(currentItem, player, event.getInventory());
                        }
                        else{
                            event.setCancelled(true);
                        }
                    }
                    else{
                        player.sendMessage(plugin.format(plugin.getConfig().getString("i18n.message.not-allow")));
                        event.setCancelled(true);
                    }
                }
                else{
                    event.setCancelled(true);
                }

                return;
            }

            //      Slot to place item
            if(slot == 4){
                ItemStack currentItem = event.getCurrentItem();
                ItemStack cursorItem = event.getCursor();
                GrindStoneMenu.changePage(0, player, event.getInventory());

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
                if(event.getInventory().getItem(4) == null || event.getInventory().getItem(4).getType() == Material.AIR){
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }

                if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.GRINDSTONE){
                    GrindStoneMenu.removeAllEnchantments(player, event.getInventory());
                }
                event.setCancelled(true);
            }

            //      Page slot
            else if (slot == 45){
                if(event.getInventory().getItem(4) == null || event.getInventory().getItem(4).getType() == Material.AIR){
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }

                if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.getMaterial(plugin.getConfig().getString("i18n.gui.page.previous.type"))){
                    GrindStoneMenu.changePage(-1, player, event.getInventory());
                }
                event.setCancelled(true);
            }
            else if (slot == 53){
                if(event.getInventory().getItem(4) == null || event.getInventory().getItem(4).getType() == Material.AIR){
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }

                if(event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.getMaterial(plugin.getConfig().getString("i18n.gui.page.next.type"))){
                    GrindStoneMenu.changePage(1, player, event.getInventory());
                }
                event.setCancelled(true);
            }

            //      Holder slot
            else if ((slot < 18 || slot >= 45 || slot % 9 == 0 || (slot + 1) % 9 == 0) && slot <= 53) {
                event.setCancelled(true);
            }

            //      Enchanted Book slot
            else if(slot <= 53){
                ItemStack currentItem = event.getCurrentItem();
                ItemStack cursorItem = event.getCursor();
                if(event.getInventory().getItem(4) == null || event.getInventory().getItem(4).getType() == Material.AIR){
                    event.getWhoClicked().closeInventory();
                    event.setCancelled(true);
                }

                if(cursorItem.getType() == Material.AIR && currentItem != null && currentItem.getType() == Material.ENCHANTED_BOOK){
                    GrindStoneMenu.removeEnchantments(currentItem, player, event.getInventory());
                }
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event){
        String name = event.getView().getTitle();
        String title = plugin.format(plugin.getConfig().getString("i18n.gui.title"));

        if (name.equalsIgnoreCase(title)) {
            event.setCancelled(true);
        }
    }

    public boolean checkItemAllow(ItemStack item, Player player){
        if(item == null || item.getType() == Material.AIR || item.getItemMeta() == null){
            return false;
        }

        List<String> blacklistName = plugin.getConfig().getStringList("limits.blacklist-names");
        if(blacklistName.stream().anyMatch(string -> item.getItemMeta().getDisplayName().toLowerCase().contains(string.toLowerCase()))){
            return false;
        }

        if(item.hasItemMeta() && item.getItemMeta().hasLore()){
            List<String> blacklistLore = plugin.getConfig().getStringList("limits.blacklist-lores");
            if(item.lore().stream().anyMatch(lore->blacklistLore.stream().anyMatch(blore->lore.toString().contains(blore)))){
                return false;
            }
        }

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
