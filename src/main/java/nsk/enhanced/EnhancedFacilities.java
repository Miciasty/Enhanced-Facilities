package nsk.enhanced;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import nsk.enhanced.Buildings.Basic.Sawmill;
import nsk.enhanced.Buildings.Building;
import nsk.enhanced.Civilization.Faction;
import nsk.enhanced.Civilization.Invitation;
import nsk.enhanced.Methods.Managers.Buildings.SawmillManager;
import nsk.enhanced.Methods.Managers.Events.OnPlayerInteractEvent;
import nsk.enhanced.Methods.Managers.Regions.RegionWandManager;
import nsk.enhanced.Methods.MenuInstance;
import nsk.enhanced.Methods.PluginInstance;
import nsk.enhanced.Regions.Region;
import nsk.enhanced.Regions.Restriction;
import nsk.enhanced.Regions.Territory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
import java.util.concurrent.CompletableFuture;

public final class EnhancedFacilities extends JavaPlugin implements Listener {

    private List<Faction> factions = new ArrayList<>();
    private List<Building> buildings = new ArrayList<>();

    private List<Region> regionSelections = new ArrayList<>();

    private List<Invitation> invitations = new ArrayList<>();

    private SessionFactory sessionFactory;

    private RegionWandManager regionWandManager = new RegionWandManager();

    public RegionWandManager getRegionWandManager() {
        return regionWandManager;
    }

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(this,this);
        sessionFactory = new Configuration().configure().buildSessionFactory();

        // --- --- --- --- // Events Managers & Listeners // --- --- --- --- //
        // Events Listeners
        OnPlayerInteractEvent onPlayerInteractEvent = new OnPlayerInteractEvent();
            getServer().getPluginManager().registerEvents(onPlayerInteractEvent,this);

        // --- --- --- --- // Managers // --- --- --- --- //
            getServer().getPluginManager().registerEvents(regionWandManager,this);

        SawmillManager sawmillManager = new SawmillManager();
            getServer().getPluginManager().registerEvents(sawmillManager,this);



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

