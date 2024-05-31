package nsk.enhanced.Civilization;

import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Region;
import nsk.enhanced.Regions.Restriction;
import nsk.enhanced.Regions.Territory;
import nsk.enhanced.Regions.restrictions.WarRestriction;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "faction_id")
    protected List<Territory> territories;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "faction_id")
    private List<Restriction> restrictions;

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

    public void addBuilding(String type, String level, int durability, List<Region> regions, Player player) {
        int id = buildings.size();

        this.buildings.add( new Building(type, id, level, durability, regions) );
        PluginInstance.getInstance().saveFactionAsync(this);
        player.sendMessage("Building " + type + " was successfully added to the faction");
    }

    public void addBuilding(String type, String level, int durability, List<Region> regions) {
        int id = buildings.size();

        this.buildings.add( new Building(type, id, level, durability, regions) );
        PluginInstance.getInstance().saveFactionAsync(this);
    }

    public void removeBuilding(Building building, Player player) {
        if (this.buildings.contains(building)) {
            this.buildings.remove(building);
            PluginInstance.getInstance().saveFactionAsync(this);
            player.sendMessage("Building was successfully removed");
        } else {
            player.sendMessage("Your faction doesn't possess this building");
        }
    }
    public void removeBuilding(Building building) {
        try {
            if (this.buildings.contains(building)) {
                this.buildings.remove(building);
                PluginInstance.getInstance().saveFactionAsync(this);
            } else {
                throw new IllegalArgumentException("This building does not exist");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
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

    public void addPlayer(UUID uuid, Player p) {
        try {
            Player target = Bukkit.getPlayer(uuid);

            if (target.equals(uuid) && this.playersUUID.contains(uuid.toString())) {
                playersUUID.add(uuid.toString());
            } else {
                throw new IllegalArgumentException("Player with uuid " + uuid + " does not exist");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().playerWarning(e,p);
        }
    }

    public void addPlayer(String name, Player p) {
        try {
            Player target = Bukkit.getPlayer(name);

            if(target.equals(name) && this.playersUUID.contains(target.getUniqueId().toString())) {
                playersUUID.add(target.getUniqueId().toString());
            } else {
                throw new IllegalArgumentException("Player with that name doesn't exists.");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().playerWarning(e,p);
        }
    }

    private void addPlayers(List<Player> players) {

        try {
            int state = 0;
            for (Player player : players) {
                this.playersUUID.add( player.getUniqueId().toString() );
                state++;
            }

            if (state == 0) {
                throw new IllegalArgumentException("No players were added to the faction");
            } else {
                PluginInstance.getInstance().saveFactionAsync(this);
            }
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }

    }

    public void addPlayer(Player player) {
        this.playersUUID.add(player.getUniqueId().toString());
        PluginInstance.getInstance().saveFactionAsync(this);
    }

    public void removePlayer(String name, Player p) {
        try {
            Player target = Bukkit.getPlayer(name);

            if (playersUUID.contains(target.getUniqueId().toString())) {
                playersUUID.remove(target.getUniqueId().toString());
            } else {
                throw new IllegalArgumentException("Player with name " + name + " doesn't belong to the faction");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().playerWarning(e, p);
        }
    }

    public void removePlayer(UUID uuid, Player p) {
        try {
            Player target = Bukkit.getPlayer(uuid);

            if(target.getUniqueId().equals(p.getUniqueId())) {
                playersUUID.remove(uuid.toString());
                PluginInstance.getInstance().saveFactionAsync(this);
            } else {
                throw new IllegalArgumentException("Player with that uuid doesn't belong to the faction.");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().playerWarning(e, p);
        }
    }

    private void removePlayers(List<Player> players) {
        for (Player player : players) {
            try {
                if (playersUUID.contains(player.getUniqueId().toString())) {
                    playersUUID.remove(player.getUniqueId().toString());
                    PluginInstance.getInstance().saveFactionAsync(this);
                } else {
                    throw new IllegalArgumentException("Player with uuid " + player.getUniqueId() + " doesn't belong to the faction");
                }
            } catch (Exception e) {
                PluginInstance.getInstance().consoleError(e);
            }
        }
    }

    public void removePlayer(Player player) {
        try {
            if (playersUUID.contains(player.getUniqueId().toString())) {
                playersUUID.remove(player.getUniqueId().toString());
                PluginInstance.getInstance().saveFactionAsync(this);
            } else {
                throw new IllegalArgumentException("Player with that uuid " + player.getUniqueId() + " doesn't belong to the faction");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public boolean isFactionPlayer(Player player) {

        return this.playersUUID.contains(player.getUniqueId().toString());

    }

    // --- --- --- --- // Faction Territory // --- --- --- --- //

    public List<Territory> getTerritory() {
        return this.territories;
    }

    public void addChunkAsTerritory(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        Location pointA = chunk.getBlock(0, 0, 0).getLocation();
        Location pointB = chunk.getBlock(15, 255, 15).getLocation();

        Territory newTerritory = new Territory(pointA, pointB);

        for (Territory territory : this.territories) {
            if (territory.overlaps(newTerritory)) {
                player.sendMessage("This chunk is already claimed by another territory.");
                return;
            }
        }

        this.territories.add(newTerritory);
        PluginInstance.getInstance().saveFactionAsync(this);
        player.sendMessage("Chunk claimed successfully.");
    }

    /*public void addTerritory(Location pointA, Location pointB, Player player) {
        Territory newTerritory = new Territory(pointA, pointB);

        for (Territory territory : this.territories) {
            if (territory.overlaps(newTerritory)) {
                player.sendMessage("This territory is already claimed by another territory.");
                return;
            }
        }

        this.territories.add(newTerritory);
        PluginInstance.getInstance().saveFactionAsync(this);
        player.sendMessage("Territory claimed successfully.");
    }*/

    public void removeChunkFromTerritory(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        Location pointA = chunk.getBlock(0, 0, 0).getLocation();
        Location pointB = chunk.getBlock(15, 255, 15).getLocation();

        for (Territory territory : this.territories) {
            if (territory.getPointA().equals(pointA) && territory.getPointB().equals(pointB)) {
                this.territories.remove(territory);
                PluginInstance.getInstance().saveFactionAsync(this);
                player.sendMessage("Chunk removed from territory successfully.");
            } else {
                player.sendMessage("This chunk doesn't belong to this faction.");
            }
        }
    }

    public void removeTerritoryById(int id, Player player) {
        for (Territory territory : this.territories) {
            if (territory.getId() == id) {
                this.territories.remove(territory);
                PluginInstance.getInstance().saveFactionAsync(this);
                player.sendMessage("Territory with Id " + id + " removed successfully.");
            } else {
                player.sendMessage("Territory with Id doesn't exists.");
            }
        }
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
            if (restriction.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRestricted(String name) {
        for (Restriction restriction : restrictions) {
            if (restriction.getName().equals(name)) {
                return restriction.isActive();
            }
        }
        return false;
    }

    // --- --- --- --- // Methods // --- --- --- --- //

    public void declareWar(Faction enemy) {

        WarRestriction war = new WarRestriction(Restriction.RestrictionType.WAR, true, enemy.getId());
        this.restrictions.add(war);

        WarRestriction war2 = new WarRestriction(Restriction.RestrictionType.WAR, true, this.getId());
        enemy.restrictions.add(war2);

        for (String uuid : this.playersUUID) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage("Your faction has declared a war to " + enemy.getName());
            }
        }

        for (String uuid : enemy.playersUUID) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(this.getName() + " has declared a war to your faction.");
            }
        }
    }

    public boolean isAtWarWith(Faction enemy) {
        for (Restriction restriction : restrictions) {
            if (restriction instanceof WarRestriction) {
                WarRestriction war = (WarRestriction) restriction;

                if (war.getEnemyFactionID() == enemy.getId() && war.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }
}
