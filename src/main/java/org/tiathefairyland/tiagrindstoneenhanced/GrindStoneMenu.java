package org.tiathefairyland.tiagrindstoneenhanced;

import net.kyori.adventure.text.TranslatableComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GrindStoneMenu implements InventoryHolder {
    public static TiaGrindstoneEnhanced plugin;
    public Player player;
    private Inventory inventory;
    public static List<Integer> slotList = new ArrayList<>(Arrays.asList(19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43));

    public GrindStoneMenu(TiaGrindstoneEnhanced plugin, Player player){
        GrindStoneMenu.plugin = plugin;
        this.player = player;
        this.inventory = getInventory();
    }

    @Override
    public @NotNull Inventory getInventory(){
        String title = plugin.format(plugin.getConfig().getString("i18n.gui.title"));
        Inventory inventory = Bukkit.createInventory(null, 54, title);

        ItemStack holder = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
        ItemMeta holderMeta = holder.getItemMeta();
        holderMeta.setDisplayName(" ");
        holderMeta.setCustomModelData(1);
        holder.setItemMeta(holderMeta);

        for (int i = 0; i < 54; i++) {
            if ((i < 18 || i >= 45 || i % 9 == 0 || (i + 1) % 9 == 0) && i != 4 && i != 8){
                inventory.setItem(i, holder);
            }
        }

        if(plugin.getConfig().getBoolean("grindstone.send-message-on-open")){
            String message = plugin.getConfig().getString("i18n.message.open");
            player.sendMessage(plugin.format(message));
        }

        return inventory;
    }

    public void open(){
        player.openInventory(inventory);
    }

    public static List<ItemStack> getItemEnchantments(ItemStack item, Player player){
        Map<Enchantment, Integer> enchantments = new HashMap<>();

        if(item.getType() == Material.ENCHANTED_BOOK){
            EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) item.getItemMeta();
            enchantments = enchantmentStorageMeta.getStoredEnchants();
        }
        else{
            enchantments = item.getEnchantments();
        }

        if(!player.hasPermission("tiagrindstone.enchantment.bypass")){
            List<String> blacklist = plugin.getConfig().getStringList("enchantment-list.list");
            blacklist.stream().forEach(enchantments::remove);
        }

        List<ItemStack> enchantedBooks = new ArrayList<>();
        enchantments.forEach((enchantment, level) -> {
            ItemStack enchantedBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) enchantedBook.getItemMeta();
            meta.addStoredEnchant(enchantment, level, true);
            enchantedBook.setItemMeta(meta);
            enchantedBooks.add(enchantedBook);
        });

        return enchantedBooks;
    }

    public static void clearEnchantments(Inventory inventory){
        ItemStack empty = new ItemStack(Material.AIR);

        for(int slot : slotList){
            inventory.setItem(slot, null);
        }
        inventory.setItem(8, null);
    }

    public static void setupEnchantments(ItemStack item, Player player, Inventory inventory){
        List<ItemStack> enchantedBooks = getItemEnchantments(item, player);

        clearEnchantments(inventory);
        if(!enchantedBooks.isEmpty()){
            int pages = enchantedBooks.size() / 21 + 1;
            for(int i=0; i < Math.min(21 * pages, enchantedBooks.size()); i++){
                ItemStack book = enchantedBooks.get(i);
                ItemMeta meta = book.getItemMeta();

                Map<Enchantment, Integer> enchants;
                if(meta instanceof EnchantmentStorageMeta){
                    enchants = ((EnchantmentStorageMeta) meta).getStoredEnchants();
                }
                else{
                    enchants = book.getEnchantments();
                }

                List<String> loreTemplate = plugin.getConfig().getStringList("i18n.gui.lore");

                List<String> lores = new ArrayList<>();

                for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()){
                    Enchantment enchantment = entry.getKey();
                    int level = entry.getValue();

                    for(String loreLine : loreTemplate){
                        String costType = plugin.getConfig().getString("costs.removing-single.type");
                        loreLine = loreLine.replace("%type%", plugin.getConfig().getString("i18n.gui.types." + costType));
                        loreLine = loreLine.replace("%unit%", plugin.getConfig().getString("i18n.gui.unit." + costType));
                        loreLine = loreLine.replace("%price%", String.valueOf(countCostPrice(enchantment, level)));
                        lores.add(plugin.format(loreLine));
                    }
                }
                meta.setLore(lores);
                book.setItemMeta(meta);
                inventory.setItem(slotList.get(i), book);
            }

            ItemStack grindstone = new ItemStack(Material.GRINDSTONE);
            ItemMeta grindstoneMeta = grindstone.getItemMeta();
            grindstoneMeta.setDisplayName(plugin.format(plugin.getConfig().getString("i18n.gui.clear")));
            String costType = plugin.getConfig().getString("costs.removing-all.type");
            int price = 0;

            if(plugin.getConfig().getBoolean("costs.removing-all.count-all")){
                ItemMeta itemMeta = item.getItemMeta();
                Map<Enchantment, Integer> enchants;
                if(itemMeta instanceof EnchantmentStorageMeta){
                    enchants = ((EnchantmentStorageMeta) itemMeta).getStoredEnchants();
                }
                else{
                    enchants = item.getEnchantments();
                }
                for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()){
                    price = price + countCostPrice(entry.getKey(), entry.getValue());
                }
                price = Math.min(plugin.getConfig().getInt("costs.removing-all.count-all-max"), price);
            }
            else{
                price = plugin.getConfig().getInt("costs.removing-all.fix-amount");
            }

            List<String> loreTemplate = plugin.getConfig().getStringList("i18n.gui.lore");
            List<String> lores = new ArrayList<>();
            for(String loreLine : loreTemplate){
                loreLine = loreLine.replace("%type%", plugin.getConfig().getString("i18n.gui.types." + costType));
                loreLine = loreLine.replace("%unit%", plugin.getConfig().getString("i18n.gui.unit." + costType));
                loreLine = loreLine.replace("%price%", String.valueOf(price));
                lores.add(plugin.format(loreLine));
            }
            grindstoneMeta.setLore(lores);
            grindstone.setItemMeta(grindstoneMeta);
            inventory.setItem(8, grindstone);
        }
    }

    public static int countCostPrice(Enchantment enchantment, int level){
        List<String> overrides = plugin.getConfig().getStringList("costs.removing-single.override-price");

        for(String override : overrides){
            String[] name = override.split(":");
            if(name.length == 2 && name[0].equalsIgnoreCase(enchantment.getKey().getKey())){
                return Integer.parseInt(name[1]) * level;
            }
        }

        return plugin.getConfig().getInt("costs.removing-single.per-level") * level;
    }

    public static void removeEnchantments(ItemStack book, Player player, Inventory inventory){
        ItemStack originalItem = inventory.getItem(4);
        ItemMeta meta = book.getItemMeta();
        Map<Enchantment, Integer> enchants;
        if(meta instanceof EnchantmentStorageMeta){
            enchants = ((EnchantmentStorageMeta) meta).getStoredEnchants();
        }
        else{
            enchants = book.getEnchantments();
        }
        String priceType = plugin.getConfig().getString("costs.removing-single.type");
        int price = 0;
        Enchantment enchantment = null;
        for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()){
            enchantment = entry.getKey();
            price = countCostPrice(enchantment, entry.getValue());
        }
        if(price < 0){
            plugin.getLogger().severe(plugin.getConfig().getString("i18n.consoles.warning.negative-price"));
            return;
        }

        if(cost(priceType, price, player)){
            originalItem.removeEnchantment(enchantment);
            if(originalItem.getType() == Material.BOOK){
                ItemMeta meta1 = originalItem.getItemMeta();
                if(((EnchantmentStorageMeta) meta1).getStoredEnchants().size() == 0){
                    originalItem.setType(Material.BOOK);
                }
            }
            String enchantName = enchantment.getKey().getKey();
            enchantName = enchantName.substring(0, 1).toUpperCase() + enchantName.substring(1);
            player.sendMessage(plugin.format(plugin.getConfig().getString("i18n.message.remove").replace("%enchantment%", enchantName)));
        }
        else{
            String notEnough = plugin.getConfig().getString("i18n.message.not-enough").replace("%type%", plugin.getConfig().getString("i18n.gui.types." + priceType));
            player.sendMessage(plugin.format(notEnough));
        }
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
    }

    public static void removeAllEnchantments(Player player, Inventory inventory){
        ItemStack item = inventory.getItem(4);
        String priceType = plugin.getConfig().getString("costs.removing-all.type");
        int price = 0;

        ItemMeta itemMeta = item.getItemMeta();
        Map<Enchantment, Integer> enchants;
        if(itemMeta instanceof EnchantmentStorageMeta){
            enchants = ((EnchantmentStorageMeta) itemMeta).getStoredEnchants();
        }
        else{
            enchants = item.getEnchantments();
        }

        if(plugin.getConfig().getBoolean("costs.remaining-all.count-all")){
            for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()){
                price = price + countCostPrice(entry.getKey(), entry.getValue());
            }
            price = Math.max(plugin.getConfig().getInt("costs.removing-all.count-all-max"), price);
        }
        else{
            price = plugin.getConfig().getInt("costs.removing-all.fix-amount");
        }

        if(cost(priceType, price, player)){
            for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()){
                item.removeEnchantment(entry.getKey());
            }

            if(item.getType() == Material.ENCHANTED_BOOK){
                item.setType(Material.BOOK);
            }

            player.sendMessage(plugin.format(plugin.getConfig().getString("i18n.message.remove-all")));
        }
        else{
            String notEnough = plugin.getConfig().getString("i18n.message.not-enough").replace("%type%", plugin.getConfig().getString("i18n.gui.types." + priceType));
            player.sendMessage(plugin.format(notEnough));
        }
        player.closeInventory(InventoryCloseEvent.Reason.PLUGIN);
    }

    public static boolean cost(String priceType, int price, Player player){
        Economy economy = plugin.getVault().getEconomy();
        switch (priceType){
            case "none":
                break;
            case "give-coin":
                economy.depositPlayer(player, price);
                break;
            case "take-coin":
                if(economy.getBalance(player) < price){
                    return false;
                }
                economy.withdrawPlayer(player, price);
                break;
            case "give-exp":
                player.giveExp(price);
                break;
            case "take-exp":
                if(player.getTotalExperience() < price){
                    return false;
                }
                int exp = player.getTotalExperience();
                player.setTotalExperience(0);
                player.setLevel(0);
                player.setExp(0);
                player.giveExp(exp - price);
                break;
            case "give-exp-lvl":
                player.giveExpLevels(price);
                break;
            case "take-exp-lvl":
                if(player.getLevel() < price){
                    return false;
                }
                player.setLevel(player.getLevel() - price);
                break;
            default:
                plugin.getLogger().warning(plugin.getConfig().getString("i18n.consoles.warning.invalid-type"));
        }
        return true;
    }
}
