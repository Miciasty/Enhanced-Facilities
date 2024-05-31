package nsk.enhanced;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Civilization.Faction;
import nsk.enhanced.Civilization.Invitation;
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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class EnhancedFacilities extends JavaPlugin implements Listener {

    private List<Faction> factions = new ArrayList<>();
    private List<Invitation> invitations = new ArrayList<>();

    private SessionFactory sessionFactory;

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this,this);
        sessionFactory = new Configuration().configure().buildSessionFactory();

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

        //  uploadFactions();
        loadFactionsFromDatabase();

        PluginInstance.setInstance(this);
        this.getCommand("ef").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this,this);

        startAutoSaveTask();
    }

    private void loadFactionsFromDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Faction> query = builder.createQuery(Faction.class);
            query.from(Faction.class);

            List<Faction> result = session.createQuery(query).getResultList();
            factions.addAll(result);
            session.getTransaction().commit();
        } catch (Exception e) {
            this.consoleError(e);
        }
    }

    /*private void saveFactionsToDatabase() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            for (Faction faction : factions) {
                session.saveOrUpdate(faction);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            this.consoleError(e);
        }
    }*/

    private void saveFaction(Faction faction) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.saveOrUpdate(faction);
            session.getTransaction().commit();
        } catch (Exception e) {
            this.consoleError(e);
        }
    }

    public void saveFactionAsync(Faction faction) {

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                session.saveOrUpdate(faction);
                session.getTransaction().commit();
            } catch (Exception e) {
                this.consoleError(e);
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

    public void saveFactionFromBuilding(Building building) {
        try {
            for (Faction faction : factions) {
                if (faction.getBuildings().contains(building)) {
                    saveFaction(faction);
                    return;
                }
            }
            throw new IllegalArgumentException("Building doesn't belong to any faction!");

        } catch (Exception e) {
            this.consoleError(e);
        }
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

    private Invitation getInvitationForPlayer(Player player) {
        for (Invitation invitation : this.invitations) {
            if (invitation.isInvited(player)) {
                return invitation;
            }
        }
        return null;
    }

    private void sentInvitationForPlayer(UUID uuid, Faction faction) {

        Player target = Bukkit.getPlayer(uuid);

        if (target != null ){

            this.invitations.add(new Invitation(target, faction));

            Component ENTRY = MiniMessage.miniMessage().deserialize("<green>You were invited to faction called</green> ");
            Component FACTION = MiniMessage.miniMessage().deserialize("<white>" + faction.getName() + "</white>. ");
            Component ACCEPT = MiniMessage.miniMessage().deserialize("<yellow><click:run_command:/ef accept>Accept</click></yellow> ");
            Component DECLINE  = MiniMessage.miniMessage().deserialize("<red><click:run_command:/ef decline>Decline</click></red>");

            target.sendMessage(ENTRY.append(FACTION).append(ACCEPT).append(DECLINE));

        }
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //
    /*
                   dP""b8      dP"Yb      8b    d8     8b    d8        db        88b 88     8888b.
                  dP   `"     dP   Yb     88b  d88     88b  d88       dPYb       88Yb88      8I  Yb
                  Yb          Yb   dP     88YbdP88     88YbdP88      dP__Yb      88 Y88      8I  dY
                   YboodP      YbodP      88 YY 88     88 YY 88     dP""""Yb     88  Y8     8888Y"
    */
    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

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

                    if (args.length == 2) {
                        return false;
                    }

                    switch (args[1].toLowerCase()) {
                        case "create":
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

                        case "invite":
                            if (sender instanceof Player && args.length == 3) {
                                Player player = (Player) sender;
                                Faction faction = getFactionForPlayer(player);

                                if (faction == null) {
                                    player.sendMessage("You are not part of any faction.");
                                } else {
                                    Player invited = Bukkit.getPlayer(args[2]);

                                    if (invited == null) {
                                        player.sendMessage("This player is not on the server.");
                                    } else {
                                        sentInvitationForPlayer(invited.getUniqueId(), faction);
                                        player.sendMessage("Player " + invited.getName() + " has been invited to faction " + faction.getName());
                                    }
                                }

                            } else {
                                sender.sendMessage("This command can only be executed by a player.");
                                return true;
                            }
                    }

                case "accept":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Faction faction = getFactionForPlayer(player);

                        Invitation invitation = getInvitationForPlayer(player);

                        if (invitation == null) {
                            player.sendMessage("You didn't receive any invitations.");
                            return false;
                        }

                        if (faction != null && invitation.status()){
                            Component MESSAGE = MiniMessage.miniMessage().deserialize("<red>Do you want to leave faction called</red> ");
                            Component FACTION = MiniMessage.miniMessage().deserialize("<yellow>" + faction.getName() + "</yellow> ");
                            Component STAY = MiniMessage.miniMessage().deserialize("<yellow><click:run_command:/ef decline>Stay</click></yellow> ");
                            Component LEAVE = MiniMessage.miniMessage().deserialize("<red><click:run_command:/ef autojoin>Leave</click></red>");

                            player.sendMessage("You are already part of faction called " + faction.getName());
                            player.sendMessage(MESSAGE.append(FACTION).append(STAY).append(LEAVE));
                        }
                    } else {
                        sender.sendMessage("This command can only be executed by a player.");
                        return true;
                    }

                case "decline":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Invitation invitation = getInvitationForPlayer(player);

                        if (invitation == null) {
                            player.sendMessage("You didn't receive any invitations.");
                        } else {
                            player.sendMessage("You decline your invitation to join " + invitation.getFaction().getName());
                            invitations.remove(invitation);
                        }

                    } else {
                        sender.sendMessage("This command can only be executed by a player.");
                        return true;
                    }

                case "leave":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Faction faction = getFactionForPlayer(player);

                        if (faction == null) {
                            player.sendMessage("You are not part of any faction.");
                            return true;
                        } else {
                            faction.removePlayer(player);
                        }
                    } else {
                        sender.sendMessage("This command can only be executed by a player.");
                    }

                case "autojoin":
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        Faction actualFaction = getFactionForPlayer(player);

                        if (actualFaction == null) {
                            player.sendMessage("You are not part of any faction.");
                            return true;
                        } else {
                            actualFaction.removePlayer(player);
                        }

                        Invitation invitation = getInvitationForPlayer(player);

                        if (invitation == null) {
                            player.sendMessage("You didn't receive any invitations.");
                            return true;
                        } else {
                            invitation.getFaction().addPlayer(player);
                            invitations.remove(invitation);
                        }
                    } else {
                        sender.sendMessage("This command can only be executed by a player.");
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

}
