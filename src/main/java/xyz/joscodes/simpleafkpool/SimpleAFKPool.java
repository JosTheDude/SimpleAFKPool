package xyz.joscodes.simpleafkpool;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Objects;
import java.util.UUID;

public class SimpleAFKPool extends JavaPlugin {
	private String commandRanInConsole;
	private String regionName;
	private String afkMessage;
	private Player player;
	@Override
	public void onEnable() {
		// Initialize configuration values
		saveDefaultConfig();
		commandRanInConsole = getConfig().getString("commandRanInConsole");
		regionName = getConfig().getString("regionName");
		afkMessage = getConfig().getString("afkMessage");

		int afkTime = getConfig().getInt("afkTime");

		// Schedule task to check for players in the region every minute
		getServer().getScheduler().runTaskTimer(this, () -> {
			player = checkRegionPlayers();
			if (player != null) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandRanInConsole.replace("%player%", player.getName()));
			}
		}, 0L, 20L * afkTime);
	}

	private Player checkRegionPlayers() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			Location location = player.getLocation();
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

			// github copilot moment -_- | speedcode
			World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(Objects.requireNonNull(location.getWorld()).getName());

			// Get the region manager for the spawn world
			RegionManager regions = container.get(world);

			if (regions == null) {
				continue;
			}

			ProtectedRegion region = regions.getRegion(regionName);

			if (region != null && region.contains(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))) {
				if (afkMessage != null) {
					afkMessage = ChatColor.translateAlternateColorCodes('&', afkMessage);
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(afkMessage.replace("%player%", player.getName())));
				}
				return player;
			}
		}
		return null;
	}



}
