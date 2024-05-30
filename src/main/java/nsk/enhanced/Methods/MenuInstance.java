package nsk.enhanced.Methods;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MenuInstance {

    private final Inventory inventory;

    public MenuInstance() {
        inventory = Bukkit.createInventory(null, 9 * 3, Component.text("Main menu"));

        initializeOptions();
    }

    private void initializeOptions() {
        addItem(createMenuItem(Material.DIAMOND, "Item on slot 13"), 13);
    }

    private ItemStack createMenuItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        item.setItemMeta(meta);
        return item;
    }

    private void addItem(ItemStack item, int slot) {
        inventory.setItem(slot, item);
    }

    public void openMenu(Player player) {
        player.openInventory(inventory);
    }

    public void handleMenuClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        ItemMeta meta = clickedItem.getItemMeta();

        if (meta.hasDisplayName() && Component.text("Menu item 1").equals(meta.displayName())) {
            player.sendMessage("Clicked on Menu item 1");
        }
    }
}
