package com.gc13.gRoleplay;


import java.util.HashMap;
import java.util.List;

import net.minecraft.server.EntityVillager;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
	public HashMap<Integer, Trader> traders;
	public HashMap<Player, Trader> current;
	
	public void onEnable() {
		new PluginListener(this);
		traders = new HashMap<Integer, Trader>();
		current = new HashMap<Player, Trader>();
		
		// Load traders
		
		List<Integer> allTraders = getConfig().getIntegerList("traders");
		for (int trader : allTraders) {
			Trader trade = new Trader();
			for (int i = 0; i <= 7; i++) {
				String type = getConfig().getString(trader + "." + i + ".type");
				double value = getConfig().getDouble(trader + "." + i + ".value", 0);
				if (type == null) continue;
				trade.items[i] = new Item(Material.valueOf(type), value);
			}
			traders.put(trader, trade);
		}
	}

	public void onDisable() {
		// Save traders
		traders.clear();
		current.clear();
	}

	public World getWorld() {
		return getServer().getWorld(getConfig().getString("world", "city_rp"));
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!label.equals("gecon") && !sender.isOp())
			return false;
		
		if (!(sender instanceof Player))
			return false;

		if (args.length < 1)
			return false;
		
		
		if (args[0].equals("create")) {
			
			if (args.length != 2) return false;
			Integer set = null;
			try { set = Integer.parseInt(args[1]); } catch(Exception e) { return false; }
			if (set == null) return false;
			sender.sendMessage("Trader Created!");
			Villager ent = ((Villager) getWorld().spawnCreature(((Player)sender).getLocation(), EntityType.VILLAGER));
			setProf(ent, set);

		}
		
		return true;
		
	}
	
	public Trader getTrader(Integer id) {
		if (traders.containsKey(id)) return traders.get(id);
		return null;
	}
	
	
	public Trader getCurrent(Player player) {
		if (current.containsKey(player)) return current.get(player);
		return null;
	}

	public int getProf(Entity ent) {
		CraftEntity craftEnt = ((CraftEntity) ent);
		EntityVillager villager = (EntityVillager) craftEnt.getHandle();
		return villager.getProfession();
	}
	
	public void setProf(Entity ent, int prof) {
		CraftEntity craftEnt = ((CraftEntity) ent);
		EntityVillager villager = (EntityVillager) craftEnt.getHandle();
		villager.setProfession(prof);
	}

}
