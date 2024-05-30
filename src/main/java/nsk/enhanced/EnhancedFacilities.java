package nsk.enhanced;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Civilization.Faction;
import nsk.enhanced.Methods.Database;
import nsk.enhanced.Methods.MenuInstance;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Region;
import nsk.enhanced.Regions.system.RegionSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public final class EnhancedFacilities extends JavaPlugin implements Listener {

    private ArrayList<Faction> factions = new ArrayList<>();

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this,this);

        Component EF_L1 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d>  _____           _                                         _ ");
        Component EF_L2 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d> | ____|  _ __   | |__     __ _   _ __     ___    ___    __| |");
        Component EF_L3 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d> |  _|   | '_ \\  | '_ \\   / _` | | '_ \\   / __|  / _ \\  / _` |");
        Component EF_L4 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d> | |___  | | | | | | | | | (_| | | | | | | (__  |  __/ | (_| |");
        Component EF_L5 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d> |_____| |_| |_| |_| |_|  \\__,_| |_| |_|  \\___|  \\___|  \\__,_|");
        Component EF_L6 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d>  _____                  _   _   _     _                      ");
        Component EF_L7 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d> |  ___|   __ _    ___  (_) (_) | |_  (_)   ___   ___         ");
        Component EF_L8 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d> | |_     / _` |  / __| | | | | | __| | |  / _ \\ / __|        ");
        Component EF_L9 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d> |  _|   | (_| | | (__  | | | | | |_  | | |  __/ \\__ \\        ");
        Component EF_L10 = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d> |_|      \\__,_|  \\___| |_| |_|  \\__| |_|  \\___| |___/        ");

        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage(EF_L1);
        getServer().getConsoleSender().sendMessage(EF_L2);
        getServer().getConsoleSender().sendMessage(EF_L3);
        getServer().getConsoleSender().sendMessage(EF_L4);
        getServer().getConsoleSender().sendMessage(EF_L5);
        getServer().getConsoleSender().sendMessage(EF_L6);
        getServer().getConsoleSender().sendMessage(EF_L7);
        getServer().getConsoleSender().sendMessage(EF_L8);
        getServer().getConsoleSender().sendMessage(EF_L9);
        getServer().getConsoleSender().sendMessage(EF_L10);
        getServer().getConsoleSender().sendMessage(" ");
        getServer().getConsoleSender().sendMessage(" ");

        uploadFactions();

        PluginInstance.setInstance(this);
        this.getCommand("ef").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this,this);

        startAutoSaveTask();
    }

    private void uploadFactions() {
        String url = "mysql://localhost:3306/enhanced-facilities";
        String user = "root";
        String password = "root";

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM factions";
            ResultSet resultSet = statement.executeQuery(query);

            Gson gson = new GsonBuilder().create();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String playersJson = resultSet.getString("players");
                String buildingsJson = resultSet.getString("buildings");

                ArrayList<UUID> playerUUIDs = gson.fromJson(playersJson, new TypeToken<ArrayList<UUID>>(){}.getType());
                ArrayList<Building> buildings = gson.fromJson(buildingsJson, new TypeToken<ArrayList<Building>>(){}.getType());

                ArrayList<Player> players = new ArrayList<>();

                for (UUID playerUUID : playerUUIDs) {
                    Player player = Bukkit.getPlayer(playerUUID);
                    if (player != null) {
                        players.add(player);
                    }
                }

                Faction faction = new Faction(id, name,  players, buildings);
                factions.add(faction);
            }

            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(e.getMessage());

            if (e instanceof SQLException && e.getMessage().contains("Unknown database")) {
                new Database("mysql://localhost:3306/enhanced", "root", "root");
                uploadFactions();
            }
        }
    }

    private void saveFaction(Faction faction) {
        String url = "mysql://localhost:3306/enhanced-facilities";
        String user = "root";
        String password = "root";

        Gson gson = new GsonBuilder().create();

        String playersJson = gson.toJson(faction.getPlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
        String buildingsJson = gson.toJson(faction.getBuildings());

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            String query =  "INSERT INTO `factions` (id,name, players, buildings) VALUES (?,?,?,?)" +
                            "ON DUPLICATE KEY UPDATE name = VALUES(name), players = VALUES(players), buildings = VALUES(buildings)";
            PreparedStatement statement = connection.prepareStatement(query);

            statement.setInt(   1, faction.getId());
            statement.setString(2, faction.getName());
            statement.setString(3, playersJson);
            statement.setString(4, buildingsJson);

            statement.executeUpdate();
            statement.close();
            connection.close();

        } catch (Exception e) {
            getServer().getConsoleSender().sendMessage(e.getMessage());

            if (e instanceof SQLException && e.getMessage().contains("Unknown database")) {
                new Database("mysql://localhost:3306/enhanced", "root", "root");
                saveFaction(faction);
            }
        }
    }

    public void saveFactionAsync(Faction faction) {

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            String url = "mysql://localhost:3306/enhanced-facilities";
            String user = "root";
            String password = "root";

            Gson gson = new GsonBuilder().create();

            String playersJson = gson.toJson(faction.getPlayers().stream().map(Player::getUniqueId).collect(Collectors.toList()));
            String buildingsJson = gson.toJson(faction.getBuildings());

            try {
                Connection connection = DriverManager.getConnection(url, user, password);
                String query =  "INSERT INTO `factions` (id,name, players, buildings) VALUES (?,?,?,?)" +
                                "ON DUPLICATE KEY UPDATE name = VALUES(name), players = VALUES(players), buildings = VALUES(buildings)";
                PreparedStatement statement = connection.prepareStatement(query);

                statement.setInt(   1, faction.getId());
                statement.setString(2, faction.getName());
                statement.setString(3, playersJson);
                statement.setString(4, buildingsJson);

                statement.executeUpdate();
                statement.close();
                connection.close();

            } catch (Exception e) {
                getServer().getConsoleSender().sendMessage(e.getMessage());

                if (e instanceof SQLException && e.getMessage().contains("Unknown database")) {
                    new Database("mysql://localhost:3306/enhanced", "root", "root");
                    saveFactionAsync(faction);
                }
            }
        });

    }

    private void saveAllFactionsAsync(JavaPlugin plugin) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Faction faction : factions) {
                saveFaction(faction);
            }
            plugin.getLogger().info("All factions have been saved.");
        });

    }

    private void startAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAllFactionsAsync(EnhancedFacilities.this);
            }
        }.runTaskTimerAsynchronously(this,0L, 20L * 60 * 15);
    }

    public void consoleError(Exception e) {
        getServer().getConsoleSender().sendMessage(e.getMessage());
    }

    public void playerWarning(Exception e, Player p) {
        p.sendMessage(e.getMessage());
    }

    public void serverMessage(Component msg) {
        Component plugin = MiniMessage.miniMessage().deserialize("<gradient:#9953aa:#172d5d>[Enhanced] ");
        Component message = plugin.append(msg);

        getServer().sendMessage(message);
    }

    @Override
    public void onDisable() {
        this.saveAllFactionsAsync(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("ef")) {

            if (args.length == 0) {
                return false;
            }

            switch (args[0].toLowerCase()) {
                case "wand":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        giveRegionWand(player);
                        return true;
                    } else {
                        sender.sendMessage("This command can only be executed by a player.");
                        return true;
                    }

                case "building":
                    if (args.length > 3 &&  args[2].equalsIgnoreCase("add") &&
                        args[3].equalsIgnoreCase("region")) {

                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            int buildingId = Integer.parseInt(args[1]);
                            addRegionToBuilding(player, buildingId);
                            return true;

                        } else {
                            sender.sendMessage("This command can only be executed by a player.");
                            return true;
                        }
                    } else {
                        sender.sendMessage("Invalid arguments for building command");
                        return false;
                    }

                case "faction":
                    if (args.length > 2 &&  args[1].equalsIgnoreCase("create")) {

                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            int factionId = factions.size();
                            String factionName = args[2];

                            this.factions.add(new Faction(factionId, factionName, player));

                            player.sendMessage("Faction " + factionName + " was created with ID " + factionId);

                        } else {
                            sender.sendMessage("This command can only be executed by a player.");
                            return true;
                        }
                    } else {
                        sender.sendMessage("Invalid arguments for building command");
                        return false;
                    }

                default:
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        MenuInstance menuInstance = new MenuInstance();

                        menuInstance.openMenu(player);
                        return true;
                    } else if (sender instanceof ConsoleCommandSender) {
                        sender.sendMessage("Invalid arguments for building command");
                        return true;
                    }
                    return false;
            }
        }

        return false;
    }

    private void giveRegionWand(Player player) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();

        meta.displayName(Component.text("Region Wand"));
        wand.setItemMeta(meta);

        player.getInventory().addItem(wand);
        player.sendMessage("You have been given the Region Wand.");
    }

    private void addRegionToBuilding(Player player, int buildingId) {
        RegionSelector regionSelector = new RegionSelector();
        Location pointA = regionSelector.getPointA(player.getUniqueId());
        Location pointB = regionSelector.getPointB(player.getUniqueId());

        if (pointA != null && pointB != null) {
            player.sendMessage("Both points A and B must be set.");
            return;
        }

        Region region = new Region(pointA, pointB);
        Faction faction = getFactionForPlayer(player);

        if (faction == null) {
            player.sendMessage("You are not part of any faction.");
            return;
        }

        Building building = faction.getBuilding(buildingId);

        if (building == null) {
            player.sendMessage("Building with ID " + buildingId + " does not exist.");
            return;
        }

        building.addRegion(region);


        player.sendMessage("Region added to building " + buildingId);

    }

    private Faction getFactionForPlayer(Player player) {
        for (Faction faction : this.factions) {
            if (faction.isFactionPlayer(player)) {
                return faction;
            }
        }
        return null;
    }

}
