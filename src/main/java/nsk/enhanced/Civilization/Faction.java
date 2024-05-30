package nsk.enhanced.Civilization;

import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Region;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.UUID;


public class Faction implements Listener {

    private String name;
    private int id;

    private final ArrayList<Player> players;

    private final ArrayList<Building> buildings;

    public Faction(int id, String name, ArrayList<Player> players, ArrayList<Building> buildings) {
        this.players = new ArrayList<>();
        this.buildings = new ArrayList<>();

        this.players.addAll(players);
        this.buildings.addAll(buildings);

        this.name = name;
        this.id = id;
    }

    public Faction(int id, String name, Player player) {
        this.players = new ArrayList<>();
        this.players.add(player);

        this.buildings = new ArrayList<>();

        this.name = name;
        this.id = id;
    }

    // --- --- --- --- // Setter's / Getter's // --- --- --- --- //

    private void setName(String name) {
        this.name = name;
        PluginInstance.getInstance().saveFactionAsync(this);
    }
    public String getName() {
        return name;
    }

    private void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    // --- --- --- --- // Faction Buildings // --- --- --- --- //

    public ArrayList<Building> getBuildings() { return this.buildings; }

    public void addBuildings(ArrayList<Building> buildings) {
        this.buildings.addAll(buildings);
        PluginInstance.getInstance().saveFactionAsync(this);
    }

    private void addBuilding(String type, String level, int durability, ArrayList<Region> regions) {
        int id = buildings.size();

        this.buildings.add( new Building(type, id, level, durability, regions) );
        PluginInstance.getInstance().saveFactionAsync(this);
    }

    public Building getBuilding(int id) {
        for (Building building : this.buildings) {
            if (building.getId() == id) {
                return building;
            }
        }
        return null;
    }

    public ArrayList<Building> getBuildingsOfType(String type) {
        ArrayList<Building> inType = new ArrayList<>();
        for (Building building : this.buildings) {
            if (building.getType().equals(type)) {
                inType.add(building);
            }
        }

        return inType;
    }

    // --- --- --- --- // Faction Players // --- --- --- --- //

    public ArrayList<Player> getPlayers() { return this.players; }

    public void addPlayers(ArrayList<Player> players) {
        this.players.addAll(players);
        PluginInstance.getInstance().saveFactionAsync(this);
    }

    private void addPlayer(Player player) {
        this.players.add(player);
        PluginInstance.getInstance().saveFactionAsync(this);
    }
    public Player getPlayer(String name) {
        for (Player p : this.players) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }
    public Player getPlayer(UUID uuid) {
        for (Player p : this.players) {
            if (p.getUniqueId().equals(uuid)) {
                return p;
            }
        }
        return null;
    }

    public boolean isFactionPlayer(Player player) {
        for (Player p : this.players) {
            if (p.equals(player)) {
                return true;
            }
        }
        return false;
    }

}
