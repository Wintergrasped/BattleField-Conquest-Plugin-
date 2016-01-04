package net.wmcsn.DeathMatch;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI implements Listener{

	public void CreateClassGUI(Player p) {
	     Inventory inv = p.getPlayer().getServer().createInventory(null, 18, "Select a Class");
	     // 18 is number of slots and must be divisible by 9

	     ItemStack istack = new ItemStack(Material.DIAMOND_BLOCK);
	     ItemMeta imeta = istack.getItemMeta();
	     imeta.setDisplayName("Assult");
	     istack.setItemMeta(imeta);
	     inv.addItem(istack);
	     
	     ItemStack istack2 = new ItemStack(Material.DIAMOND_BLOCK);
	     ItemMeta imeta2 = istack.getItemMeta();
	     imeta.setDisplayName("Engineer");
	     istack.setItemMeta(imeta2);
	     inv.addItem(istack2);
	     
	     ItemStack istack3 = new ItemStack(Material.DIAMOND_BLOCK);
	     ItemMeta imeta3 = istack.getItemMeta();
	     imeta.setDisplayName("Medic");
	     istack.setItemMeta(imeta3);
	     inv.addItem(istack3);
	     
	     ItemStack istack4 = new ItemStack(Material.DIAMOND_BLOCK);
	     ItemMeta imeta4 = istack.getItemMeta();
	     imeta.setDisplayName("Sniper");
	     istack.setItemMeta(imeta4);
	     inv.addItem(istack4);
	     
	     p.getPlayer().openInventory(inv);
	}
	
}
