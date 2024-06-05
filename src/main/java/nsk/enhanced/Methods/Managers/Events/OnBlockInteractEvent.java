package nsk.enhanced.Methods.Managers.Events;

import nsk.enhanced.Civilization.Faction;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Restriction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.List;

public class OnBlockInteractEvent implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();

        List<Faction> factions = PluginInstance.getInstance().getAllFactions();

        for (Faction faction : factions) {
            if (!faction.isFactionPlayer(player) && faction.containLocation(location)) {
                for (Restriction restriction : faction.getRestrictions()) {
                    if (restriction.getRestriction().equals(Restriction.RestrictionType.BLOCK_BREAK)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();

        List<Faction> factions = PluginInstance.getInstance().getAllFactions();

        for (Faction faction : factions) {
            if (!faction.isFactionPlayer(player) && faction.containLocation(location)) {
                for (Restriction restriction : faction.getRestrictions()) {
                    if (restriction.getRestriction().equals(Restriction.RestrictionType.BLOCK_PLACE)) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

}
