package nsk.enhanced.Buildings.Basic;

import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Region;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@Entity
@DiscriminatorValue("sawmill")
public class Sawmill extends Building {

    private final int radius = 20;

    private final double dropChance = calculateDropChance();

    private final int dailyGenerated = calculateDailyWood();

    public Sawmill(int level, int durability, ArrayList<Region> regions) {
        super(level, durability, regions);
    }

    public Sawmill() {}

    // --- --- --- --- // Leveling // --- --- --- --- //

    public void levelUp() {
        try {
            this.level++;
            CompletableFuture.allOf(
                    PluginInstance.getInstance().saveEntityAsync(this)
            ).exceptionally(e -> {
                this.level--;
                throw new IllegalStateException("Query failed! ", e);
            });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }
    public void levelDown() {
        if (this.level > 1) {
            try {
                this.level--;
                CompletableFuture.allOf(
                        PluginInstance.getInstance().saveEntityAsync(this)
                ).exceptionally(e -> {
                    this.level++;
                    throw new IllegalStateException("Query failed! ", e);
                });
            } catch (Exception e) {
                PluginInstance.getInstance().consoleError(e);
            }
        }
    }

    // --- --- --- --- // Methods // --- --- --- --- //

    private double calculateDropChance() {
        final double baseChance = 15.0;
        final double increment = 5.0;
        double chance = baseChance + (getLevel() - 1) * increment;
        return Math.min(chance, 100.0);
    }

    private int calculateDailyWood() {
        final int min = 2;
        final int max = 5 + (getLevel() - 1) * 8;
        return min + (int) (Math.random() * (max - min + 1));
    }

    // --- --- --- --- // Sawmill Logic // --- --- --- --- //

    public void addWoodToChest() {
        try {
            PluginInstance.getInstance().lookForBlock(getRegions(), Material.CHEST).thenAccept(block -> {
                if (block != null) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Chest chest = (Chest) block.getState();
                            addWoodToChest(chest);
                        }
                    }.runTask(PluginInstance.getInstance());
                }
            }).exceptionally(e -> {
                throw new IllegalStateException("Error adding wood to chest in " + this.getFaction().getName() + " Sawmill " + this.getId());
            });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    private void addWoodToChest(Chest chest) {
        ItemStack woodStack = new ItemStack(Material.OAK_LOG, dailyGenerated);
        chest.getInventory().addItem(woodStack);
        chest.update();
    }

    // --- --- --- --- // Sawmill Logic // --- --- --- --- //


}
