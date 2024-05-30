package nsk.enhanced.Buildings;

import nsk.enhanced.Regions.Region;
import org.bukkit.Location;

import java.util.ArrayList;

public class Building {

    protected String type;

    protected int id;
    protected String level;

    protected int durability;

    protected ArrayList<Region> regions;

    // --- --- --- --- // CONSTRUCTOR // --- --- --- --- //

    public Building(String type, int id, String level, int durability, ArrayList<Region> regions) {
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

    private void addRegion(Region region) {
        this.regions.add(region);
    }
    public ArrayList<Region> getRegions() { return regions; }

    public boolean isInRegion(Location location) {
        for (Region region : regions) {
            if (region.contains(location)) {
                return true;
            }
        }
        return false;
    }

}
