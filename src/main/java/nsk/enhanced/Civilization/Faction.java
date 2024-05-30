package nsk.enhanced.Civilization;

import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Region;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "factions")
public class Faction implements Listener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "faction_players", joinColumns = @JoinColumn(name = "faction_id"))
    @Column(name = "player_uuid")
    private List<String> playersUUID;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "faction_id")
    private List<Building> buildings;

    public Faction(int id, String name, List<Player> players, List<Building> buildings) {
        this.playersUUID = new ArrayList<>();
        this.buildings = new ArrayList<>();

        for (Player p : players) {
            this.playersUUID.add( p.getUniqueId().toString() );
        }

        this.buildings.addAll(buildings);

        this.name = name;
        this.id = id;
    }

    public Faction(int id, String name, Player player) {
        this.playersUUID = new ArrayList<>();
        this.buildings = new ArrayList<>();

        this.playersUUID.add( player.getUniqueId().toString() );

        this.name = name;
        this.id = id;
    }

    public Faction() { /* Pusty konstruktor wymagany przez JPA */ }

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

    public List<Building> getBuildings() { return this.buildings; }

    public void addBuildings(ArrayList<Building> buildings) {
        this.buildings.addAll(buildings);
        PluginInstance.getInstance().saveFactionAsync(this);
    }

    private void addBuilding(String type, String level, int durability, List<Region> regions) {
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

    public List<Building> getBuildingsOfType(String type) {

        List<Building> inType = new ArrayList<>();

        for (Building building : this.buildings) {
            if (building.getType().equals(type)) {
                inType.add(building);
            }
        }

        return inType;
    }

    // --- --- --- --- // Faction Players // --- --- --- --- //

    public List<String> getPlayersUUID() { return this.playersUUID; }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();

        for (String uuid : this.playersUUID) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                players.add(player);
            }
        }

        return players;
    }

    public void addPlayers(List<Player> players) {

        for (Player player : players) {
            this.playersUUID.add( player.getUniqueId().toString() );
        }

        PluginInstance.getInstance().saveFactionAsync(this);
    }

    private void addPlayer(Player player) {
        this.playersUUID.add(player.getUniqueId().toString());
        PluginInstance.getInstance().saveFactionAsync(this);
    }

    public Player getPlayer(String name) {
        for (String uuid : this.playersUUID) {
            if (Bukkit.getPlayer(uuid).equals(name)) {
                return Bukkit.getPlayer(uuid);
            }
        }
        return null;
    }
    public Player getPlayer(UUID uuid) {
        for (String u : this.playersUUID) {
            if (Bukkit.getPlayer(u).equals(uuid)) {
                return Bukkit.getPlayer(u);
            }
        }
        return null;
    }

    public boolean isFactionPlayer(Player player) {

        return this.playersUUID.contains(player.getUniqueId().toString());

    }

}
