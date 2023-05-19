package xyz.joscodes.simpleafkpool;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Objects;

public class SimpleAFKPool extends JavaPlugin {
	private String commandRanInConsole;
	private String regionName;
	private String afkMessage;

	@Override
	public void onEnable() {
		// Initialize configuration values
		saveDefaultConfig();
		commandRanInConsole = getConfig().getString("commandRanInConsole");
		regionName = getConfig().getString("regionName");
		afkMessage = getConfig().getString("afkMessage");

		int afkTime = getConfig().getInt("afkTime");

		// Schedule task to check for players in the region every minute
		getServer().getScheduler().runTaskTimerAsynchronously(this, this::checkRegionPlayers, 0L, 20L * afkTime);
	}

	private void checkRegionPlayers() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			Location location = player.getLocation();
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

			// github copilot moment -_- | speedcode
			World world = WorldGuard.getInstance().getPlatform().getMatcher().getWorldByName(Objects.requireNonNull(location.getWorld()).getName());

			// Get the region manager for the spawn world
			RegionManager regions = container.get(world);

			if (regions == null) {
				return;
			}

			ProtectedRegion region = regions.getRegion(regionName);

			if (region != null && region.contains(BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ()))) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandRanInConsole.replace("%player%", player.getName()));
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', afkMessage));
			}
		}
	}

}
