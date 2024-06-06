package nsk.enhanced.Civilization;

import net.kyori.adventure.text.Component;
import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Civilization.status.atWar;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Region;
import nsk.enhanced.Regions.Restriction;
import nsk.enhanced.Regions.Territory;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;

import javax.persistence.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Entity
@Table(name = "factions")
public class Faction implements Listener {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "faction_players", joinColumns = @JoinColumn(name = "faction_id"))
    @Column(name = "player_uuid")
    private List<String> playersUUID;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "faction_id")
    private List<Building> buildings;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "faction_id")
    protected List<Territory> territories;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "faction_id")
    private List<Restriction> restrictions;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "attacker_id")
    private List<atWar> warsAsAttacker;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "defender_id")
    private List<atWar> warsAsDefender;


    public Faction(String name, List<Player> players, List<Building> buildings) {
        this.playersUUID = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.territories = new ArrayList<>();
        this.restrictions = new ArrayList<>();
        this.warsAsAttacker = new ArrayList<>();
        this.warsAsDefender = new ArrayList<>();

        for (Player p : players) {
            this.playersUUID.add( p.getUniqueId().toString() );
        }

        this.buildings.addAll(buildings);

        this.name = name;
    }

    public Faction(String name, Player player) {
        this.playersUUID = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.territories = new ArrayList<>();
        this.restrictions = new ArrayList<>();
        this.warsAsAttacker = new ArrayList<>();
        this.warsAsDefender = new ArrayList<>();

        this.playersUUID.add( player.getUniqueId().toString() );

        this.name = name;
    }

    public Faction(String name) {
        this.playersUUID = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.territories = new ArrayList<>();
        this.restrictions = new ArrayList<>();
        this.warsAsAttacker = new ArrayList<>();
        this.warsAsDefender = new ArrayList<>();

        this.name = name;
    }

    public Faction() { /* Pusty konstruktor wymagany przez JPA */ }

    // --- --- --- --- // Setter's / Getter's // --- --- --- --- //

    private void setName(String name) {
        String p = this.name;
        this.name = name;
        try {
            PluginInstance.getInstance().saveEntityAsync(this)
                .exceptionally(e -> {
                    this.name = p;
                    throw new IllegalStateException("Query failed! ", e);
                });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    // --- --- --- --- // Faction Buildings // --- --- --- --- //

    public List<Building> getBuildings() { return this.buildings; }

    public void addBuildings(ArrayList<Building> buildings) {
        try {
            this.buildings.addAll(buildings);
            PluginInstance.getInstance().saveEntityAsync(this)
                .exceptionally(e -> {
                    this.buildings.removeAll(buildings);
                    throw new IllegalStateException("Query failed! ", e);
                });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public void addBuilding(Building building) {
        try {
            this.buildings.add(building);
            PluginInstance.getInstance().saveEntityAsync(this)
                .exceptionally(e -> {
                    this.buildings.remove(building);
                    throw new IllegalStateException("Query failed! ", e);
                });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addBuilding(int level, int durability, List<Region> regions, Player player) {
        Building newBuilding = new Building(level, durability, regions);

        try {
            this.buildings.add( newBuilding );
            PluginInstance.getInstance().saveEntityAsync(this)
                .thenRun(() -> {
                    player.sendMessage("Building was successfully added to the faction");
                }).exceptionally(e -> {
                    this.buildings.remove(newBuilding);
                    throw new IllegalStateException("Query failed! ", e);
                });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public void addBuilding(int level, int durability, List<Region> regions) {
        Building newBuilding = new Building(level, durability, regions);

        try {
            this.buildings.add( newBuilding );

            PluginInstance.getInstance().saveEntityAsync(this)
                .exceptionally(e -> {
                    this.buildings.remove(newBuilding);
                    throw new IllegalStateException("Query failed! ", e);
                });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public void removeBuilding(Building building, Player player) {
        try {
            if (this.buildings.contains(building)) {
                this.buildings.remove(building);
                PluginInstance.getInstance().saveEntityAsync(this)
                    .thenRun(() -> {
                        player.sendMessage("Building was successfully removed");
                    }).exceptionally(e -> {
                        this.buildings.add(building);
                        throw new IllegalStateException("Query failed! ", e);
                    });
            } else {
                player.sendMessage("Your faction doesn't possess this building");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }
    public void removeBuilding(Building building) {
        try {
            if (this.buildings.contains(building)) {
                this.buildings.remove(building);
                PluginInstance.getInstance().saveEntityAsync(this)
                    .exceptionally(e -> {
                        this.buildings.add(building);
                        throw new IllegalStateException("Query failed! ", e);
                    });
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
                PluginInstance.getInstance().saveEntityAsync(this)
                    .exceptionally(e -> {
                        this.playersUUID.remove(uuid.toString());
                        throw new IllegalStateException("Query failed! ", e);
                    });
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
                PluginInstance.getInstance().saveEntityAsync(this)
                    .exceptionally(e -> {
                        this.playersUUID.remove(target.getUniqueId().toString());
                        throw new IllegalStateException("Query failed! ", e);
                    });
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

                PluginInstance.getInstance().saveEntityAsync(this)
                    .exceptionally(e -> {
                        this.playersUUID.remove(player.getUniqueId().toString());
                        throw new IllegalStateException("Query failed! ", e);
                    });
            }

            if (state == 0) {
                throw new IllegalArgumentException("No players were added to the faction");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }

    }

    public void addPlayer(Player player) {
        try {
            this.playersUUID.add(player.getUniqueId().toString());
            PluginInstance.getInstance().saveEntityAsync(this)
                .exceptionally(e -> {
                    this.playersUUID.remove(player.getUniqueId().toString());
                    throw new IllegalStateException("Query failed! ", e);
                });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public void removePlayer(String name, Player p) {
        try {
            Player target = Bukkit.getPlayer(name);

            if (playersUUID.contains(target.getUniqueId().toString())) {
                playersUUID.remove(target.getUniqueId().toString());
                PluginInstance.getInstance().saveEntityAsync(this)
                    .exceptionally(e -> {
                        playersUUID.add(target.getUniqueId().toString());
                        throw new IllegalStateException("Query failed! ", e);
                    });
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
                PluginInstance.getInstance().saveEntityAsync(this)
                    .exceptionally(e -> {
                        playersUUID.add(uuid.toString());
                        throw new IllegalStateException("Query failed! ", e);
                    });
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
                    PluginInstance.getInstance().saveEntityAsync(this)
                        .exceptionally(e -> {
                            playersUUID.add(player.getUniqueId().toString());
                            throw new IllegalStateException("Query failed! ", e);
                        });
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
                PluginInstance.getInstance().saveEntityAsync(this)
                    .exceptionally(e -> {
                        playersUUID.add(player.getUniqueId().toString());
                        throw new IllegalStateException("Query failed! ", e);
                    });
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

    public void forEachPlayer(Component message) {
        this.playersUUID.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        });
    }
    public void forEachPlayer(Faction faction) {
        this.playersUUID.forEach(uuid -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage("Your faction " + this.getName() + " is now at war with " + faction.getName());
            }
        });
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

        try {
            this.territories.add(newTerritory);
            PluginInstance.getInstance().saveEntityAsync(this)
                .thenRun(() -> {
                    player.sendMessage("Chunk claimed successfully.");
                }).exceptionally(e -> {
                    this.territories.remove(newTerritory);
                    player.sendMessage("Query failed! Chunk could not be claimed.");
                    throw new IllegalStateException("Query failed! ", e);
                });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }

    }

    public void removeChunkFromTerritory(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        Location pointA = chunk.getBlock(0, 0, 0).getLocation();
        Location pointB = chunk.getBlock(15, 255, 15).getLocation();

        for (Territory territory : this.territories) {
            if (territory.getPointA().equals(pointA) && territory.getPointB().equals(pointB)) {
                try {
                    this.territories.remove(territory);
                    PluginInstance.getInstance().saveEntityAsync(this)
                        .thenRun(() -> {
                            player.sendMessage("Chunk removed from territory successfully.");
                        }).exceptionally(e -> {
                            this.territories.add(territory);
                            player.sendMessage("Query failed! Chunk could not be removed.");
                            throw new IllegalStateException("Query failed! ", e);
                        });
                } catch (Exception e) {
                    PluginInstance.getInstance().consoleError(e);
                }
            } else {
                player.sendMessage("This chunk doesn't belong to this faction.");
            }
        }
    }

    public void removeTerritoryById(int id, Player player) {
        for (Territory territory : this.territories) {
            if (territory.getId() == id) {
                try {
                    this.territories.remove(territory);
                    PluginInstance.getInstance().saveEntityAsync(this)
                            .thenRun(() -> {
                                player.sendMessage("Territory with id " + id + " removed successfully.");
                            }).exceptionally(e -> {
                                this.territories.add(territory);
                                player.sendMessage("Query failed! Territory with id " + id + " could not be removed.");
                                throw new IllegalStateException("Query failed! ", e);
                            });
                } catch (Exception e) {
                    PluginInstance.getInstance().consoleError(e);
                }

            } else {
                player.sendMessage("Territory with id " + id + " doesn't exists.");
            }
        }
    }

    public boolean containLocation(Location location) {
        for (Territory territory : this.territories) {
            if (territory.contains(location)) {
                return true;
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
            if (!this.restrictions.contains(restriction)) {
                this.restrictions.add(restriction);
                PluginInstance.getInstance().saveEntityAsync(this)
                    .exceptionally(e -> {
                        this.restrictions.remove(restriction);
                        throw new IllegalStateException("Query failed! ", e);
                    });
            } else {
                throw new IllegalArgumentException("This restriction already exists.");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public void removeRestriction(Restriction restriction) {
        try {
            if (this.restrictions.contains(restriction)) {
                this.restrictions.remove(restriction);
                PluginInstance.getInstance().saveEntityAsync(this)
                    .exceptionally(e -> {
                        this.restrictions.add(restriction);
                        throw new IllegalStateException("Query failed! ", e);
                    });
            } else {
                throw new IllegalArgumentException("Restriction with Id " + restriction.getId() + " doesn't exist.");
            }
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public boolean hasRestriction(String name) {

        for (Restriction restriction : restrictions) {
            if (restriction.toString().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isRestricted(String name) {
        if (this.hasRestriction(name)) {
            return true;
        } else {
            return false;
        }
    }

    // --- --- --- --- // Wars // --- --- --- --- //

    public void declareWar(Faction enemy, Player player) {

        if (getAllEnemies().contains(enemy)) {
            return;
        }

        atWar war = new atWar(this, enemy);

        try {
            this.warsAsAttacker.add(war);
            enemy.warsAsDefender.add(war);

            CompletableFuture.allOf(
                    PluginInstance.getInstance().saveEntityAsync(this),
                    PluginInstance.getInstance().saveEntityAsync(enemy)
            ).thenRun(() -> {
                this.forEachPlayer(enemy);
                enemy.forEachPlayer(this);
            }).exceptionally(e -> {
                this.warsAsAttacker.remove(war);
                enemy.warsAsDefender.remove(war);
                throw new IllegalStateException("Query failed! ", e);
            });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }
    }

    public void declarePeace(Faction enemy, Player player) {

        for (atWar war : getWarsAsAttacker()) {
            if (war.getDefender().equals(enemy)) {
                try {
                    this.warsAsAttacker.remove(war);
                    enemy.warsAsDefender.remove(war);

                    CompletableFuture.allOf(
                            PluginInstance.getInstance().saveEntityAsync(this),
                            PluginInstance.getInstance().saveEntityAsync(enemy)
                    ).exceptionally(e -> {
                        this.warsAsAttacker.add(war);
                        enemy.warsAsDefender.add(war);
                        throw new IllegalStateException("Query failed! ", e);
                    });
                } catch (Exception e) {
                    PluginInstance.getInstance().consoleError(e);
                }
            }
        }
    }


    public List<atWar> getWarsAsAttacker() {
        return this.warsAsAttacker;
    }

    public List<atWar> getWarsAsDefender() {
        return this.warsAsDefender;
    }

    public List<Faction> getAllEnemies() {
        List<Faction> wars = new ArrayList<>();
        for (atWar attacker : warsAsAttacker) {
            if (!wars.contains(attacker)) {
                wars.add(attacker.getDefender());
            }
        }
        for (atWar defender : warsAsDefender) {
            if (!wars.contains(defender)) {
                wars.add(defender.getAttacker());
            }
        }
        return wars;
    }

    public boolean isAtWarWith(Faction enemy) {
        for (Faction faction : getAllEnemies()) {
            if (faction.equals(enemy)) {
                return true;
            }
        }
        return false;
    }
}
