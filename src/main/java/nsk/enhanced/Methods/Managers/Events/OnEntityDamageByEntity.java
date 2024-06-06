package nsk.enhanced.Methods.Managers.Events;

import nsk.enhanced.Civilization.Faction;
import nsk.enhanced.EnhancedFacilities;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Restriction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OnEntityDamageByEntity implements Listener {

    @EventHandler
    public void  onEntityDamagebyEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();

            if (event.getEntity() instanceof Player) {
                Player victim = (Player) event.getEntity();

                EnhancedFacilities core = PluginInstance.getInstance();
                Faction A = core.getFactionForPlayer(attacker); // A - Faction of Attacker
                Faction V = core.getFactionForPlayer(victim);   // V - Faction of Victim

                if ( A.equals( V )) {

                    for (Restriction restriction : core.getFactionForPlayer(attacker).getRestrictions()) {

                        if (restriction.getRestriction().equals(Restriction.RestrictionType.FRIENDLY_FIRE)) {
                            event.setCancelled(true);
                        }

                    }
                }
            }
        }
    }

}
