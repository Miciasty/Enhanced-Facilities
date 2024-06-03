package nsk.enhanced.Methods.Managers.Regions;

import nsk.enhanced.Methods.PluginInstance;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class RegionWandManager implements Listener {

    private final HashMap<UUID, Location> pointA  = new HashMap<>();
    private final HashMap<UUID, Location> pointB = new HashMap<>();

    public RegionWandManager() {}

    public void setPoint(int hand, Player player, Location location) {
        UUID uuid = player.getUniqueId();

        try {
            if (hand == 0) {
                pointA.put(uuid, location);
            } else if (hand == 1){
                pointB.put(uuid, location);
            } else {
                throw new IllegalArgumentException("Invalid hand");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public Location getPointA(UUID uuid) {
        return pointA.get(uuid);
    }
    public Location getPointB(UUID uuid) {
        return pointB.get(uuid);
    }

    public HashMap<UUID, Location> getPointA() {
        return pointA;
    }
    public HashMap<UUID, Location> getPointB() {
        return pointB;
    }

}
