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

@Entity
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

        regions.add(region);
        PluginInstance.getInstance().saveFactionFromBuilding(this);
        player.sendMessage("This region was successfully added to this building.");

    }
    public void addRegion(Region region) {
        try {
            for (Region r : regions) {
                if (r.overlaps(region)) {
                    throw new IllegalArgumentException("This region overlaps already existing region in this building.");
                }
            }
            regions.add(region);
            PluginInstance.getInstance().saveFactionFromBuilding(this);
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public void removeRegion(int Id, Player player) {
        for (Region r : regions) {
            if (r.getId() == Id) {
                regions.remove(r);
                PluginInstance.getInstance().saveFactionFromBuilding(this);
            }
        }
    }
    public void removeRegion(Region region, Player player) {
        if (regions.contains(region)) {
            regions.remove(region);
            PluginInstance.getInstance().saveFactionFromBuilding(this);
        } else {
            player.sendMessage("This region does not exist in this building.");
        }
    }
    public void removeRegion(Region region) {
        try {
            if (regions.contains(region)) {
                this.regions.remove(region);
                PluginInstance.getInstance().saveFactionFromBuilding(this);
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
        this.restrictions.add(restriction);
    }

    public void removeRestriction(Restriction restriction) {
        this.restrictions.remove(restriction);
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
