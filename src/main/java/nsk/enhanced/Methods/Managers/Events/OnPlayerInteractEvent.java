package nsk.enhanced.Methods.Managers.Events;

import net.kyori.adventure.text.Component;
import nsk.enhanced.Civilization.Faction;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Restriction;
import nsk.enhanced.Regions.Territory;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class OnPlayerInteractEvent implements Listener {

    public void onRegionWand(PlayerInteractEvent event) {
        if (event.getItem() != null || !event.getItem().equals(Material.AIR)) {
            ItemStack item = event.getItem();
            ItemMeta im = item.getItemMeta();

            if (im.hasDisplayName()) {
                Component itemName = im.displayName();

                if (itemName.equals(Component.text("Region Wand"))) {

                    int h = -1;
                    switch (event.getAction()) {
                        case LEFT_CLICK_BLOCK:
                            h = 0;
                        case RIGHT_CLICK_BLOCK:
                            h = 1;
                    }

                    PluginInstance.getInstance().getRegionWandManager().setPoint(
                            h,
                            event.getPlayer(),
                            event.getClickedBlock().getLocation()
                    );

                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Action action = event.getAction();
        Location location = event.getClickedBlock().getLocation();

        Player player = event.getPlayer();
        List<Faction> factions = PluginInstance.getInstance().getAllFactions();

        switch (action) {

            case RIGHT_CLICK_BLOCK:

                for (Faction faction : factions) {

                    if (!faction.isFactionPlayer(player) && faction.containLocation(location)) {

                        for (Restriction restriction : faction.getRestrictions()) {

                            if (restriction.getRestriction().equals(Restriction.RestrictionType.INTERACT)) {
                                player.sendMessage("You are not permitted to interact on that territory.");
                                event.setCancelled(true);
                            } else /* If player is permitted to interact on Territory */ {
                                this.onRegionWand(event);
                            }
                        }

                    } else /* If player belongs to Faction */ {
                        this.onRegionWand(event);
                    }
                }

            case LEFT_CLICK_BLOCK:

                for (Faction faction : factions) {

                    if (!faction.isFactionPlayer(player) && faction.containLocation(location)) {

                        for (Restriction restriction : faction.getRestrictions()) {

                            if (restriction.getRestriction().equals(Restriction.RestrictionType.INTERACT)) {
                                player.sendMessage("You are not permitted to interact on that territory.");
                                event.setCancelled(true);
                            } else /* If player is permitted to interact on Territory */ {
                                this.onRegionWand(event);
                            }
                        }

                    } else /* If player belongs to Faction */ {
                        this.onRegionWand(event);
                    }
                }

        }
    }
}
