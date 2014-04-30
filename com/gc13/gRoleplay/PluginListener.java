package com.gc13.gRoleplay;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;


public class PluginListener implements Listener {
	private Plugin plugin;
	
	public PluginListener(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void playerInventory(InventoryClickEvent event) {
		
		
		if (event.isCancelled()) return;
		if (!event.getWhoClicked().getWorld().equals(plugin.getWorld())) return;
		if (!event.getInventory().getName().equals("Trader")) return;
		if (event.getSlotType() == SlotType.OUTSIDE || event.getSlotType() == SlotType.QUICKBAR) return;
		
		Trader trader = plugin.getCurrent((Player) event.getWhoClicked());
		if (trader == null) {
			event.getWhoClicked().closeInventory();
			return;
		}
		
		if (event.getSlot() == 7) {
			ItemStack price = trader.getStackPrice(event.getCursor());
			if (price != null) 
				event.getInventory().setItem(7, price);
			event.setCancelled(true);
			return;
		}
		
		if (event.getSlot() == 8) {
			boolean sold = trader.sellItem(event.getWhoClicked(), event.getCursor());

			if (!sold) {
				event.setCancelled(true);
				return;
			}
			
			if(event.getCursor() != null) {
				ItemStack cursor = null;
				if(event.getCursor().getType() == Material.MILK_BUCKET && sold) 
					cursor = new ItemStack(Material.BUCKET, 1);
				if(event.getCursor().getType() == Material.GOLD_NUGGET) {
					int ingamount = (int) Math.floor(event.getCursor().getAmount()/9);
					int loose = event.getCursor().getAmount()-(ingamount*9);
					cursor = new ItemStack(Material.GOLD_INGOT, ingamount);
					event.getWhoClicked().getInventory().addItem(new ItemStack(Material.GOLD_NUGGET, loose));
				}
				
				event.setCursor(cursor);
			}
			

			event.setCancelled(true);
			return;
		}
		
		if (event.getSlot() < 8) {
			ItemStack price = trader.getStackPrice(event.getCurrentItem());
			if (price == null) {
				event.setCancelled(true);
				return;
			}
			event.getInventory().setItem(7, price);
			event.setCancelled(true);
			return;
		}
		

	}
	
	
	@EventHandler
	public void damageTrader(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (player.isOp())
				return;
		}
		if (event.getEntity().getWorld().equals(plugin.getWorld()))
			if (event.getEntity() instanceof Villager)
				event.setCancelled(true);
	}
	
	@EventHandler
	public void interactPlayer(PlayerInteractEntityEvent event) {
		
		if (event.isCancelled()) return;
		if (!(event.getRightClicked() instanceof Villager)) return;
		if (!event.getRightClicked().getWorld().equals(plugin.getWorld())) return;
		if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
		
		Trader trader  = plugin.getTrader(plugin.getProf(event.getRightClicked()));
		if (trader == null) return;
		plugin.current.put(event.getPlayer(), trader);
		trader.createInv(plugin.getServer(), event.getPlayer());
		

	}
	
	
	@EventHandler
	public void interactPlayer(InventoryCloseEvent event) {
		if (plugin.current.containsKey((Player) event.getPlayer()))
			plugin.current.remove((Player) event.getPlayer());
		
	}
	
}
