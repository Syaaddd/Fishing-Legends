package core.sg.fishinglegend;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FishingListener implements Listener {
    private final Map<Player, Double> playerPoints = new HashMap<>();
    private final Pattern tierPattern = Pattern.compile(".*\\((Common|Uncommon|Rare|Epic|Legendary|Mythic|Exotic)\\)");

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Player player = event.getPlayer();
            if (!(event.getCaught() instanceof ItemStack)) return;

            ItemStack caughtItem = (ItemStack) event.getCaught();
            if (caughtItem.hasItemMeta() && caughtItem.getItemMeta().hasDisplayName()) {
                String itemName = caughtItem.getItemMeta().getDisplayName();
                double points = calculatePoints(itemName);

                if (points > 0) {
                    playerPoints.put(player, playerPoints.getOrDefault(player, 0.0) + points);
                    player.sendMessage(ChatColor.GREEN + "You earned " + ChatColor.YELLOW + points + " points! Total: " + playerPoints.get(player));
                }
            }
        }
    }

    private double calculatePoints(String itemName) {
        double basePoints = 200;
        double multiplier = 1.5;
        Matcher matcher = tierPattern.matcher(itemName);

        if (matcher.find()) {
            switch (matcher.group(1)) {
                case "Common": return basePoints * Math.pow(multiplier, 1);
                case "Uncommon": return basePoints * Math.pow(multiplier, 2);
                case "Rare": return basePoints * Math.pow(multiplier, 3);
                case "Epic": return basePoints * Math.pow(multiplier, 4);
                case "Legendary": return basePoints * Math.pow(multiplier, 5);
                case "Mythic": return basePoints * Math.pow(multiplier, 6);
                case "Exotic": return basePoints * Math.pow(multiplier, 7);
            }
        }
        return 0; // Jika item tidak memiliki tier yang valid, tidak dapat poin
    }
}
