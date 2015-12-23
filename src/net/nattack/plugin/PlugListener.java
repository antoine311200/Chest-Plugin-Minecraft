package net.nattack.plug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;

public class PlugListener implements Listener {
	
	private Plugin plugin;
	
	public int time = 0;
	public int task;
	public int maxTime;
	public int max;
	public int min;
	public int nbKit;
	
	public Location lastChestPos;

	public PlugListener(Plugin plugin) {
		this.plugin = plugin;
		this.lastChestPos = new Location(plugin.getServer().getWorld("world"), 0, 80, 0);

		this.maxTime = (int) plugin.getConfig().get("time");
		this.max = (int) plugin.getConfig().get("maps-max");
		this.min = (int) plugin.getConfig().get("maps-min");
		this.nbKit = (int) plugin.getConfig().get("kit-number");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		
		Player p = e.getPlayer();
		String message = e.getMessage();
		
		String[] params = message.split(" ");
		
		if(params[0].equalsIgnoreCase("/chest") && params[1].equalsIgnoreCase("create")) {
			Location loc = new Location(plugin.getServer().getWorld("world"), 0, 0, 0);
			
			int randX = (int) (Math.floor(Math.random() * this.max) + this.min);
			int randZ = (int) (Math.floor(Math.random() * this.max) + this.min);
			
		    loc.setX(randX);
		    loc.setY(loc.getWorld().getHighestBlockYAt(randX, randZ));
		    loc.setZ(randZ);
		    
		    Block b = loc.getBlock();
		    b.setType(Material.CHEST);
		    Chest chest = (Chest) b.getState();

			int randItem = (int) (Math.random()*(10-1));
			System.out.println(nbKit);
			
			List<List<ItemStack>> loot = new ArrayList<List<ItemStack>>();

			for(int i = 0; i < nbKit; i++) {
				loot.add(new ArrayList<ItemStack>());
				int kit = i+1;
				List<String> elem = plugin.getConfig().getStringList("chests.kit"+Integer.toString(kit));
				
				for(int j = 0; j < elem.size(); j++) {
					String[] items = elem.get(j).split(" ");
					
					if(items[1] == "rand10") {
						items[1] = Integer.toString(randItem);
					}
					loot.get(i).add(new ItemStack(Material.getMaterial(items[0]), Integer.parseInt(items[1])));
					if(items.length > 2) {
						for(int z = 2; z < items.length; z++) {
							List<String> enchant = Arrays.asList(items[z].split(","));
							loot.get(i).get(j).addEnchantment(
								Enchantment.getByName(enchant.get(0)), 
								Integer.parseInt(enchant.get(1))
							);
						}
					}
				}
			}

			int randKit = (int)(Math.random() * (nbKit));
			
			for(int i = 0; i < loot.get(randKit).size(); i++) {
				chest.getInventory().addItem(loot.get(randKit).get(i));
			}
			
			this.lastChestPos.setX(loc.getX());
			this.lastChestPos.setY(loc.getY()+5);
			this.lastChestPos.setZ(loc.getZ());
			
		    plugin.getServer().broadcastMessage(
		    	ChatColor.RED + p.getName() + 
		    	ChatColor.AQUA + " a fait spawn un coffre bonus en :"
		    );
		    plugin.getServer().broadcastMessage(ChatColor.GREEN + "X = " + ChatColor.BLUE + loc.getX());
		    plugin.getServer().broadcastMessage(ChatColor.GREEN + "Y = " + ChatColor.BLUE + loc.getY());
		    plugin.getServer().broadcastMessage(ChatColor.GREEN + "Z = " + ChatColor.BLUE + loc.getZ());
		}
		else if(params[0].equalsIgnoreCase("/chest") && params[1].equalsIgnoreCase("start")) {
			if(p.isOp()) {
				plugin.getServer().broadcastMessage(
					ChatColor.GREEN +"La génération des "+ 
					ChatColor.GOLD + "coffres Bonus "+ 
					ChatColor.GREEN + "a été démarrée par "+ 
					ChatColor.RED + p.getName()
				);
				time = 0;
				timer();
			}
			else {
				p.sendMessage(ChatColor.RED +"Il faut être Admin pour faire cette commande.");
			}
		}
		else if(params[0].equalsIgnoreCase("/chest") && params[1].equalsIgnoreCase("stop")) {
			if(p.isOp()) {
				time = 0;
				plugin.getServer().getScheduler().cancelTask(task);
				plugin.getServer().broadcastMessage(
					ChatColor.GREEN +"La génération des "+ 
					ChatColor.GOLD + "coffres Bonus "+ 
					ChatColor.GREEN + "a été arrêtée par "+ 
					ChatColor.RED + p.getName()
				);
			}
			else {
				p.sendMessage(ChatColor.RED +"Il faut être Admin pour faire cette commande.");
			}
		}
		else if(params[0].equalsIgnoreCase("/chest") && params[1].equalsIgnoreCase("tp")) {
			if(p.isOp()) {
				p.teleport(this.lastChestPos);
			}
			else {
				p.sendMessage(ChatColor.RED +"Il faut être Admin pour faire cette commande.");
			}
		}
		else if(params[0].equalsIgnoreCase("/chest") && params[1].equalsIgnoreCase("time")) {
			if(p.isOp()) {
				this.maxTime = Integer.parseInt(params[2]);
				plugin.getConfig().set("time", this.maxTime);
				plugin.saveConfig();
				
				plugin.getServer().broadcastMessage(
					ChatColor.GREEN +"Le temps entre la génération des "+ 
					ChatColor.GOLD + "coffres Bonus "+ 
					ChatColor.GREEN + "a été changé à "+ 
					ChatColor.BLUE + this.maxTime + 
					ChatColor.GREEN + " par "+ ChatColor.RED + p.getName()
				);
			}
			else {
				p.sendMessage(ChatColor.RED +"Il faut être Admin pour faire cette commande.");
			}
		}
		else if(params[0].equalsIgnoreCase("/chest") && params[1].equalsIgnoreCase("size")) {
			if(p.isOp()) {
				this.max = Integer.parseInt(params[2]);
				this.min = Integer.parseInt(params[3]);
				
				plugin.getConfig().set("maps-max", this.max);
				plugin.getConfig().set("maps-min", this.min);
				plugin.saveConfig();
				
				plugin.getServer().broadcastMessage(
					ChatColor.GREEN +"La taille de la zone de génération des "+ 
					ChatColor.GOLD + "coffres Bonus "+ 
					ChatColor.GREEN + "a été changée à "+
					ChatColor.BLUE+"[ "+this.min+", "+this.max+" ]"+ 
					ChatColor.GREEN + " par "+ 
					ChatColor.RED + p.getName()
				);
			}
			else {
				p.sendMessage(ChatColor.RED +"Il faut être Admin pour faire cette commande.");
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void timer() {
	task = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {

		@Override
		public void run() {
			
			Location loc = new Location(plugin.getServer().getWorld("world"), 0, 0, 0);

			int max = (int) plugin.getConfig().get("maps-max");
			int min = (int) plugin.getConfig().get("maps-min");
			
			int randX = (int) (Math.floor(Math.random() * max) + min);
			int randZ = (int) (Math.floor(Math.random() * max) + min);
			
		    loc.setX(randX);
		    loc.setY(loc.getWorld().getHighestBlockYAt(randX, randZ));
		    loc.setZ(randZ);
		    
			
			if(time < maxTime) {
				time = time + 1;
				
			}
			else if (time == maxTime-11) {
				plugin.getServer().broadcastMessage(
					ChatColor.GREEN + "Un" + 
					ChatColor.GOLD + " coffre Bonus "+ 
					ChatColor.GREEN + "va apparaître dans " + (maxTime-time)
				);
			}
			
			else {
				time = 0;
				
				plugin.getServer().getWorld("world").createExplosion(randX, loc.getWorld().getHighestBlockYAt(randX, randZ), randZ, 4F, false, true);
				
				Block b = loc.getBlock();
				b.setType(Material.CHEST);
				Chest chest = (Chest) b.getState();
				
				int randItem = (int) (Math.random()*(10-1));
				int nbKit = (int) plugin.getConfig().get("kit-number");
				
				List<List<ItemStack>> loot = new ArrayList<List<ItemStack>>();

				for(int i = 0; i < nbKit; i++) {
					loot.add(new ArrayList<ItemStack>());
					int kit = i+1;
					List<String> elem = plugin.getConfig().getStringList("chests.kit"+Integer.toString(kit));
					
					for(int j = 0; j < elem.size(); j++) {
						String[] items = elem.get(j).split(" ");
						
						if(items[1] == "rand10") {
							items[1] = Integer.toString(randItem);
						}
						loot.get(i).add(
							new ItemStack(Material.getMaterial(items[0]), 
							Integer.parseInt(items[1]))
						);
						if(items.length > 2) {
							for(int z = 2; z < items.length; z++) {
								List<String> enchant = Arrays.asList(items[z].split(","));
								//System.out.println(enchant.toString());
								
								loot.get(i).get(j).addEnchantment(
									Enchantment.getByName(enchant.get(0)), 
									Integer.parseInt(enchant.get(1))
								);
							}
						}
					}
				}

				int randKit = (int)(Math.random() * (nbKit));
				
				for(int i = 0; i < loot.get(randKit).size(); i++) {
					chest.getInventory().addItem(loot.get(randKit).get(i));
				}
				
				lastChestPos.setX(loc.getX());
				lastChestPos.setY(loc.getY()+5);
				lastChestPos.setZ(loc.getZ());

			    plugin.getServer().broadcastMessage(
			    	ChatColor.GREEN + "Un" + 
			    	ChatColor.GOLD + " coffre Bonus "+ 
			    	ChatColor.GREEN + "est apparu en :"
			    );
			    plugin.getServer().broadcastMessage(ChatColor.RED + "X = " + ChatColor.BLUE + loc.getX());
			    plugin.getServer().broadcastMessage(ChatColor.RED + "Y = " + ChatColor.BLUE + loc.getY());
			    plugin.getServer().broadcastMessage(ChatColor.RED + "Z = " + ChatColor.BLUE + loc.getZ());
			}
		}
	}, 0L,20L);
	}
}
