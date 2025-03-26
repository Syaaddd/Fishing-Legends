package core.sg.fishinglegend;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BoatManager implements Listener, CommandExecutor {
    private final JavaPlugin plugin;
    private final HashMap<UUID, Boat> playerBoats = new HashMap<>();

    public BoatManager(JavaPlugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("boat").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("remove")) {
            removeBoat(player);
            return true;
        }

        spawnBoat(player);
        return true;
    }

    private void spawnBoat(Player player) {
        if (playerBoats.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You already have a boat! Use /boat remove to remove it.");
            return;
        }

        int distance = 5;
        int distanceY = 5;

        Location loc = player.getLocation();
        World world = player.getWorld();

        int minX = (int) (loc.getX() - distance);
        int maxX = (int) (loc.getX() + distance);
        int minY = (int) (loc.getY() - distanceY);
        int maxY = (int) (loc.getY() + distanceY);
        int minZ = (int) (loc.getZ() - distance);
        int maxZ = (int) (loc.getZ() + distance);

        Location checkLoc = loc.clone();


        while (checkLoc.getBlock().getType() != Material.WATER && checkLoc.getY() > 0) {
            checkLoc.subtract(0, 1, 0);
        }
        if (checkLoc.getBlock().getType() != Material.WATER) {
            player.sendMessage(ChatColor.RED + "You must be above water to spawn a boat!");
            return;
        }

//        if (!checkLoc.add(0, 1, 0).getBlock().isEmpty()) {
//            player.sendMessage(ChatColor.RED + "Cannot spawn boat! There is a block above the water.");
//            return;
//        }


        Block highestBlock = null;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <=maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.WATER) {
                        if (highestBlock == null || block.getY() > highestBlock.getY()) {
                            highestBlock = block;
                        }
                    }
                }
            }
        }


        if (highestBlock == null) {
            player.sendMessage(ChatColor.RED + "NO WATER");
            return;
        }

        Location boatSpawn = highestBlock.getLocation().add(0.5, 1, 0.5);

        if (!boatSpawn.clone().add(0, 1, 0).getBlock().isEmpty()) {
            player.sendMessage("THERES SOMETHING ONTOP OF THE BOAT!");
            return;
        }


        Boat boat = player.getWorld().spawn(boatSpawn, Boat.class);
        int xpLevel = Math.min(player.getLevel(), 20);
        boat.setVelocity(boat.getVelocity().multiply(0.5 + (xpLevel * 0.1)));

        playerBoats.put(player.getUniqueId(), boat);
        player.sendMessage(ChatColor.GREEN + "Boat spawned! Speed: " + xpLevel);
    }


    private void removeBoat(Player player) {
        Boat boat = playerBoats.remove(player.getUniqueId());
        if (boat != null) {
            boat.remove();
            player.sendMessage(ChatColor.YELLOW + "Your boat has been removed.");
        } else {
            player.sendMessage(ChatColor.RED + "You have no boat to remove!");
        }
    }

//    @EventHandler
//    private void onPlayerExitVehicle(VehicleExitEvent event) {
//        if (event.getExited() instanceof Player) {
//            Player player = (Player) event.getExited();
//            if (player != null && playerBoats.containsKey(player.getUniqueId()));
//            playerBoats.remove(player.getUniqueId());
//            event.getVehicle().remove();
//        }
//    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeBoat(event.getPlayer());
    }

    @EventHandler
    public void onBoatDestroy(VehicleDestroyEvent event) {
        if (event.getVehicle() instanceof Boat) {
            Player player = (Player) event.getAttacker();
            if (player != null && playerBoats.containsKey(player.getUniqueId())) {
                playerBoats.remove(player.getUniqueId());
                event.setCancelled(true);
                event.getVehicle().remove();
                player.sendMessage(ChatColor.YELLOW + "Your boat has been removed.");
            }
        }
    }
}