        // Start buildings schedulers;
        for (Faction faction : factions) {

            // All sawmills
            int i = 0; for (Building building : faction.getBuildingsOfType("sawmill")) {
                sawmillManager.addSawmill( (Sawmill) building );
                i++;
            }
            this.consoleNotification(faction.getName() + ": Loaded " + i + " sawmills of all " + faction.getBuildingsOfType("sawmill"));
        }
    }

    @Override
    public void onDisable() {
        this.saveAllFactionsAsync(this);
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

    private void startAutoSaveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAllFactionsAsync(EnhancedFacilities.this);
            }
        }.runTaskTimerAsynchronously(this,0L, 20L * 60 * 15);
    }

    public void consoleNotification(String m) {
        getServer().getConsoleSender().sendMessage(m);
    }

    public void consoleMessage(Exception e) {
        getServer().getConsoleSender().sendMessage(e.getMessage());
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

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //
    /*
                            888888     88b 88     888888     88     888888     Yb  dP
                            88__       88Yb88       88       88       88        YbdP
                            88""       88 Y88       88       88       88         8P
                            888888     88  Y8       88       88       88         dP
    */
    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private <T> void saveEntity(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.saveOrUpdate(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            this.consoleError(e);
        }
    }
    public <T> CompletableFuture<Void> saveEntityAsync(T entity) {

        return CompletableFuture.runAsync(() -> {
                try (Session session = sessionFactory.openSession()) {
                    session.beginTransaction();

                    session.saveOrUpdate(entity);
                    session.getTransaction().commit();
                } catch (Exception e) {
                    this.consoleError(e);
                }

            });
    }
    public <T> CompletableFuture<Void> saveAllEntitiesFromListAsync(List<T> entities) {

        return CompletableFuture.runAsync(() -> {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                for (T entity : entities) {
                    session.saveOrUpdate(entity);
                }

                session.getTransaction().commit();
            } catch (Exception e) {
                this.consoleError(e);
            }
        });
    }

    private <T> void deleteEntity(T entity) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            session.delete(entity);
            session.getTransaction().commit();
        } catch (Exception e) {
            this.consoleError(e);
        }
    }
    public <T> CompletableFuture<Void> deleteEntityAsync(T entity) {

        return CompletableFuture.runAsync(() -> {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                session.saveOrUpdate(entity);
                session.getTransaction().commit();
            } catch (Exception e) {
                this.consoleError(e);
            }
        });
    }
    public <T> CompletableFuture<Void> deleteAllEntitiesFromListAsync(List<T> entities) {

        return CompletableFuture.runAsync(() -> {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                for (T entity : entities) {
                    session.delete(entity);
                }

                session.getTransaction().commit();
            } catch (Exception e) {
                this.consoleError(e);
            }
        });
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //
    /*
                     888888        db         dP""b8     888888     88      dP"Yb      88b 88
                     88__         dPYb       dP   `"       88       88     dP   Yb     88Yb88
                     88""        dP__Yb      Yb            88       88     Yb   dP     88 Y88
                     88         dP""""Yb      YboodP       88       88      YbodP      88  Y8
    */
    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private void saveAllFactionsAsync(JavaPlugin plugin) {

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Faction faction : factions) {
                saveEntity(faction);
            }
            plugin.getLogger().info("All factions have been saved.");
        });

    }
    public CompletableFuture<Void> saveFactionFromBuilding(Building building) {
        return CompletableFuture.runAsync(() -> {
            try (Session session = sessionFactory.openSession()) {
                session.beginTransaction();

                for (Faction faction : factions) {
                    if (faction.getBuildings().contains(building)) {
                        session.saveOrUpdate(faction);
                        session.getTransaction().commit();
                    }
                }

                session.getTransaction().commit();
                throw new IllegalArgumentException("Building doesn't belong to any faction!");

            } catch (Exception e) {
                this.consoleError(e);
            }
        });
    }
    //       //       //       //       //       //       //       //       //       //       //       //       //
    public List<Faction> getAllFactions() {
        return this.factions;
    }
    public Faction getFactionByID(int id) {
        for (Faction faction : factions) {
            if (faction.getId() == id) {
                return faction;
            }
        }
        return null;
    }
    public Faction getFactionForBuilding(Building building) {
        for (Faction faction : this.factions) {
            if (faction.getBuildings().contains(building)) {
                return faction;
            }
        }
        return null;
    }
    public Faction getFactionForPlayer(Player player) {
        for (Faction faction : this.factions) {
            if (faction.isFactionPlayer(player)) {
                return faction;
            }
        }
        return null;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();

        for (Faction faction : factions) {
            if (!faction.isFactionPlayer(player)) {
                for (Territory territory : faction.getTerritory()) {
                    if (territory.contains(location)) {
                        for (Restriction restriction : faction.getRestrictions()) {
                            if (restriction.getRestriction().equals(Restriction.RestrictionType.BLOCK_BREAK)) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Location location = block.getLocation();

        for (Faction faction : factions) {
            if (!faction.isFactionPlayer(player)) {
                for (Territory territory : faction.getTerritory()) {
                    if (territory.contains(location)) {
                        for (Restriction restriction : faction.getRestrictions()) {
                            if (restriction.getRestriction().equals(Restriction.RestrictionType.BLOCK_BREAK)) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void  onEntityDamagebyEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player attacker = (Player) event.getDamager();

            if (event.getEntity() instanceof Player) {
                Player victim = (Player) event.getEntity();

                if (this.getFactionForPlayer(attacker).equals(this.getFactionForPlayer(victim))) {
                    for (Restriction restriction : this.getFactionForPlayer(attacker).getRestrictions()) {
                        if (restriction.getRestriction().equals(Restriction.RestrictionType.FRIENDLY_FIRE)) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }



    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //
    /*
                             88""Yb     888888      dP""b8     88      dP"Yb      88b 88
                             88__dP     88__       dP   `"     88     dP   Yb     88Yb88
                             88"Yb      88""       Yb  "88     88     Yb   dP     88 Y88
                             88  Yb     888888      YboodP     88      YbodP      88  Y8
    */
    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

    private void giveRegionWand(Player player) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();

        meta.displayName(Component.text("Region Wand"));
        wand.setItemMeta(meta);

        player.getInventory().addItem(wand);
        player.sendMessage("You have been given the Region Wand.");
    }
    private void addRegionToBuilding(Player player, int buildingId) {
        Location pointA = regionWandManager.getPointA(player.getUniqueId());
        Location pointB = regionWandManager.getPointB(player.getUniqueId());

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

        try {
            building.addRegion(region);
            CompletableFuture.allOf(
                    PluginInstance.getInstance().saveEntityAsync(faction),
                    PluginInstance.getInstance().saveEntityAsync(building),
                    PluginInstance.getInstance().saveEntityAsync(region)
            ).thenRun(() -> {
                player.sendMessage("New region was added to the building " + buildingId);
                regionWandManager.getPointA().remove(player.getUniqueId());
                regionWandManager.getPointB().remove(player.getUniqueId());
            }).exceptionally(e -> {
                building.removeRegion(region);
                throw new IllegalStateException("Query failed! ", e);
            });
        } catch (Exception e) {
            PluginInstance.getInstance().consoleError(e);
        }

    }

    public CompletableFuture<Block> lookForBlock(List<Region> regions, Material material) {
        return CompletableFuture.supplyAsync(() -> {
            for (Region region : regions) {
                for (Block block : region.getBlocks()) {
                    if (block.getType() == material) {
                        return block;
                    }
                }
            }
            return null;
        });
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //
    /*
                          .dP"Y8     888888        db        888888     88   88     .dP"Y8
                          `Ybo."       88         dPYb         88       88   88     `Ybo."
                          o.`Y8b       88        dP__Yb        88       Y8   8P     o.`Y8b
                          8bodP'       88       dP""""Yb       88       `YbodP'     8bodP'
    */
    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- //

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
                        this.giveRegionWand(player);
                    } else {
                        sender.sendMessage("This command can only be executed by a player.");
                    }
                    return true;

                case "building":
                    if (args.length > 3 &&  args[2].equalsIgnoreCase("add") &&
                            args[3].equalsIgnoreCase("region")) {

                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            int buildingId = Integer.parseInt(args[1]);
                            this.addRegionToBuilding(player, buildingId);

                        } else {
                            sender.sendMessage("This command can only be executed by a player.");
                        }
                        return true;
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

                                String factionName = args[2];
                                Faction faction = new Faction(factionName, player);

                                try {
                                    this.factions.add(faction);
                                    CompletableFuture.allOf(
                                            this.saveEntityAsync(faction)
                                    ).thenRun(() -> {
                                        player.sendMessage("Faction " + factionName + " was created with ID " + factions.size());
                                    }).exceptionally(e -> {
                                        this.factions.remove(faction);
                                        player.sendMessage("You were not able to create faction " + factionName);
                                        throw new IllegalStateException("Query failed! ", e);
                                    });
                                } catch (Exception e) {
                                    this.consoleError(e);
                                }

                            } else {
                                sender.sendMessage("This command can only be executed by a player.");
                                return true;
                            }

                        case "delete":
                            if (sender instanceof Player) {
                                Player player = (Player) sender;
                                Faction faction = this.getFactionForPlayer(player);

                                if (faction != null && faction.getPlayersUUID().indexOf(player.getUniqueId().toString()) == 0) {
                                    try {
                                        this.factions.remove(faction);
                                        CompletableFuture.allOf(
                                                PluginInstance.getInstance().deleteEntityAsync(faction)
                                        ).thenRun(() -> {
                                            player.sendMessage("Faction " + faction.getName() + " was deleted.");
                                        }).exceptionally(e -> {
                                            this.factions.add(faction);
                                            player.sendMessage("You were not able to delete faction " + faction.getName());
                                            throw new IllegalStateException("Query failed! ", e);
                                        });
                                    } catch (Exception e) {
                                        this.consoleError(e);
                                    }
                                } else {
                                    player.sendMessage("You don't have permission to delete this Faction.");
                                }
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
                            CompletableFuture.allOf(
                                    PluginInstance.getInstance().saveEntityAsync(faction)
                            ).thenRun(() -> {
                                player.sendMessage("You left faction " + faction.getName());
                            }).exceptionally(e -> {
                                faction.addPlayer(player);
                                player.sendMessage("You were not able to leave faction " + faction.getName());
                                throw new IllegalStateException("Query failed! ", e);
                            });
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
                            try {
                                actualFaction.removePlayer(player);
                                CompletableFuture.allOf(
                                        PluginInstance.getInstance().saveEntityAsync(actualFaction)
                                ).thenRun(() -> {
                                    player.sendMessage("You left faction " + actualFaction.getName());
                                }).exceptionally(e -> {
                                    actualFaction.addPlayer(player);
                                    player.sendMessage("You were not able to leave faction " + actualFaction.getName());
                                    throw new IllegalStateException("Query failed! ", e);
                                });
                            } catch (Exception e) {
                                throw new IllegalStateException("Query failed! ", e);
                            }
                        }

                        Invitation invitation = getInvitationForPlayer(player);

                        if (invitation == null) {
                            player.sendMessage("You didn't receive any invitations.");
                            return true;
                        } else {
                            try {
                                invitation.getFaction().addPlayer(player);
                                CompletableFuture.allOf(
                                        PluginInstance.getInstance().saveEntityAsync(invitation.getFaction())
                                ).thenRun(() -> {
                                    player.sendMessage("You joined faction " + invitation.getFaction().getName());
                                    invitations.remove(invitation);
                                }).exceptionally(e -> {
                                    invitation.getFaction().removePlayer(player);
                                    player.sendMessage("You were not able to join faction " + invitation.getFaction().getName());
                                    throw new IllegalStateException("Query failed! ", e);
                                });
                            } catch (Exception e) {
                                throw new IllegalStateException("Query failed! ", e);
                            }
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
