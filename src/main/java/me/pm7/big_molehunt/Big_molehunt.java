package me.pm7.big_molehunt;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.*;

public final class Big_molehunt extends JavaPlugin implements CommandExecutor, Listener {


    public static final Set<Player> moles = new HashSet<>();
    public static Scoreboard board;
    public static Big_molehunt plugin;

    Team red;
    Team green;
    Team blue;
    Team yellow;

    @Override
    public void onEnable() {
        board = Bukkit.getScoreboardManager().getMainScoreboard();

        plugin = this;

        for(Team t : board.getTeams()) {
            t.unregister();
        }

        getConfig().options().copyDefaults(true);
        saveConfig();
        saveDefaultConfig();

        red = board.registerNewTeam("1Red");
        red.setCanSeeFriendlyInvisibles(false);
        red.setAllowFriendlyFire(true);
        //red.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        red.color(NamedTextColor.RED);

        green = board.registerNewTeam("2Green");
        green.setCanSeeFriendlyInvisibles(false);
        green.setAllowFriendlyFire(true);
        //green.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        green.color(NamedTextColor.GREEN);

        blue = board.registerNewTeam("3Blue");
        blue.setCanSeeFriendlyInvisibles(false);
        blue.setAllowFriendlyFire(true);
        //blue.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        blue.color(NamedTextColor.BLUE);

        yellow = board.registerNewTeam("4Yellow");
        yellow.setCanSeeFriendlyInvisibles(false);
        yellow.setAllowFriendlyFire(true);
        //yellow.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        yellow.color(NamedTextColor.YELLOW);

        getCommand("startmolehunt").setExecutor(this);
        getCommand("molemsg").setExecutor(new molemsg());
        getCommand("starttimer").setExecutor(new starttimer());
        Bukkit.getPluginManager().registerEvents(this, this);

        for(World w : Bukkit.getWorlds()) {
            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            w.setGameRule(GameRule.DO_INSOMNIA, false);
            w.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(sender instanceof Player p)) {
            return true;
        }

        if(!p.isOp()) return true;


        // spread
        Bukkit.broadcast(Component.text("Starting in 5 seconds...").color(NamedTextColor.RED));

        new BukkitRunnable() {

            List<Player> plrs = new ArrayList<>(Bukkit.getOnlinePlayers());
            int i=0;

            @Override
            public void run() {

                spread(plrs.get(i));
                i++;

                if(i>=plrs.size()) {
                    announce();
                    cancel();
                }

            }
        }.runTaskTimer(this, 100, 2);
        return true;
    }

