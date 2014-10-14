package io.github.jisaacs1207.trickortreat;

import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public final class TrickOrTreat extends JavaPlugin implements Listener{
	public static Economy econ = null;
	@Override
	public void onEnable() {
		getLogger().info("Trick or Treating is enabled!");
		getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		setupEconomy();
		getWorldGuard();
	}
 
	@Override
	public void onDisable() {
		getLogger().info("Trick or Treat has been disabled.");
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onBlockPlace(BlockPlaceEvent event){
		Player player = event.getPlayer(); // player name
		if((player.getItemInHand().getTypeId()==354)&&(player.getItemInHand().getDurability()==50)){
			player.sendMessage(ChatColor.RED + "The cake is a lie.");
			player.playEffect(event.getBlock().getLocation(),Effect.SMOKE,5);
			event.getBlock().setType(Material.AIR);
		}
	}
	
	@EventHandler
	public void onPlayerThrow(PlayerInteractEvent event) {
		if(event.getPlayer().getVehicle()!=null){
			if(event.getAction().equals(Action.LEFT_CLICK_AIR) && event.getPlayer().getVehicle().equals(EntityType.PLAYER)) {
		        Player toThrow = event.getPlayer();
		        Player thrower = (Player) event.getPlayer().getVehicle();
		        Vector dir = thrower.getLocation().getDirection();
		        Vector vec = new Vector(dir.getX(), dir.getY()*.9D, dir.getZ());
		        thrower.eject();
		        toThrow.setVelocity(vec);
		    }
		}    
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPieClick(PlayerInteractEvent event){
		Player player = event.getPlayer(); // player name
		Action pAction = event.getAction();
		if (pAction == Action.RIGHT_CLICK_AIR || pAction == Action.RIGHT_CLICK_BLOCK ) {
			if((player.getItemInHand().getTypeId()==400)&&(player.getItemInHand().getDurability()==2)){
				int pies = player.getItemInHand().getAmount()-1;
				if(pies>0){
					player.getInventory().getItemInHand().setAmount(pies);
					ItemStack seeds = new ItemStack( Material.PUMPKIN_SEEDS);
					player.getInventory().addItem( seeds );
				}
				if(pies==0){
					player.getInventory().getItemInHand().setType(Material.PUMPKIN_SEEDS);
				}
				player.setFoodLevel(20);
				player.playSound(player.getLocation(), Sound.ENDERMAN_SCREAM, 5, -3);
				player.sendMessage(ChatColor.GRAY+"Mmmm... horrifyingly delicious.");
				event.setCancelled(true);
			}
		}
		
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBoneClick(PlayerInteractEvent event){
		Player player = event.getPlayer(); // player name
		Action pAction = event.getAction();
		if (pAction == Action.RIGHT_CLICK_BLOCK ) {
			
			if((player.getItemInHand().getTypeId()==352)&&(player.getItemInHand().getDurability()==1)){
				if (getWorldGuard().canBuild(player, event.getClickedBlock().getLocation())){
					if (event.getClickedBlock().getTypeId()==2){
						int bones = player.getItemInHand().getAmount()-1;
						if(bones>0){
							player.getInventory().getItemInHand().setAmount(bones);
							ItemStack bonedust = new ItemStack( Material.getMaterial(351), 1, (byte)15 );
							player.getInventory().addItem( bonedust ); 
						}
						if(bones==0){
							ItemStack bonedust = new ItemStack( Material.getMaterial(351), 1, (byte)15 );
							player.getInventory().setItemInHand(bonedust);
						}
						player.setFoodLevel(20);
						player.playSound(player.getLocation(), Sound.FIZZ, 5, -3);
						player.playEffect(event.getClickedBlock().getLocation(),Effect.SMOKE,1);
						
							event.getClickedBlock().setTypeId(12);
							Location blockLoc = event.getClickedBlock().getLocation();
							double x=blockLoc.getBlockX();
							double y=blockLoc.getBlockY()+1;
							double z=blockLoc.getBlockZ();
							Location treeLoc = new Location (player.getWorld(),x,y,z);
							treeLoc.getBlock().setType(Material.DEAD_BUSH);
						
						event.setCancelled(true);
					}
					else player.sendMessage(ChatColor.GOLD+"You need to use that on a grass block.");
				}
				else player.sendMessage(ChatColor.GOLD+"You can't do that here.");
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEggClick(PlayerInteractEvent event){
		Player player = event.getPlayer(); // player name
		Action pAction = event.getAction();
		if (pAction == Action.RIGHT_CLICK_BLOCK ) {
			if((player.getItemInHand().getTypeId()==122)&&(player.getItemInHand().getDurability()==1)){
				if(getWorldGuard().canBuild(player, player.getTargetBlock(null, 5).getLocation())){
					double areaRadius;
					double minRadius;
					double t;
					double radius;
					double x;
					double y;
					double z;
					int yes;
					World world = player.getWorld();
					boolean notOkay = true;
					Location loc;
					Location loc2;
					Location loc3;
					Block block;
					while(notOkay){
						yes = 0;
						areaRadius = 6000; 
						minRadius = 200;
						t = Math.random() * Math.PI;
						radius = Math.random()*(areaRadius - minRadius) + minRadius;
						x = Math.cos(t) * radius;
						y = Math.sin(t) * radius;
						z = Math.sin(t) * radius;
						loc = new Location(world,x,y,z);
					    block = player.getWorld().getBlockAt(loc);
					    while(block.getTypeId()!=0){
					    	y=y+1;
							loc = new Location(world,x,y,z);
						    block = player.getWorld().getBlockAt(loc);
					    }
					    yes=yes+1;
					    
					    loc2 = new Location(world,x,y+1,z);
					    block = player.getWorld().getBlockAt(loc2);
					    while(block.getTypeId()!=0){
					    	y=y+1;
							loc2 = new Location(world,x,y+1,z);
						    block = player.getWorld().getBlockAt(loc2);
					    }
					    yes=yes+1;
					    
					    loc3 = new Location(world,x,y-1,z);
					    block = player.getWorld().getBlockAt(loc3);
					    if((block.getTypeId()!=0)&&(block.getTypeId()!=10)&&(block.getTypeId()!=11)) yes = yes+1;
					    
					    if(getWorldGuard().canBuild(player, block)) yes = yes+1;
					    
					    if(yes==4){
					    	notOkay=false;
					    	loc = new Location(world,x,y,z);
					    	player.playEffect(loc,Effect.SMOKE,5);
						    player.playSound(loc, Sound.ENDERMAN_TELEPORT, 10, 4);
						    player.teleport(loc);
						    player.damage(2);
					    }
					}	
					event.setCancelled(true);
				}
				else{
					player.sendMessage(ChatColor.RED +"Try placing the dung in an unprotected area!");
				}
				
			}
		}

	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onReaperDamage(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			boolean person = false;
			if(event.getEntity() instanceof Player) person = true;
			if(!person){
				Player player = (Player) event.getDamager(); // player name
				if((player.getItemInHand().getTypeId()==30196)&&(player.getItemInHand().getDurability()==-24)){
					player.getInventory().setItemInHand(null);
					event.setDamage(300);
					player.updateInventory();
					player.sendMessage(ChatColor.GRAY + "You've"+ChatColor.RED+ " REAPED " +ChatColor.GRAY+"the poor creature!");
					player.sendMessage(ChatColor.GOLD+ "With a flash, the Grim Reaper is gone.");
				}
			}	
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event){	
		Player player = event.getPlayer(); // player name
		Entity eClicked = event.getRightClicked(); // clicked entity
		
		if(eClicked instanceof Player){
			if(player.getItemInHand().getTypeId()==91){
				this.reloadConfig();
				List<String> treated = getConfig().getStringList(player.getName());
				boolean proceed = true;
				for (int i = 0; i < treated.size(); i++) {
				    String name = treated.get(i);
				    if(((Player) eClicked).getName().equalsIgnoreCase(name)) proceed = false;
				}
				if(proceed){
					Inventory inv = player.getInventory(); //Inventory of the player
	                ItemStack[] items = inv.getContents(); //Contents of player inventory
	                boolean hasEmptySlot = false;
	                for (ItemStack is : items) {
	                    if (is == null) {
	                        hasEmptySlot = true;
	                    }
	                }
	                if(hasEmptySlot){
	    				Player pClicked = (Player) eClicked; // clicked player
	    				int chance = (int )(Math.random() * 100 + 1);
	    // Begin ===================================================================================================================================
	    				player.sendMessage(ChatColor.GRAY + "You've " + ChatColor.GOLD + "TRICK OR TREATED " + ChatColor.GRAY + "another player!");
	    				pClicked.sendMessage(ChatColor.GRAY + "You've been " + ChatColor.GOLD + "TRICK OR TREATED " + ChatColor.GRAY + "by another player!");
	    				List<String> plAdd = getConfig().getStringList(player.getName());
	    				if(!plAdd.contains(pClicked.getName())){
	    				    plAdd.add(pClicked.getName());
	    				    getConfig().set(player.getName(), plAdd);
	    				    saveConfig();
	    				}
	    // Trick ===================================================================================================================================
	    				if(chance > 50){    
	    					player.sendMessage(ChatColor.GOLD+ pClicked.getDisplayName() +ChatColor.GRAY + " has chosen " + ChatColor.RED + "TRICK" + ChatColor.GRAY + ". MWAHAHAHA!");
	    					pClicked.sendMessage(ChatColor.GRAY + "You've chosen " + ChatColor.RED + "TRICK" + ChatColor.GRAY + ". MWAHAHAHA!");
	    					double d = Math.random();
	    					if (d < 0.1){ //10%
	    						getServer().dispatchCommand(getServer().getConsoleSender(), "jail " + player.getName() + " jail5 3m");
	    						double v = Math.random();
	    						double s = Math.random();
	    						double n = Math.random();
	    						String verb;
	    						String staff;
	    						String noun;
	    						if (v < 0.2) verb = "touching";
	    						else if (v < 0.4) verb = "biting";
	    						else if (v < 0.6) verb = "kicking";
	    						else if (v < 0.8) verb = "punching";
	    						else verb = "pestering";
	    						if (s < 0.2) staff = "Anita";
	    						else if (s < 0.4) staff = "Respawn";
	    						else if (s < 0.6) staff = "Tanis";
	    						else if (s < 0.8) staff= "Shadow";
	    						else staff = "Lucky";
	    						if (n < 0.2) noun = "No-Noes";
	    						else if (n < 0.4) noun = "cat";
	    						else if (n < 0.6) noun = "grapefruit";
	    						else if (n < 0.8) noun= "monkey";
	    						else noun = "underwear";
	    						getServer().broadcastMessage(ChatColor.RED + player.getName() + ChatColor.GOLD + " has been jailed for " + verb + " " + staff + "'s " + noun + "." );
	    					}
	    					else if (d < 0.2){  //10%
	    						ItemStack hStake = new ItemStack(Material.getMaterial(280), 1, (byte) 1);
								ItemMeta m = hStake.getItemMeta();
								m.setDisplayName(ChatColor.RED+ "The Vampire Slayer");
								ArrayList<String> lore = new ArrayList<String>();
								lore.add("NOTE : Not intended for use");
								lore.add("on real vampires.");
								m.setLore(lore);
								hStake.setItemMeta(m);
								player.getInventory().addItem(new ItemStack(hStake));
								player.sendMessage(ChatColor.GREEN + "You've been given the " +ChatColor.RED+ "VAMPIRE SLAYER" + ChatColor.GREEN + "!");
	    						player.getInventory().setItemInHand(hStake);
	    					}
	    					else if (d < 0.3){  //10%
	    						player.teleport(new Location(Bukkit.getWorld("world"), -871,550,-734));
	    						player.sendMessage(ChatColor.GREEN + "Welcome to the great beyond!");
	    						player.sendMessage(ChatColor.GOLD+ "Enjoy the brief show, astronaut!");
	    						player.sendMessage(ChatColor.DARK_GRAY + "(You may not enjoy the landing.)");
	    					}
	    					else if (d < 0.4){  //10%
	    						player.setHealth(1);
	    						Location pLoc = player.getLocation();
	    						getServer().getWorld("world").createExplosion(pLoc, 0F);
	    						player.sendMessage(ChatColor.RED+"BOOM SHAKALAKA, SHAKALAKA SHAKALAKA!");
	    					}
	    					else if (d < 0.5){  //10%
	       						String pName = player.getName();
	       						String pAdd;
	       						getServer().dispatchCommand(getServer().getConsoleSender(), "nick " + player.getName() + " off");
	       						double n = Math.random();
	       						if (n < 0.1) pAdd = "Lame_";
	       						else if (n < 0.1) pAdd = "Stinky_";
	       						else if (n < 0.2) pAdd = "Ugly_";
	       						else if (n < 0.3) pAdd = "Simple_";
	       						else if (n < 0.4) pAdd = "Stupid_";
	       						else if (n < 0.5) pAdd = "Weak_";
	       						else if (n < 0.6) pAdd = "Whiny_";
	       						else if (n < 0.7) pAdd = "Poopy_";
	       						else if (n < 0.8) pAdd = "Baby_";
	       						else if (n < 0.9) pAdd = "Grumpy_";
	       						else pAdd = "Doofus_";
	       						String newName = pAdd + pName;
	       						getServer().dispatchCommand(getServer().getConsoleSender(), "nick " + player.getName() + " " + newName);
	       						player.sendMessage(ChatColor.GOLD+"Enjoy your new name, " + newName + "!");
	    					}
	    					else if (d < 0.6){  //10%
	    						ItemStack theCake = new ItemStack(Material.CAKE, 1, (byte) 50);
								ItemMeta m = theCake.getItemMeta();
								m.setDisplayName(ChatColor.RED + "The Lie");
								ArrayList<String> lore = new ArrayList<String>();
								lore.add("Your promised confection is merely");
								lore.add("a fictitious motivator.");
								m.setLore(lore);
								theCake.setItemMeta(m);
								player.getInventory().addItem(new ItemStack(theCake));
								player.sendMessage(ChatColor.GREEN + "You've been given the " +ChatColor.RED+ "LIE" + ChatColor.GREEN + "!");
	    					}
	    					else if (d < 0.7){  //10%
	    						player.setFoodLevel(1);
	    						Location pLoc = player.getLocation();
	    						player.playEffect(pLoc,Effect.SMOKE,5);
	    						player.sendMessage(ChatColor.RED+"Your tummy rumbles.");
	    					}
	    					else if (d < 0.8){  //10%
	    						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2400, 3));
	    						player.sendMessage(ChatColor.RED+"Hey?! Where did everyone go?");
	    					}
	    					else if (d < 0.9){  //10%
	    						player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2400, 3));
	    						player.sendMessage(ChatColor.RED+"Vertigo? Well, sucks to be you. Haw!");
	    					}
	    					else{ //10%
	    						player.setPassenger(eClicked);
	    						player.sendMessage(ChatColor.RED+"You decide to give them a ride on your back.");
	    					}
	    				}
	    // Treat ===================================================================================================================================
	    				else{  
	    					player.sendMessage(ChatColor.GOLD+ pClicked.getDisplayName() +ChatColor.GRAY + " has chosen " + ChatColor.GREEN + "TREAT" + ChatColor.GRAY + ". Awww.");
	    					pClicked.sendMessage(ChatColor.GRAY + "You've chosen " + ChatColor.GREEN + "TREAT" + ChatColor.GRAY + ". Awww.");
	    					double d = Math.random();
	    					double reward = Math.random();
	    					if (d < 0.6){  //60%
	    						player.sendMessage(ChatColor.GOLD+ "You've received a...... " + ChatColor.GREEN +  "TREAT"+ChatColor.GOLD+"!");
	    						if (reward<0.1){   //GP Piece 1
	    							ItemStack hLantern = new ItemStack(Material.JACK_O_LANTERN);
	    							ItemMeta m = hLantern.getItemMeta();
	    							m.setDisplayName(ChatColor.GOLD + "Piece 1: Great Pumpkin");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Have all five Great Pumpkin pieces?");
	    							lore.add("Right-click me for an awesome surprise");
	    							lore.add("on Halloween weekend only!!");
	    							m.setLore(lore);
	    							hLantern.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(hLantern));
	    							player.sendMessage(ChatColor.GREEN + "You've been given a " +ChatColor.GOLD+ "PUMPKIN PIECE" + ChatColor.GREEN + "!");
	    						}
	    						else if(reward<0.2){ //GP Piece 2
	    							ItemStack hLantern = new ItemStack(Material.JACK_O_LANTERN, 1, (byte) 1);
	    							ItemMeta m = hLantern.getItemMeta();
	    							m.setDisplayName(ChatColor.GOLD + "Piece 2: Great Pumpkin");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Have all five Great Pumpkin pieces?");
	    							lore.add("Right-click piece 1 for a surprise!");
	    							m.setLore(lore);
	    							hLantern.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(hLantern));
	    							player.sendMessage(ChatColor.GREEN + "You've been given a " +ChatColor.GOLD+ "PUMPKIN PIECE" + ChatColor.GREEN + "!");
	    						}
	    						else if(reward<0.3){ //GP Piece 3
	    							ItemStack hLantern = new ItemStack(Material.JACK_O_LANTERN, 1, (byte) 2);
	    							ItemMeta m = hLantern.getItemMeta();
	    							m.setDisplayName(ChatColor.GOLD + "Piece 3: Great Pumpkin");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Have all five Great Pumpkin pieces?");
	    							lore.add("Right-click piece 1 for a surprise!");
	    							m.setLore(lore);
	    							hLantern.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(hLantern));
	    							player.sendMessage(ChatColor.GREEN + "You've been given a " +ChatColor.GOLD+ "PUMPKIN PIECE" + ChatColor.GREEN + "!");
	    						}
	    						else if(reward<0.4){ // Little money
	    							int randomCash = 10 + (int)(Math.random()*100); 
	    							econ.depositPlayer(player.getDisplayName(), randomCash);
	    							player.sendMessage(ChatColor.GREEN + "Your shard wallet is a little bit heavier!");
	    						}
	    						else if(reward<0.5){ // Haunted pumpkin pie ***
	    							ItemStack hPumpkin = new ItemStack(Material.getMaterial(400), 1, (byte) 2);
	    							ItemMeta m = hPumpkin.getItemMeta();
	    							m.setDisplayName(ChatColor.GOLD + "Haunted Pumpkin Pie");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Strangely filling despite all of the");
	    							lore.add("horrifying sounds coming from it.");
	    							m.setLore(lore);
	    							hPumpkin.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(hPumpkin));
	    							player.sendMessage(ChatColor.GREEN + "You've been given some " +ChatColor.GOLD+ "HAUNTED PIE" + ChatColor.GREEN + "!");
	    						}
	    						else if(reward<0.6){ // Enderdung.
	    							ItemStack theCake = new ItemStack(Material.DRAGON_EGG, 1, (byte) 1);
	    							ItemMeta m = theCake.getItemMeta();
	    							m.setDisplayName(ChatColor.DARK_PURPLE+ "Ender Dung");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("It smells revolting. Let's touch it.");
	    							lore.add("Right-click it for fun and prizes.");
	    							lore.add("Use at your own peril.");
	    							m.setLore(lore);
	    							theCake.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(theCake));
	    							player.sendMessage(ChatColor.GREEN + "You've been given some " +ChatColor.DARK_PURPLE+ "DUNG" + ChatColor.GREEN + "!");
	    						}
	    						else if(reward<0.7){ // The grim reaper ****
	    							ItemStack gReaper = new ItemStack(Material.getMaterial(30196), 1, (byte) -24);
	    							ItemMeta m = gReaper.getItemMeta();
	    							m.setDisplayName(ChatColor.GRAY + "The Slightly Grim Reaper");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Right-click me to kill any entity");
	    							lore.add("other than other players.");
	    							m.setLore(lore);
	    							gReaper.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(gReaper));
	    							player.sendMessage(ChatColor.GREEN + "You've been given the " +ChatColor.GRAY+ "GRIM REAPER" + ChatColor.GREEN + "!");
	    						}
	    						else if(reward<0.8){ // The grim wrencher
	    							ItemStack gWrencher = new ItemStack(Material.getMaterial(30183), 1);
	    							ItemMeta m = gWrencher.getItemMeta();
	    							m.setDisplayName(ChatColor.GRAY+ "The Grim Wrencher");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Also known as an orange wrench to");
	    							lore.add("those with limited creativity.");
	    							m.setLore(lore);
	    							gWrencher.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(gWrencher));
	    							player.sendMessage(ChatColor.GREEN + "You've been given the " +ChatColor.GRAY+ "GRIM WRENCHER" + ChatColor.GREEN + "!");
	    						}
	    						else if(reward<0.9){
	    							player.setHealth(20);
	    							player.setFoodLevel(20);
	    							player.sendMessage(ChatColor.GREEN + "You've been completely healed and fed. Yum!");
	    						}
	    						else{
	    							ItemStack hBone = new ItemStack(Material.getMaterial(352), 1, (byte) 1);
	    							ItemMeta m = hBone.getItemMeta();
	    							m.setDisplayName(ChatColor.GRAY+ "The Bone Wand");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Right-click me to kill a grass block");
	    							lore.add("and plant a dead bush.");
	    							m.setLore(lore);
	    							hBone.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(hBone));
	    							player.sendMessage(ChatColor.GREEN + "You've been given the " +ChatColor.GRAY+ "BONE WAND" + ChatColor.GREEN + "!");
	    						}
	    					}
	    					else if (d < 0.9){  //30%
	    						if (reward<0.1){ //GP Piece 4
	    							ItemStack hLantern = new ItemStack(Material.JACK_O_LANTERN, 1, (byte) 3);
	    							ItemMeta m = hLantern.getItemMeta();
	    							m.setDisplayName(ChatColor.GOLD + "Piece 4: Great Pumpkin");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Have all five Great Pumpkin pieces?");
	    							lore.add("Right-click piece 1 for a surprise!");
	    							m.setLore(lore);
	    							hLantern.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(hLantern));
	    							player.sendMessage(ChatColor.GREEN + "You've been given a " +ChatColor.GOLD+ "PUMPKIN PIECE" + ChatColor.GREEN + "!");
	    						}
	    						else if (reward<0.3){
	    							ItemStack bHelmet = new ItemStack(Material.getMaterial(25026));
	    							ItemMeta m = bHelmet.getItemMeta();
	    							m.setDisplayName(ChatColor.WHITE + "Great Bone Helmet");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("The skull of chicken that grew to");
	    							lore.add("a preposterous size.");
	    							m.setLore(lore);
	    							bHelmet.setItemMeta(m);
	    							bHelmet.addEnchantment(Enchantment.WATER_WORKER, 1);
	    							player.getInventory().addItem(new ItemStack(bHelmet));
	    							player.sendMessage(ChatColor.GREEN + "You've been given the " +ChatColor.WHITE+ "GREAT BONE HELMET" + ChatColor.GREEN + "!");
	    						}
	    						else if (reward<0.5){
	    							int randomCash = 20 + (int)(Math.random()*1000); 
	    							econ.depositPlayer(player.getDisplayName(), randomCash);
	    							player.sendMessage(ChatColor.GREEN + "Your shard wallet is heavier!");
	    						}
	    						else if (reward<0.7){
	    							ItemStack hLantern = new ItemStack(Material.WOOL, 64, (byte) 1);
	    							ItemMeta m = hLantern.getItemMeta();
	    							m.setDisplayName(ChatColor.GOLD + "Pumpkin Wool");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Q:What is poorly built and orange all over?");
	    							lore.add("A:Probably your house after this.");
	    							m.setLore(lore);
	    							hLantern.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(hLantern));
	    							player.sendMessage(ChatColor.GREEN + "You've been given some " +ChatColor.GOLD+ "PUMPKIN WOOL" + ChatColor.GREEN + "!");
	    						}
	    						else{
	    							ItemStack hGlass = new ItemStack(Material.getMaterial(3129), 64, (byte) 1);
	    							ItemMeta m = hGlass.getItemMeta();
	    							m.setDisplayName(ChatColor.GOLD + "Pumpkin GLASS");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Q:What is poorly built and orange all over?");
	    							lore.add("A:Probably your house after this.");
	    							m.setLore(lore);
	    							hGlass.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(hGlass));
	    							player.sendMessage(ChatColor.GREEN + "You've been given some " +ChatColor.GOLD+ "PUMPKIN GLASS" + ChatColor.GREEN + "!");
	    						}
	    					}
	    					else{ //10%
	    						if (reward<0.1){ //GP Piece 5
	    							ItemStack hLantern = new ItemStack(Material.JACK_O_LANTERN, 1, (byte) 4);
	    							ItemMeta m = hLantern.getItemMeta();
	    							m.setDisplayName(ChatColor.GOLD + "Piece 5: Great Pumpkin");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("Have all five Great Pumpkin pieces?");
	    							lore.add("Right-click piece 1 for a surprise!");
	    							m.setLore(lore);
	    							hLantern.setItemMeta(m);
	    							player.getInventory().addItem(new ItemStack(hLantern));
	    							player.sendMessage(ChatColor.GREEN + "You've been given a " +ChatColor.GOLD+ "PUMPKIN PIECE" + ChatColor.GREEN + "!");
	    						}
	    						if (reward<0.2){
	    							double e = Math.random();
	    							player.sendMessage(ChatColor.GREEN + "You've been given a " +ChatColor.AQUA+ "SPAWN EGG" + ChatColor.GREEN + "!");
	    							if (e<0.30){
	    								ItemStack mEgg = new ItemStack(Material.MONSTER_EGG, 1, (byte) 51);
	    								player.getInventory().addItem(new ItemStack(mEgg));
	    							}
	    							else if (e<0.60){
	    								ItemStack mEgg = new ItemStack(Material.MONSTER_EGG, 1, (byte) 52);
	    								player.getInventory().addItem(new ItemStack(mEgg));
	    							}
	    							else if (e<0.80){
	    								ItemStack mEgg = new ItemStack(Material.MONSTER_EGG, 1, (byte) 58);
	    								player.getInventory().addItem(new ItemStack(mEgg));
	    							}
	    							else if (e<0.90){
	    								ItemStack mEgg = new ItemStack(Material.MONSTER_EGG, 1, (byte) 66);
	    								player.getInventory().addItem(new ItemStack(mEgg));
	    							}
	    							else if (e<0.95){
	    								ItemStack mEgg = new ItemStack(Material.MONSTER_EGG, 1, (byte) 55);
	    								player.getInventory().addItem(new ItemStack(mEgg));
	    							}
	    							else{
	    								ItemStack mEgg = new ItemStack(Material.MONSTER_EGG, 1, (byte) 120);
	    								player.getInventory().addItem(new ItemStack(mEgg));
	    							}
	    						}
	    						if (reward<0.7){
	    							ItemStack bChest = new ItemStack(Material.getMaterial(25027));
	    							ItemMeta m = bChest.getItemMeta();
	    							m.setDisplayName(ChatColor.WHITE + "Great Bone Chestpiece");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("The ribs of Sun WuKong");
	    							lore.add("the Monkey King.");
	    							m.setLore(lore);
	    							bChest.setItemMeta(m);
	    							bChest.addEnchantment(Enchantment.THORNS, 3);
	    							player.getInventory().addItem(new ItemStack(bChest));
	    							player.sendMessage(ChatColor.GREEN + "You've been given the " +ChatColor.WHITE+ "GREAT BONE CHEST" + ChatColor.GREEN + "!");
	    						}
	    						else{
	    							ItemStack bChest = new ItemStack(Material.getMaterial(25028));
	    							ItemMeta m = bChest.getItemMeta();
	    							m.setDisplayName(ChatColor.WHITE + "Great Bionic Bone Legs");
	    							ArrayList<String> lore = new ArrayList<String>();
	    							lore.add("The legs of a cocky robot");
	    							lore.add("named 'Bender'.");
	    							m.setLore(lore);
	    							bChest.setItemMeta(m);
	    							bChest.addEnchantment(Enchantment.THORNS, 3);
	    							player.getInventory().addItem(new ItemStack(bChest));
	    							player.sendMessage(ChatColor.GREEN + "You've been given the " +ChatColor.WHITE+ "GREAT BONE LEGS" + ChatColor.GREEN + "!");
	    						}
	    					}
	    				
	    				}
	    				
					
	// END =====================================================================================================================================
						
					}
	                else{
	                	player.sendMessage(ChatColor.GOLD + "You " + ChatColor.RED + "don't have enough room " +ChatColor.GOLD+ "to go Trick or Treating!");
	                	player.sendMessage(ChatColor.GOLD + "Try freeing up at least one free slot in your inventory.");
	                }
				}
				else player.sendMessage(ChatColor.RED +"You've already Trick or Treated them!");
			}
		}
		player.updateInventory();
	}
	
	private WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
	        //May disable Plugin
	        return null; // Maybe you want throw an exception instead
	    }
	    return (WorldGuardPlugin) plugin;
	}	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
