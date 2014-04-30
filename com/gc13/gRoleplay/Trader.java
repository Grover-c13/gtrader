package com.gc13.gRoleplay;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Trader {
	public Item[] items;
	
	public Trader() {
		items = new Item[7];
	}
	
	public void createInv(Server server, HumanEntity player) {
		Inventory inv = server.createInventory(player.getInventory().getHolder(), 9, "Trader");
		for (Item item : items) {
			if (item == null) continue;
			inv.addItem(new ItemStack(item.type, 1));
		}
		player.openInventory(inv);
	}
	
	public double getPrice(Material mat) {
		for (Item item : items) {
			if (item == null) continue;
			if (item.type == mat) return item.value;
		}
		return 0;
	}
	
	public boolean sellItem(HumanEntity player, ItemStack item) {
		ItemStack price = getStackPrice(item);
		if (price == null) return false;
		player.getInventory().addItem(price);
		return true;
	}
	
	public ItemStack getStackPrice(ItemStack item) {
		if (item == null) return null;
		int rawPrice = (int) (getPrice(item.getType())*item.getAmount());
		if (rawPrice == 0) return null;
		Material material = Material.GOLD_NUGGET;
		double displayPrice = rawPrice;
		
		if (displayPrice > 64) {
			displayPrice = rawPrice/9;
			material = Material.GOLD_INGOT;
		}
		
		if (displayPrice > 64) {
			displayPrice = displayPrice/9;
			material = Material.GOLD_BLOCK;
		}
		
		if (displayPrice < 1) displayPrice = 1;
		return new ItemStack(material, (int) displayPrice);
	}
	
}