    public void announce() {
        List<Player> teamless = new ArrayList<>(Bukkit.getOnlinePlayers());

        new BukkitRunnable() {
            int i=0;

            @Override
            public void run() {
                Team t;
                String message;
                NamedTextColor color;

                switch (i) {
                    case 0 -> {
                        t = board.getTeam("1Red");
                        message = "Red Team:";
                        color = NamedTextColor.RED;
                    }
                    case 1 -> {
                        t = board.getTeam("2Green");
                        message = "Green Team:";
                        color = NamedTextColor.GREEN;
                    }
                    case 2 -> {
                        t = board.getTeam("3Blue");
                        message = "Blue Team:";
                        color = NamedTextColor.BLUE;
                    }
                    case 3 -> {
                        t = board.getTeam("4Yellow");
                        message = "Yellow Team:";
                        color = NamedTextColor.YELLOW;
                    }
                    default -> {
                        cancel();

                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            for(Player plr : Bukkit.getOnlinePlayers()) plr.sendTitle(ChatColor.YELLOW + "You are...", "", 10, 70, 20);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                                moles.add(getRandomPlayer(red));
                                moles.add(getRandomPlayer(green));
                                moles.add(getRandomPlayer(blue));
                                moles.add(getRandomPlayer(yellow));

                                // announce the funky
                                for(Player plr : Bukkit.getOnlinePlayers()) {
                                    if(moles.contains(plr)) plr.sendTitle("§c§lThe Mole.", "", 10, 70, 20);
                                    else plr.sendTitle("§a§lNOT The Mole.", "", 10, 70, 20);
                                }

                                // announce the funkier
                                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                    StringBuilder moleowo = new StringBuilder("Moles: ");
                                    for(Player p : moles) {
                                        if(p==null) continue;
                                        moleowo.append(p.getName()).append(", ");
                                    }
                                    String finalmoleowo = moleowo.substring(0, moleowo.length() - 2);

                                    for(Player p : moles) {
                                        if(p==null) continue;
                                        p.sendMessage(Component.text(finalmoleowo).color(NamedTextColor.RED));
                                    }
                                }, 120L);
                            }, 100L);
                        }, 1200);

                        return;
                    }
                }
                i++;

                titleAll(message, "", color, 0, 200, 0);
                soundAll(Sound.BLOCK_VAULT_ACTIVATE, 0.5f);

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    Player random1 = teamless.get((int) (Math.random() * teamless.size()));
                    String name1 = random1.getName();
                    titleAll(message, name1 + ",", color, 0, 200, 0);
                    soundAll(Sound.BLOCK_VAULT_CLOSE_SHUTTER, 1.0f);
                    t.addEntry(name1);
                    if(teamless.size() > 1) teamless.remove(random1);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        Player random2 = teamless.get((int) (Math.random() * teamless.size()));
                        String name2 = random2.getName();
                        titleAll(message, name1 + ", " + name2, color, 0, 200, 0);
                        soundAll(Sound.BLOCK_VAULT_CLOSE_SHUTTER, 1.0f);
                        t.addEntry(name2);
                        if(teamless.size() > 1) teamless.remove(random2);


                        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                            Player random3 = teamless.get((int) (Math.random() * teamless.size()));
                            String name3 = random3.getName();
                            titleAll(message, name1 + ", " + name2 + ", " + name3, color, 0, 200, 0);
                            soundAll(Sound.BLOCK_VAULT_CLOSE_SHUTTER, 1.0f);
                            t.addEntry(name3);
                            if(teamless.size() > 1) teamless.remove(random3);

                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                Player random4 = teamless.get((int) (Math.random() * teamless.size()));
                                String name4 = random4.getName();
                                titleAll(message, name1 + ", " + name2 + ", " + name3 + ", " + name4, color, 0, 20, 20);
                                soundAll(Sound.BLOCK_VAULT_CLOSE_SHUTTER, 1.0f);
                                t.addEntry(name4);
                                if(teamless.size() > 1) teamless.remove(random4);
                            }, 20L);
                        }, 20L);
                    }, 20L);
                }, 40L);

            }
        }.runTaskTimer(this, 200, 140L);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) {
            return;
        }

        if (!(p.getHealth() - e.getFinalDamage() <= 0)) {
            return;
        }

        e.setCancelled(true);
        p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_GENERIC_HURT, 1L, 1L);
        p.setGameMode(GameMode.SPECTATOR);
        p.sendTitle(ChatColor.RED + ":(", ":(", 10, 70, 20);

        // dropp :3
        Inventory inv = p.getInventory();
        Location loc = p.getLocation();
        World world = p.getWorld();
        double power = 0.2D;
        for(ItemStack item : inv.getContents()) {
            if(item != null && item.getItemMeta() != null) {
                double xVel = -power + (Math.random() * (power*2));
                double zVel = -power + (Math.random() * (power*2));
                Entity dropped = world.dropItem(loc, item);
                dropped.setVelocity(new Vector(xVel, 0.3, zVel));
            }
        }
        inv.clear();

        if(e.getDamageSource().getCausingEntity() instanceof Player killer) {
            if( (red.getEntries().contains(p.getName()) && red.getEntries().contains(killer.getName())) ||
                    (green.getEntries().contains(p.getName()) && green.getEntries().contains(killer.getName())) ||
                    (blue.getEntries().contains(p.getName()) && blue.getEntries().contains(killer.getName())) ||
                    (yellow.getEntries().contains(p.getName()) && yellow.getEntries().contains(killer.getName()))
            ) {
                double currentMax = killer.getAttribute(Attribute.MAX_HEALTH).getValue();
                killer.getAttribute(Attribute.MAX_HEALTH).setBaseValue(currentMax + 4.0d);
            }
        }

    }

    private final Random random = new Random();
    private Player getRandomPlayer(Team team) {
        if (team == null) return null;

        List<Player> players = new ArrayList<>();
        for (String entry : team.getEntries()) {
            Player player = Bukkit.getPlayer(entry);
            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }

        if (players.isEmpty()) {
            return null;
        }

        return players.get(random.nextInt(players.size()));
    }

    private static final long TICKS_TO_MILLIS = 50;
    public static void titleAll(String title, String subtitle, NamedTextColor color, int fadeIn, int hold, int fadeOut) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.showTitle(Title.title(
                    Component.text(title).color(color),
                    Component.text(subtitle).color(color),
                    Title.Times.times(
                            Duration.ofMillis(fadeIn * TICKS_TO_MILLIS),
                            Duration.ofMillis(hold * TICKS_TO_MILLIS),
                            Duration.ofMillis(fadeOut * TICKS_TO_MILLIS)
                    )
            ));
        }
    }
    public static void soundAll(Sound sound, float pitch) {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, sound, 2, pitch);
        }
    }

    private void spread(Player p) {
        if(p == null) { return; }

        World main = Bukkit.getWorlds().getFirst();
        WorldBorder border = main.getWorldBorder();
        Location gameLoc = border.getCenter().clone();
        double spreadDistance = plugin.getConfig().getDouble("spreadDistance");
        double x = (gameLoc.getX() + (Math.random() * (spreadDistance - 4)) - (spreadDistance/2));
        double z = (gameLoc.getZ() + (Math.random() * (spreadDistance - 4)) - (spreadDistance/2));

        Block ground = gameLoc.getWorld().getHighestBlockAt((int) x, (int) z);
        while(!ground.getType().isSolid() && ground.getType() != Material.WATER) {
            x = (gameLoc.getX() + (Math.random() * (spreadDistance - 4)) - (spreadDistance/2));
            z = (gameLoc.getZ() + (Math.random() * (spreadDistance - 4)) - (spreadDistance/2));
            ground = gameLoc.getWorld().getHighestBlockAt((int) x, (int) z);
        }

        Location oldLoc = p.getLocation().clone();

        Location tpLoc = ground.getLocation().add(0.5, 1, 0.5);
        tpLoc.setYaw(((float) Math.random()*360) - 180);
        p.teleport(tpLoc);

        // Sounds and effects
        main.playSound(oldLoc, Sound.ENTITY_BREEZE_LAND, 500, 0.9f);
        main.playSound(tpLoc, Sound.ENTITY_BREEZE_LAND, 500, 0.9f);
        main.spawnParticle(Particle.GUST_EMITTER_SMALL, oldLoc, 1);
        main.spawnParticle(Particle.GUST_EMITTER_SMALL, tpLoc, 1);
    }


    @EventHandler
    public void onPlrJoin(PlayerJoinEvent e) {
        e.joinMessage(null);
    }

    @EventHandler
    public void onPlrQuit(PlayerQuitEvent e) {
        e.quitMessage(null);
    }

    public static Big_molehunt getPlugin() {
        return plugin;
    }

}
