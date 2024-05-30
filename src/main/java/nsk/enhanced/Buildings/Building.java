package nsk.enhanced.Buildings;

import nsk.enhanced.Regions.Region;
import org.bukkit.Location;

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

    public void addRegion(Region region) {
        this.regions.add(region);
    }
    public void removeRegion(Region region) {
        this.regions.remove(region);
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

}
