package nsk.enhanced.Methods.Managers.Buildings;

import nsk.enhanced.Buildings.Basic.Sawmill;
import nsk.enhanced.Methods.PluginInstance;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

public class SawmillManager implements Listener {

    private final List<Sawmill> sawmills = new ArrayList<Sawmill>();

    public void addSawmill(Sawmill sawmill) {
        sawmills.add(sawmill);
    }
    public void removeSawmill(Sawmill sawmill) {
        sawmills.remove(sawmill);
    }

    @EventHandler
    public void onSeverLoad(ServerLoadEvent event) {
        this.scheduledDailyTask();
    }

    public void scheduledDailyTask() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer(PluginInstance.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                World world = Bukkit.getWorld("world");
                long time = world.getTime();

                if (time >= 1000 && time < 1100) {
                    for (Sawmill sawmill : sawmills) {
                        sawmill.addWoodToChest();
                    }
                }
            }
        }, 0L, 100L);
    }
}
