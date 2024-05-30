package nsk.enhanced.Regions.system;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class RegionSelector implements Listener {

    private HashMap<UUID, Location> pointA = new HashMap<>();
    private HashMap<UUID, Location> pointB = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if ( item.getType() == Material.BLAZE_ROD && item.getItemMeta().displayName().equals("Region Wand")) {

            if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                pointA.put(player.getUniqueId(), event.getClickedBlock().getLocation());
                player.sendMessage("Point A: " + pointA.get(player.getUniqueId()));
            } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                pointB.put(player.getUniqueId(), event.getClickedBlock().getLocation());
                player.sendMessage("Point B: " + pointB.get(player.getUniqueId()));
            }

            event.setCancelled(true);
        }
    }

    public Location getPointA(UUID playerUUID) {
        return pointA.get(playerUUID);
    }
    public Location getPointB(UUID playerUUID) {
        return pointB.get(playerUUID);
    }

}
