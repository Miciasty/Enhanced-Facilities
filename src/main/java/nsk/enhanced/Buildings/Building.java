package nsk.enhanced.Buildings;

import nsk.enhanced.Civilization.Faction;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Region;
import nsk.enhanced.Regions.Restriction;
import nsk.enhanced.Regions.Territory;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "building_type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "faction_buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    @Column(nullable = false)
    protected String type;

    @Column(nullable = false)
    protected String level;

    @Column(nullable = false)
    protected int durability;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "building_id")
    protected List<Region> regions;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "building_id")
    private List<Restriction> restrictions;

    // --- --- --- --- // CONSTRUCTOR // --- --- --- --- //

    public Building(String type, int id, String level, int durability, List<Region> regions) {
        setType(type);
        setId(id);
        setLevel(level);
        setDurability(durability);

        this.regions = new ArrayList<>();
        this.regions.addAll(regions);
    }

    public Building(String type, int id, String level, int durability, Region region) {
        setType(type);
        setId(id);
        setLevel(level);
        setDurability(durability);

        this.regions = new ArrayList<>();
        this.regions.add(region);
    }

    public Building() { /* Pusty konstruktor wymagany przez JPA */ }

    // --- --- --- --- // Setter's / Getter's // --- --- --- --- //

    protected void setType(String type) { this.type = type; }
    public String getType() { return type; }

    protected void setId(int id) { this.id = id; }
    public int getId() { return id; }

    protected void setLevel(String level) { this.level = level; }
    public String getLevel() { return level; }

    protected void setDurability(int durability) { this.durability = durability; }
    public int getDurability() { return durability; }

    // --- --- --- --- // Regions // --- --- --- --- //

    public void addRegion(Region region, Player player) {
        for (Region r : regions) {
            if (r.overlaps(region)) {
                player.sendMessage("This region overlaps already existing region in this building.");
                return;
            }
        }

        try {
            regions.add(region);

            CompletableFuture.allOf(
                    PluginInstance.getInstance().saveFactionFromBuilding(this),
                    PluginInstance.getInstance().saveEntityAsync(this),
                    PluginInstance.getInstance().saveEntityAsync(region)
            ).thenRun(() -> {
                player.sendMessage("This region was successfully added to this building.");
            }).exceptionally(e -> {
                regions.remove(region);
                player.sendMessage("Query failed! This region was not added to this building.");
                throw new IllegalStateException("Query failed! ", e);
            });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }
    public void addRegion(Region region) {
        try {
            for (Region r : regions) {
                if (r.overlaps(region)) {
                    throw new IllegalArgumentException("This region overlaps already existing region in this building.");
                }
            }
            regions.add(region);
            CompletableFuture.allOf(
                    PluginInstance.getInstance().saveFactionFromBuilding(this),
                    PluginInstance.getInstance().saveEntityAsync(this),
                    PluginInstance.getInstance().saveEntityAsync(region)
            ).exceptionally(e -> {
                regions.remove(region);
                throw new IllegalStateException("Query failed! ", e);
            });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public void removeRegion(int Id, Player player) {
        for (Region r : regions) {
            if (r.getId() == Id) {
                try {
                    regions.remove(r);
                    CompletableFuture.allOf(
                            PluginInstance.getInstance().saveFactionFromBuilding(this),
                            PluginInstance.getInstance().saveEntityAsync(this),
                            PluginInstance.getInstance().deleteEntityAsync(r)
                    ).thenRun(() -> {
                        player.sendMessage("This region was successfully removed from this building.");
                    }).exceptionally(e -> {
                        regions.add(r);
                        throw new IllegalStateException("Query failed! ", e);
                    });
                } catch (Exception e) {
                    PluginInstance.getInstance().consoleError(e);
                }
            }
        }
    }
    public void removeRegion(Region region, Player player) {
        if (regions.contains(region)) {
            try {
                regions.remove(region);
                CompletableFuture.allOf(
                        PluginInstance.getInstance().saveFactionFromBuilding(this),
                        PluginInstance.getInstance().saveEntityAsync(this),
                        PluginInstance.getInstance().deleteEntityAsync(region)
                ).thenRun(() -> {
                    player.sendMessage("This region was successfully removed from this building.");
                }).exceptionally(e -> {
                    regions.add(region);
                    throw new IllegalStateException("Query failed! ", e);
                });
            } catch (Exception e) {
                PluginInstance.getInstance().consoleError(e);
            }
        } else {
            player.sendMessage("This region does not exist in this building.");
        }
    }
    public void removeRegion(Region region) {
        try {
            if (regions.contains(region)) {
                this.regions.remove(region);
                CompletableFuture.allOf(
                        PluginInstance.getInstance().saveFactionFromBuilding(this),
                        PluginInstance.getInstance().saveEntityAsync(this),
                        PluginInstance.getInstance().deleteEntityAsync(region)
                ).exceptionally(e -> {
                    regions.add(region);
                    throw new IllegalStateException("Query failed! ", e);
                });
            } else {
                throw new IllegalArgumentException("This region does not exist in this building.");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public List<Region> getRegions() { return regions; }

    public boolean isInRegion(Location location) {
        for (Region region : regions) {
            if (region.contains(location)) {
                return true;
            }
        }
        return false;
    }

    // --- --- --- --- // Faction // --- --- --- --- //

    public Faction getFaction() {
        return PluginInstance.getInstance().getFactionForBuilding(this);
    }

    // --- --- --- --- // Territories // --- --- --- --- //

    public boolean isInFactionTerritory(Faction faction) {

        List<Territory> territories = faction.getTerritory();

        for (Region region : regions) {
            for (Territory territory : territories) {
                if (territory.contains(region.getPointA()) && territory.contains(region.getPointB())) {
                    return true;
                }
            }
        }
        return false;
    }

    // --- --- --- --- // Faction Restrictions // --- --- --- --- //

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public void addRestriction(Restriction restriction) {
        try {
            this.restrictions.add(restriction);
            CompletableFuture.allOf(
                    PluginInstance.getInstance().saveFactionFromBuilding(this),
                    PluginInstance.getInstance().saveEntityAsync(this),
                    PluginInstance.getInstance().saveEntityAsync(restriction)
            ).exceptionally(e -> {
                restrictions.remove(restriction);
                throw new IllegalStateException("Query failed! ", e);
            });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public void removeRestriction(Restriction restriction) {
        try {
            this.restrictions.remove(restriction);
            CompletableFuture.allOf(
                    PluginInstance.getInstance().saveFactionFromBuilding(this),
                    PluginInstance.getInstance().saveEntityAsync(this),
                    PluginInstance.getInstance().deleteEntityAsync(restriction)
            ).exceptionally(e -> {
                restrictions.add(restriction);
                throw new IllegalStateException("Query failed! ", e);
            });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public boolean hasRestriction(String name) {

        for (Restriction restriction : restrictions) {
            if (restriction.toString().toUpperCase().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRestricted(String name) {

        Faction faction = getFaction();

        for (Restriction restriction : faction.getRestrictions()) {
            if (restriction.toString().toUpperCase().equals(name)) {

                if (this.hasRestriction(name)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        if (this.hasRestriction(name)) {
            return true;
        } else {
            return false;
        }
    }

}
