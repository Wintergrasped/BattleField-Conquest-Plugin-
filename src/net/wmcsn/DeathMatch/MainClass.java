package net.wmcsn.DeathMatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import javax.swing.text.html.HTML.Tag;

import net.milkbowl.vault.economy.Economy;
import net.wmcsn.DeathMatch.Mysql;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.avaje.ebeaninternal.server.cluster.mcast.Message;

public class MainClass extends JavaPlugin implements Listener {
	
	public List<Player> CN = new ArrayList();
	public List<Player> US = new ArrayList();
	public List<Player> INCLASS = new ArrayList();
	public List<Player> CAPTURING = new ArrayList();
	public List<String> BUGS = new ArrayList();
	public List<Player> INRCLASS = new ArrayList();
	
    private FileConfiguration Config = null;
    
    public Mysql sql = null;
    
	public int USTIC = 0;
	public int CNTIC = 0;
	public int USD;
	public int REDD;
	public int proc = 0;
	private int procCT;
	public int OC = 0;
	public int SF = 0;
	public int PSC = 0;
	
	public boolean ST = false;
	public boolean STT = false;
	public boolean CO = false;
	public boolean FA = false;
	public boolean FB = false;
	public boolean FC = false;
	public boolean FD = false;
	public boolean FE = false;
	private static boolean loaded = false;

	public static Economy econ = null;

	public String A = "None";
	public String B = "None";
	public String C = "None";
	public String D = "None";
	public String E = "None";
	
	ScoreboardManager manager = Bukkit.getScoreboardManager();
	Scoreboard board = manager.getNewScoreboard();
	Objective objective = board.registerNewObjective("test", "dummy");
	
	public MainClass MC = (MainClass)this;;
	
	
	public void onEnable() {
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName("Score");
	    org.bukkit.scoreboard.Score score = objective.getScore(ChatColor.GREEN + "US:"); //Get a fake offline player
	    score.setScore(USTIC);
	    
	    org.bukkit.scoreboard.Score score2 = objective.getScore(ChatColor.GREEN + "CN:"); //Get a fake offline player
	    score.setScore(CNTIC);
	    if (Config == null) {
            Config = this.getConfig();
            this.getConfig().options().copyDefaults(true);
            this.saveConfig();
            Config = this.getConfig();
    } else {
            Config = this.getConfig();
    }	
		Bukkit.getServerId();
		
	    
		if (this.getConfig() == null) {
			Bukkit.getPluginManager().disablePlugin(this);
		}else{
			
		}
		Bukkit.getLogger().log(Level.INFO, "Checking License");
		
		String L = this.getConfig().getString("ServerInfo.License");
		if (L == null) {
			this.getConfig().set("ServerInfo.License", "YOUR LICENSE KEY HERE!");
		}
		ST = false;
		STT = false;
		setupEconomy();
		FlagOperations();

		Bukkit.getPluginManager().registerEvents(this, this);

		ReSetPointBlock();
		Score();
		RTT();
		Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD
				+ "Reload COmplete Please Re-Log If You Can See This Message!");
		RoundTimer();
		HugerTimer();
	
		
		
		//Note At The SPigot Stafff Looking at this.
		//Yes it is sloppy altough it does work.
		
		
		
		
		//sql = new Mysql(this, "212.83.161.167", 3306, "winterec_gmod", "winterec_admin", "APASSWORDHERE");
		//sql.openConnection();
		
		USTIC = this.getConfig().getInt("GameSettings.Tickets");
		CNTIC = this.getConfig().getInt("GameSettings.Tickets");
		
		
		if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
			Bukkit.getLogger().log(Level.SEVERE, "[Battle Feild] Vault Not Found! Battle Feild Disabling!");
			Bukkit.getPluginManager().disablePlugin(this);
		}else{
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Vault Found! USing Vault For Economy!");
		}
		
		if (Bukkit.getPluginManager().getPlugin("CrackShot") == null) {
			Bukkit.getLogger().log(Level.SEVERE, "[Battle Feild] CrackShot Not Found! Battle Feild Disabling!");
			Bukkit.getPluginManager().disablePlugin(this);
		}else{
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] CrackShot Found! Using Crackshot For Weapons");
		}
		
		if (Bukkit.getPluginManager().getPlugin("Essentials") == null) {
			Bukkit.getLogger().log(Level.SEVERE, "[Battle Feild] Essentials Not Found! Battle Feild Disabling!");
			Bukkit.getPluginManager().disablePlugin(this);
		}else{
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Essentials Found! Using Essentials for Code Optimizations!");
		}
		
		
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule keepInventory true");
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.A.X")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.A.X", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.A.Y")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.A.Y", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.A.Z")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.A.Z", 0);
		}
		
		
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.B.X")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.B.X", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.B.Y")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.B.Y", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.B.Z")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.B.Z", 0);
		}
		
		
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.C.X")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.C.X", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.C.Y")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.C.Y", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.C.Z")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.C.Z", 0);
		}
		
		
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.D.X")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.D.X", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.D.Y")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.D.Y", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.D.Z")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.D.Z", 0);
		}
		
		
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.E.X")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.E.X", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.E.Y")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.E.Y", 0);
		}
		
		if (this.getConfig().contains("GameSettings.ObjectiveSettings.E.Z")) {
			
		}else{
			this.getConfig().set("GameSettings.ObjectiveSettings.E.Z", 0);
		}
		
		if (this.getConfig().contains("GameSettings.TeamSettings.US.SpawnBorderX")) {
			
		}else{
			this.getConfig().set("GameSettings.TeamSettings.US.SpawnBorderX", 0);
		}
		
		if (this.getConfig().contains("GameSettings.TeamSettings.CN.SpawnBorderX")) {
			
		}else{
			this.getConfig().set("GameSettings.TeamSettings.CN.SpawnBorderX", 0);
		}
	}
	
	public void loadConfiguration() {//to load config file
        Config = this.getConfig();//get the configuration
        saveConfig();//write them to file
    }

    
	public String getpTeam(Player p) {
		String Team = "G";

		if (CN.contains(p)) {
			Team = "CN";
			return Team;
		}
		if (US.contains(p)) {
			Team = "US";
			return Team;
		}

		return Team;
	}

	public boolean assignTeam(Player p) {
		if (US.size() > CN.size()) {
			CN.add(p);
			p.sendMessage(ChatColor.RED + "You Have Been Added To The CN Team!");

			return true;
		}

		if (CN.size() > US.size()) {
			US.add(p);
			p.sendMessage(ChatColor.AQUA
					+ "You Have Been Added To The US Team!");
			return true;
		}

		if (CN.size() == US.size()) {
			US.add(p);
			p.sendMessage(ChatColor.AQUA
					+ "You Have Been Added To The US Team!");
			return true;
		}
		return false;
	}

	public void checkKills() {

		if (USTIC == 0) {
			win("CN");
		}
		if (CNTIC == 0) {
			win("US");
		}

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Scores();
		Player p = e.getPlayer();
		InfoTimer(p);
		try {

			if (p == null) {

			} else {
				assignTeam(p);
			}
		} catch (NullPointerException x) {
			x.printStackTrace();
		}

		if (getpTeam(p) == "CN") {
			p.teleport(new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnX"), this.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnY"), this.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnZ")));
		}

		if (getpTeam(p) == "US") {
			p.teleport(new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.TeamSettings.US.SpawnX"), this.getConfig().getInt("GameSettings.TeamSettings.US.SpawnY"), this.getConfig().getInt("GameSettings.TeamSettings.US.SpawnZ")));
		}

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gm 2 " + p.getName());

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ci " + p.getName());

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
				"xp set " + p.getName() + " 0");

		p.setExp(0);

		GiveColor(p, getpTeam(p));

		if (p.getName().equals("Wintergrasped")) {

			AnWinter();

		}
		
		if (p.getName().equalsIgnoreCase("DragonMasterFu")) {

			AnTeam(p);

		}
		
		if (p.getName().equalsIgnoreCase("DarkerBlaze")) {

			AnTeam(p);

		}
		
		if (p.getName().equalsIgnoreCase("Kitty69lol")) {

			AnTeam(p);

		}
		
		if (p.getName().equalsIgnoreCase("BunkerM4")) {

			AnTeam(p);

		}
		
		if (p.getName().equalsIgnoreCase("Mutiplyyou")) {

			AnTeam(p);

		}
		
		if (p.getName().equalsIgnoreCase("1redstonemaster")) {

			AnTeam(p);

		}
		
		if (this.getConfig().get("PlayerData." + p.getName()) !=null) {
		
			this.getConfig().set("PlayerData."+e.getPlayer().getName()+".BattlesPlayed", this.getConfig().getInt("PlayerData."+e.getPlayer().getName()+".BattlesPlayed")+1);
		
			this.saveConfig();
		}else{
			CreatPlayerConfig(p);
		}
		
		for (Player ps : Bukkit.getOnlinePlayers()) {
			SQLUpdate(ps);
		}
		
		}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Scores();
		Player p = e.getEntity().getPlayer();
		Player k = e.getEntity().getPlayer().getKiller();

		String Team = getpTeam(e.getEntity().getPlayer());
		String KTeam = getpTeam(e.getEntity().getPlayer().getKiller());

		if (k instanceof Player) {

			if (Team.equals("US")) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD
						+ "" + p.getKiller().getName() + ChatColor.RESET + ""
						+ ChatColor.AQUA + " Killed " + p.getName());
			}
			if (Team.equals("CN")) {
				Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD
						+ "" + p.getKiller().getName() + ChatColor.RESET + ""
						+ ChatColor.RED + " Killed " + p.getName());
			}

			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
					"ci " + p.getName());

			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
					"heal " + k.getName());

			k.setHealth(20);
			k.setFoodLevel(20);
			
			for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
		}
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ci " + p.getName());
			
			p.getInventory().clear();

			checkKills();
			int KKills = this.getConfig().getInt("PlayerData."+k.getName()+".Kills");
			int PDeaths = this.getConfig().getInt("PlayerData."+p.getName()+".Deaths");
			
			
			this.getConfig().set("PlayerData."+k.getName()+".Kills", KKills+1);
			this.getConfig().set("PlayerData."+p.getName()+".Deaths", PDeaths+1);
			this.saveConfig();
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {

		Player p = e.getPlayer();

		if (CN.contains(p)) {
			CN.remove(p);
		}

		if (US.contains(p)) {
			US.remove(p);
		}
		
		StopINF();
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {

		if (e.getEntity() instanceof Player) {
			if (e.getDamager() instanceof Player) {

				Player p = (Player) e.getEntity();
				Player k = (Player) e.getDamager();
				String Team = getpTeam(p);
				String KTeam = getpTeam(k);
				if (KTeam == Team) {
					e.setCancelled(true);
				}
				if (Team == KTeam) {
					e.setCancelled(true);
				}
			}
		}
	}

	public void win(String Team) {

		if (Team.equals("CN")) {

			Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
					+ "CN Team Has Won The Game!");
			for (Player p : CN) {
				double t = 1;
				double k = p.getLevel();
				double m = k * 0.25;
				t = t * m;
				StopTimer("CN");
				CelebrationTimer(p, this);
				for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
				}
			}
		}

		if (Team.equals("US")) {

			Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD
					+ "US Team Has Won The Game!");

			for (Player p : US) {
				double t = 1;
				double k = p.getLevel();
				double m = k * 0.25;
				t = t * m;
				StopTimer("US");
				CelebrationTimer(p, this);
				for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
				}
			}

			StopTimer("US");
		}

	}

	public void CelebrationTimer(final Player p, final MainClass M) {
		PSC = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			public void run() {
				if (M.getConfig().getBoolean("GameSettings.EndingFireWorks")) {
				Celebrate(p);
			}else{
				Bukkit.getScheduler().cancelTask(PSC);
			}
			}

		}, 0L, 4L);

	}

	public void Celebrate(Player p) {

		Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(),
				EntityType.FIREWORK);
		FireworkMeta fwm = fw.getFireworkMeta();

		Random r = new Random();

		int rt = r.nextInt(4) + 1;
		Type type = Type.BALL;
		if (rt == 1)
			type = Type.BALL;
		if (rt == 2)
			type = Type.BALL_LARGE;
		if (rt == 3)
			type = Type.BURST;
		if (rt == 4)
			type = Type.CREEPER;
		if (rt == 5)
			type = Type.STAR;

		int r1i = r.nextInt(17) + 1;
		int r2i = r.nextInt(17) + 1;
		Color c1 = getColor(r1i);
		Color c2 = getColor(r2i);

		FireworkEffect effect = FireworkEffect.builder()
				.flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type)
				.trail(r.nextBoolean()).build();

		fwm.addEffect(effect);

		int rp = r.nextInt(2) + 1;
		fwm.setPower(rp);

		fw.setFireworkMeta(fwm);

	}

	private Color getColor(int i) {
		Color c = null;
		if (i == 1) {
			c = Color.AQUA;
		}
		if (i == 2) {
			c = Color.BLACK;
		}
		if (i == 3) {
			c = Color.BLUE;
		}
		if (i == 4) {
			c = Color.FUCHSIA;
		}
		if (i == 5) {
			c = Color.GRAY;
		}
		if (i == 6) {
			c = Color.GREEN;
		}
		if (i == 7) {
			c = Color.LIME;
		}
		if (i == 8) {
			c = Color.MAROON;
		}
		if (i == 9) {
			c = Color.NAVY;
		}
		if (i == 10) {
			c = Color.OLIVE;
		}
		if (i == 11) {
			c = Color.ORANGE;
		}
		if (i == 12) {
			c = Color.PURPLE;
		}
		if (i == 13) {
			c = Color.RED;
		}
		if (i == 14) {
			c = Color.SILVER;
		}
		if (i == 15) {
			c = Color.TEAL;
		}
		if (i == 16) {
			c = Color.WHITE;
		}
		if (i == 17) {
			c = Color.YELLOW;
		}

		return c;
	}

	public void StopTimer(final String t) {

		Bukkit.broadcastMessage(ChatColor.AQUA + "" + ChatColor.BOLD
				+ "Server Restarting In 30 Seconds");

		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.kickPlayer("The " + t + " Team Won The Game");
				}
				Bukkit.shutdown();
			}

		}, 600L);

	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		Player p = e.getPlayer();

		
		if (getpTeam(p) == "CN") {
			CNTIC = CNTIC - 1;
			p.teleport(new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnX"), this.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnY"), this.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnZ")));
		}

		if (getpTeam(p) == "US") {
			USTIC = USTIC - 1;
			p.teleport(new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.TeamSettings.US.SpawnX"), this.getConfig().getInt("GameSettings.TeamSettings.US.SpawnY"), this.getConfig().getInt("GameSettings.TeamSettings.US.SpawnZ")));
		}

		ReSpawnTimer(p, this);
		INCLASS.remove(p);
		p.sendMessage(ChatColor.RED + "To get in a class Do /class <CLASSNAME>");
		p.sendMessage(ChatColor.RED
				+ "To buy in a higher teir class Do /class <CLASS NAME> <TEIR NUMBER>");
		p.sendMessage(ChatColor.RED + "Avilable Classes: " + ChatColor.GREEN
				+ " Assult, Medic, Engineer, Sniper");
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {

		
		
		Location A = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.A.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.A.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.A.Z"));
		Location D = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.B.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.B.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.B.Z"));
		Location C = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.C.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.C.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.C.Z"));
		Location B = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.D.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.D.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.D.Z"));
		Location E = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.E.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.E.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.E.Z"));
		
		Player p = e.getPlayer();

		if (p.getLocation().distance(A) <= 5) {
			if (FA == false) {
				SCapturePoint(p, "A");
				FA = true;

				Ftimer("A");
			}
		}

		if (p.getLocation().distance(A) == 6) {
			if (FA == false) {
				Bukkit.getScheduler().cancelTask(procCT);
				p.sendMessage(ChatColor.GREEN + "Stoped Capturing A");
				Ftimer("A");
			}
		}
		// =============
		// END OF POINT=
		// =============

		if (p.getLocation().distance(B) <= 5) {
			if (FB == false) {
				SCapturePoint(p, "B");
				FB = true;

				Ftimer("B");
			}
		}

		if (p.getLocation().distance(B) == 6) {
			if (FB == false) {
				Bukkit.getScheduler().cancelTask(procCT);
				p.sendMessage(ChatColor.GREEN + "Stoped Capturing B");
				Ftimer("B");
			}
		}
		// =============
		// END OF POINT=
		// =============

		if (p.getLocation().distance(C) <= 5) {
			if (FC == false) {
				SCapturePoint(p, "C");
				FC = true;

				Ftimer("C");
			}
		}

		if (p.getLocation().distance(C) == 6) {
			if (FC == false) {
				Bukkit.getScheduler().cancelTask(procCT);
				p.sendMessage(ChatColor.GREEN + "Stoped Capturing C");
				Ftimer("C");
			}
		}
		// =============
		// END OF POINT=
		// =============

		if (p.getLocation().distance(D) <= 5) {
			if (FD == false) {
				SCapturePoint(p, "D");
				FD = true;

				Ftimer("D");
			}
		}

		if (p.getLocation().distance(D) == 6) {
			if (FD == false) {
				Bukkit.getScheduler().cancelTask(procCT);
				p.sendMessage(ChatColor.GREEN + "Stoped Capturing D");
				Ftimer("D");
			}
		}
		// =============
		// END OF POINT=
		// =============

		if (p.getLocation().distance(E) <= 5) {
			if (FE == false) {
				SCapturePoint(p, "E");
				FE = true;

				Ftimer("E");
			}
		}

		if (p.getLocation().distance(E) == 6) {
			if (FE == false) {
				Bukkit.getScheduler().cancelTask(procCT);
				p.sendMessage(ChatColor.GREEN + "Stoped Capturing E");
				Ftimer("E");
			}
		}
		// =============
		// END OF POINT=
		// =============

		if (getpTeam(p).equals("US")) {
			if (p.getLocation().getBlockX() >= this.getConfig().getInt("GameSettings.TeamSettings.US.SpawnBorderX")) {
					p.damage(25);
					ResetSTT();
			}
		}

		if (getpTeam(p).equals("CN")) {
			if (p.getLocation().getBlockX() <= this.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnBorderX")) {
					p.damage(25);
					ResetST();
			}
		}
	}

	public void ResetST() {

		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {
				ST = false;
			}

		}, 20L);
	}

	public void ResetSTT() {

		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {
				STT = false;
			}

		}, 20L);
	}

	public void ReSpawnTimer(final Player p, final MainClass M) {

		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {
				p.getInventory().clear();
				if (getpTeam(p) == "CN") {
					p.teleport(new Location(Bukkit.getWorld("world"), M.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnX"), M.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnY"), M.getConfig().getInt("GameSettings.TeamSettings.CN.SpawnZ")));
				}

				if (getpTeam(p) == "US") {
					p.teleport(new Location(Bukkit.getWorld("world"), M.getConfig().getInt("GameSettings.TeamSettings.US.SpawnX"), M.getConfig().getInt("GameSettings.TeamSettings.US.SpawnY"), M.getConfig().getInt("GameSettings.TeamSettings.US.SpawnZ")));
				}
				GiveColor(p, getpTeam(p));
			}

		}, 5L);

	}

	public void GiveGun(Player p, String G) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
				"crackshot give " + p.getName() + " " + G);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("bug")) {
			
			
			sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Thank You! Your Bug Has Been Reported");
			String ss = Arrays.toString(args);
			BUGS.add(ss);
			String sb = Arrays.toString(BUGS.toArray());		
			
			if (BUGS.equals(null)) {
				
				return false;
			}else{
			this.getConfig().set("Bugs.Reported", BUGS);
			this.saveConfig();
			}
			}
		
		Player p = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("bf")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Proper Syntax /bf <ARGS>");
				sender.sendMessage(ChatColor.RED + "Avalible Commands /bf help");
				sender.sendMessage(ChatColor.RED + "Avalible Commands /bf log");
				return false;
			}

			if (args[0].equals(null)) {
				sender.sendMessage(ChatColor.RED + "Proper Syntax /bf <ARGS>");
				sender.sendMessage(ChatColor.RED + "Avalible Commands /bf help");
				sender.sendMessage(ChatColor.RED + "Avalible Commands /bf log");
				return false;
			}
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD
						+ "How To Play BF By. Wintergrasped");
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD
						+ "<><><><><><><><>Main Objectivs:<><><><><><><><>");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD
						+ " - Hold The Most Objectives");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " - "
						+ ChatColor.AQUA
						+ "Kill Enemies To Deplete Enemy Tickets");
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + " ");
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD
						+ "<><><><><><><><>How To Play:<><><><><><><><>");
				sender.sendMessage(ChatColor.RED
						+ ""
						+ ChatColor.BOLD
						+ " -"
						+ ChatColor.AQUA
						+ " When You Spawn U spawn ain a colored Tower CN = CN and US = US");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " - "
						+ ChatColor.AQUA + "Choose A Class By Doing /class");
				sender.sendMessage(ChatColor.RED
						+ ""
						+ ChatColor.BOLD
						+ " - "
						+ ChatColor.AQUA
						+ "Guns Are Left Click Scope and Right Click Fire (Some Guns Have Special Conditions)");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " - "
						+ ChatColor.AQUA
						+ "If Your CN and Cross the BLUE Wool Line you Die");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " - "
						+ ChatColor.AQUA
						+ "If Your US and Cross the RED Wool Line you Die");
				sender.sendMessage(ChatColor.RED
						+ ""
						+ ChatColor.BOLD
						+ " - "
						+ ChatColor.AQUA
						+ "To Capture an objective Stand Near the Objective for 6 Seconds");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " - "
						+ ChatColor.AQUA + "Say !score to get a Scoreboard");
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + " ");
				sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD
						+ "<><><><><><><><>Rewards:<><><><><><><><>");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " - "
						+ ChatColor.AQUA
						+ "You Get 0.3 Tokens Every Point You Capture.");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " - "
						+ ChatColor.AQUA
						+ "You get 0.3 Tokens Every Kill You get.");
				sender.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + " - "
						+ ChatColor.AQUA + "Your XP Bar is you Kill Counter");
			}
			
			if (args[0].equals("log")) {
				if (args[1].equals(null)) {
					sender.sendMessage(ChatColor.RED + "Proper Syntax /bf log <Name>");
				}else{
					
			    	int BP = this.getConfig().getInt("PlayerData."+p.getName()+".BattlesPlayed");
			    	int OBC = this.getConfig().getInt("PlayerData."+p.getName()+".ObjectivesCapture");
			    	int PD = this.getConfig().getInt("PlayerData."+p.getName()+".Deaths");
			    	int PK = this.getConfig().getInt("PlayerData."+p.getName()+".Kills");
			    	int BA = this.getConfig().getInt("PlayerData."+p.getName()+".Bandaged");
					
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD +"Battle Log BETA! Note: This information may be VERY in-acurate");
					sender.sendMessage("");
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD +"Battle Log Info For : " + ChatColor.RESET + "" + ChatColor.AQUA + args[1]);
					sender.sendMessage("");
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD +"Battles Played: " + ChatColor.RESET + "" + ChatColor.AQUA + this.getConfig().getInt("PlayerData."+args[1]+".BattlesPlayed"));
					sender.sendMessage("");
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD +"Objectives Captured: " + ChatColor.RESET + "" + ChatColor.AQUA + this.getConfig().getInt("PlayerData."+args[1]+".ObjectiveCapture"));
					sender.sendMessage("");
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD +"Kills: " + ChatColor.RESET + "" + ChatColor.AQUA + this.getConfig().getInt("PlayerData."+args[1]+".Kills"));
					sender.sendMessage("");
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD +"Deaths: " + ChatColor.RESET + "" + ChatColor.AQUA + this.getConfig().getInt("PlayerData."+args[1]+".Deaths"));
					sender.sendMessage("");
					sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD +"Times Bandaged: " + ChatColor.RESET + "" + ChatColor.AQUA + this.getConfig().getInt("PlayerData."+args[1]+".Bandaged"));
					


				}
				
			}
		}

		if (cmd.getName().equalsIgnoreCase("class")) {

			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Sytanx Error!");
				sender.sendMessage(ChatColor.RED
						+ "Prop Usage /class <CLASSNAME>");
				sender.sendMessage(ChatColor.RED
						+ "For Addition Help Do /class help");
				return false;
			}

			if (args[0].equalsIgnoreCase("Help")) {

				sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD
						+ "Help For /class");
				sender.sendMessage(ChatColor.GREEN
						+ "/class <CLASSNAME>"
						+ ChatColor.AQUA
						+ " - "
						+ ChatColor.BOLD
						+ " This chooses the base level of the Specified class.");
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GREEN
						+ "/class <CLASSNAME> <TIER>"
						+ ChatColor.AQUA
						+ " - "
						+ ChatColor.BOLD
						+ "This chooses the specified Teir of the specified class. To choose high tiers you have to have money");

			}
			}

		
		// ============
		// BEGIN CLASS
		// ============
		if (args[0].equalsIgnoreCase("random")) {
			if (args.length == 1) {
				if (INCLASS.contains(p)) {

				} else {
					if (INRCLASS.contains(p)) {
						
						
					}else{
					INCLASS.add(p);
					INRCLASS.add(p);
					GiveRandomC(p);
					GiveRandomC(p);
					GiveRandomC(p);
					GiveRandomC(p);
					GiveRandomC(p);
					GiveRandomC(p);
					GiveRandomC(p);
					GiveRandomC(p);
					GiveRandomC(p);

				}
			}
			
		}
		}
			// ============
			// BEGIN MEDIC
			// ============
			if (args[0].equalsIgnoreCase("medic")) {
				if (INCLASS.contains(p)) {

				} else {
					INCLASS.add(p);
					for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Medic.T1.Guns")) {
						GiveGun(p, GNAME);
					}
					giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T1.Bandages"));
				}
			} else {

				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("2")) {
						if (INCLASS.contains(p)) {
						} else {
							if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T2.Cost"))) {
								econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T2.Cost"));
								for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Medic.T2.Guns")) {
									GiveGun(p, GNAME);
								}
								giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T2.Bandages"));
							} else {
								p.sendMessage(ChatColor.RED
										+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Medic.T2.Cost"));

							}
						}
					}
					if (args[1].equalsIgnoreCase("3")) {
						if (INCLASS.contains(p)) {
						} else {
							if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T3.Cost"))) {
								econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T3.Cost"));
								INCLASS.add(p);
								for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Medic.T3.Guns")) {
									GiveGun(p, GNAME);
								}
								giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T3.Bandages"));
							} else {
								p.sendMessage(ChatColor.RED
										+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Medic.T3.Cost"));

							}
						}
					}
					if (args[1].equalsIgnoreCase("4")) {
						if (INCLASS.contains(p)) {
						} else {
							if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T4.Cost"))) {
								econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T4.Cost"));
								INCLASS.add(p);
								for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Medic.T4.Guns")) {
									GiveGun(p, GNAME);
								}
								giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Medic.T4.Bandages"));
							} else {
								p.sendMessage(ChatColor.RED
										+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Medic.T4.Cost"));

							}
						}
					}
				}
			}

			// ============
			// BEGIN CLASS
			// ============
			if (args[0].equalsIgnoreCase("assult")) {
				if (args.length == 1) {
					if (INCLASS.contains(p)) {

					} else {
						INCLASS.add(p);
						for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Assult.T1.Guns")) {
							GiveGun(p, GNAME);
						}
						giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T1.Bandages"));
					}
				} else {

					if (args.length == 2) {
						if (args[1].equalsIgnoreCase("2")) {
							if (INCLASS.contains(p)) {
							} else {
								if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T2.Cost"))) {
									econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T2.Cost"));
									for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.Assult.T2.Guns")) {
										GiveGun(p, GNAME);
									}
									giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T2.Bandages"));
								} else {
									p.sendMessage(ChatColor.RED
											+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Assult.T2.Cost"));

								}
							}
						}
						if (args[1].equalsIgnoreCase("3")) {
							if (INCLASS.contains(p)) {
							} else {
								if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T3.Cost"))) {
									econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T3.Cost"));
									INCLASS.add(p);
									for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Assult.T3.Guns")) {
										GiveGun(p, GNAME);
									}
									giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T3.Bandages"));
								} else {
									p.sendMessage(ChatColor.RED
											+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Assult.T3.Cost"));

								}
							}
						}
						if (args[1].equalsIgnoreCase("4")) {
							if (INCLASS.contains(p)) {
							} else {
								if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T4.Cost"))) {
									econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T4.Cost"));
									INCLASS.add(p);
									for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Assult.T4.Guns")) {
										GiveGun(p, GNAME);
									}
									giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Assult.T4.Bandages"));
								} else {
									p.sendMessage(ChatColor.RED
											+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Assult.T4.Cost"));

								}
							}
						}
					}
				}
			}
			// ============
			// BEGIN CLASS
			// ============
			if (args[0].equalsIgnoreCase("engineer")) {
				if (args.length == 1) {
					if (INCLASS.contains(p)) {

					} else {
						INCLASS.add(p);
						for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Engineer.T1.Guns")) {
							GiveGun(p, GNAME);
						}
						giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T1.Bandages"));
					}
				} else {

					if (args.length == 2) {
						if (args[1].equalsIgnoreCase("2")) {
							if (INCLASS.contains(p)) {
							} else {
								if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T2.Cost"))) {
									econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T2.Cost"));
									for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Engineer.T2.Guns")) {
										GiveGun(p, GNAME);
									}
									giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T2.Bandages"));
								} else {
									p.sendMessage(ChatColor.RED
											+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T2.Cost"));

								}
							}
						}
						if (args[1].equalsIgnoreCase("3")) {
							if (INCLASS.contains(p)) {
							} else {
								if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T3.Cost"))) {
									econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T3.Cost"));
									INCLASS.add(p);
									for (String GNAME : this.getConfig().getStringList("GameSettings.ClassSettings.Engineer.T3.Guns")) {
										GiveGun(p, GNAME);
									}
									giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T3.Bandages"));
								} else {
									p.sendMessage(ChatColor.RED
											+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T3.Cost"));

								}
							}
						}
						if (args[1].equalsIgnoreCase("4")) {
							if (INCLASS.contains(p)) {
							} else {
								if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T4.Cost"))) {
									econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T4.Cost"));
									INCLASS.add(p);
									for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.Engineer.T4.Guns")) {
										GiveGun(p, GNAME);
									}
									giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T4.Bandages"));
								} else {
									p.sendMessage(ChatColor.RED
											+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Engineer.T4.Cost"));

								}
							}
						}
					}
				}
			}

			// ============
			// BEGIN CLASS
			// ============
			if (args[0].equalsIgnoreCase("VIP")) {
				if (p.hasPermission("bf.vip")) {
					if (args.length == 1) {
						if (INCLASS.contains(p)) {

						} else {
							INCLASS.add(p);
							for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.VIP.T1.Guns")) {
								GiveGun(p, GNAME);
							}
							giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T1.Bandages"));
						}
					} else {

						if (args.length == 2) {
							if (args[1].equalsIgnoreCase("2")) {
								if (INCLASS.contains(p)) {
								} else {
									if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T2.Cost"))) {
										econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T2.Cost"));
										for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.VIP.T2.Guns")) {
											GiveGun(p, GNAME);
										}
										giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T2.Bandages"));
									} else {
										p.sendMessage(ChatColor.RED
												+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.VIP.T2.Cost"));

									}
								}
							}
							if (args[1].equalsIgnoreCase("3")) {
								if (INCLASS.contains(p)) {
								} else {
									if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T3.Cost"))) {
										econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T3.Cost"));
										INCLASS.add(p);
										for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.VIP.T3.Guns")) {
											GiveGun(p, GNAME);
										}
										giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T3.Bandages"));
									} else {
										p.sendMessage(ChatColor.RED
												+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.VIP.T3.Cost"));

									}
								}
							}
							if (args[1].equalsIgnoreCase("4")) {
								if (INCLASS.contains(p)) {
								} else {
									if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T4.Cost"))) {
										econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T4.Cost"));
										INCLASS.add(p);
										for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.VIP.T4.Guns")) {
											GiveGun(p, GNAME);
										}
										giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.VIP.T4.Bandages"));
									} else {
										p.sendMessage(ChatColor.RED
												+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.VIP.T4.Cost"));

									}
								}
							}
						}
					}
				}
			}
			// ============
			// BEGIN CLASS
			// ============
			if (args[0].equalsIgnoreCase("MVP")) {
				if (p.hasPermission("bf.mvp")) {
					if (args.length == 1) {
						if (INCLASS.contains(p)) {

						} else {
							INCLASS.add(p);
							for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.MVP.T1.Guns")) {
								GiveGun(p, GNAME);
							}
							giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T1.Bandages"));
						}
					} else {

						if (args.length == 2) {
							if (args[1].equalsIgnoreCase("2")) {
								if (INCLASS.contains(p)) {
								} else {
									if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T2.Cost"))) {
										econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T2.Cost"));
										for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.MVP.T2.Guns")) {
											GiveGun(p, GNAME);
										}
										giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T2.Bandages"));
									} else {
										p.sendMessage(ChatColor.RED
												+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.MVP.T2.Cost"));

									}
								}
							}
							if (args[1].equalsIgnoreCase("3")) {
								if (INCLASS.contains(p)) {
								} else {
									if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T3.Cost"))) {
										econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T3.Cost"));
										INCLASS.add(p);
										for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.MVP.T3.Guns")) {
											GiveGun(p, GNAME);
										}
										giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T3.Bandages"));
									} else {
										p.sendMessage(ChatColor.RED
												+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.MVP.T3.Cost"));

									}
								}
							}
							if (args[1].equalsIgnoreCase("4")) {
								if (INCLASS.contains(p)) {
								} else {
									if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T4.Cost"))) {
										econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T4.Cost"));
										INCLASS.add(p);
										for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.MVP.T4.Guns")) {
											GiveGun(p, GNAME);
										}
										giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.MVP.T4.Bandages"));
									} else {
										p.sendMessage(ChatColor.RED
												+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.MVP.T4.Cost"));

									}
								}
							}
						}
					}
				}
			}

			// ============
			// BEGIN CLASS
			// ============
			if (args[0].equalsIgnoreCase("Sniper")) {
				if (args.length == 1) {
					if (INCLASS.contains(p)) {

					} else {
						INCLASS.add(p);
						for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.Sniper.T1.Guns")) {
							GiveGun(p, GNAME);
						}
						giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T1.Bandages"));
					}
				} else {

					if (args.length == 2) {
						if (args[1].equalsIgnoreCase("2")) {
							if (INCLASS.contains(p)) {
							} else {
								if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T2.Cost"))) {
									econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T2.Cost"));
									for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.Sniper.T2.Guns")) {
										GiveGun(p, GNAME);
									}
									giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T2.Bandages"));
									
								} else {
									p.sendMessage(ChatColor.RED
											+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T2.Cost"));

								}
							}
						}
						if (args[1].equalsIgnoreCase("3")) {
							if (INCLASS.contains(p)) {
							} else {
								if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T3.Cost"))) {
									econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T3.Cost"));
									INCLASS.add(p);
									for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.Sniper.T3.Guns")) {
										GiveGun(p, GNAME);
									}
									giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T3.Bandages"));
								} else {
									p.sendMessage(ChatColor.RED
											+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T3.Cost"));

								}
							}
						}
						if (args[1].equalsIgnoreCase("4")) {
							if (INCLASS.contains(p)) {
							} else {
								if (econ.has(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T4.Cost"))) {
									econ.withdrawPlayer(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T4.Cost"));
									INCLASS.add(p);
									for (String GNAME : this.getConfig().getStringList( "GameSettings.ClassSettings.Sniper.T4.Guns")) {
										GiveGun(p, GNAME);
									}
									giveBandage(p, this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T4.Bandages"));
								} else {
									p.sendMessage(ChatColor.RED
											+ "You dont have enough Money! That package requires $"+this.getConfig().getInt("GameSettings.ClassSettings.Sniper.T4.Cost"));

								}
							}
						}
					}
				}
		}
			
			// ============
			// BEGIN CLASS
			// ============
			if (args[0].equalsIgnoreCase("Token")) {
				if (args.length == 1) {
					if (INCLASS.contains(p)) {

					} else {
						if (sql.getTokens(p.getName()) >= 100) {
							int tokens = sql.getTokens(p.getName());
							sql.setTokens(p.getName(), tokens-100);
						INCLASS.add(p);
						GiveGun(p, "Barret-50");
						GiveGun(p, "m4a97");
						GiveGun(p, "DAQ-12");
						GiveGun(p, "flashbang 64");
						GiveGun(p, "grenade 64");
						GiveGun(p, "ApalmGrendae 64");
						GiveGun(p, "ApalmLauncher");
						giveBandage(p, 1);
					}else{
						
						sender.sendMessage(ChatColor.RED + "This Requires 100 Tokens");
						
					}
						if (args[1].equalsIgnoreCase("2")) {
							if (sql.getTokens(p.getName()) >= 200) {
							int tokens = sql.getTokens(p.getName());
							sql.setTokens(p.getName(), tokens-200);
						INCLASS.add(p);
						GiveGun(p, "ApalmLauncher");
						GiveGun(p, "p90");
						GiveGun(p, "ak-47");
						GiveGun(p, "Barret-50");
						GiveGun(p, "m4a97");
						GiveGun(p, "DAQ-12");
						GiveGun(p, "flashbang 64");
						GiveGun(p, "grenade 64");
						GiveGun(p, "ApalmGrendae 64");
						GiveGun(p, "ApalmCluster 64");
						GiveGun(p, "SandyCluster 64");
						giveBandage(p, 5);
						}else{
							sender.sendMessage(ChatColor.RED + "This Requires 200 Tokens");
						}
						}else{
							if (sql.getTokens(p.getName()) >= 100) {
								int tokens = sql.getTokens(p.getName());
								sql.setTokens(p.getName(), tokens-100);
							INCLASS.add(p);
							GiveGun(p, "Barret-50");
							GiveGun(p, "m4a97");
							GiveGun(p, "DAQ-12");
							GiveGun(p, "flashbang 64");
							GiveGun(p, "grenade 64");
							GiveGun(p, "ApalmGrendae 64");
							GiveGun(p, "ApalmLauncher");
							giveBandage(p, 1);
						}else{
							sender.sendMessage(ChatColor.RED + "This Requires 100 Tokens");
						}
						}
					}
					}
			}
		return false;
	}

	public void GiveRandomC(Player p) {
		Random rand = new Random();
        int rint = rand.nextInt(40);

		Random rands = new Random();
        int rint2 = rand.nextInt(64);
        
        if (rint == 1) {
        GiveGun(p, "m4a97");
        }
        
        if (rint == 2) {
        GiveGun(p, "ak-47");
        }
        
        if (rint == 13) {
        GiveGun(p, "apalmlauncher");
        }
        
        if (rint == 3) {
        GiveGun(p, "Grendae "+rint2);
        }
        
        if (rint == 5) {
        GiveGun(p, "ApalmGrenade "+rint2);
        }
        
        if (rint == 4) {
        GiveGun(p, "sandycluster "+rint2);
        }
        
        if (rint == 6) {
        GiveGun(p, "p90");
        }
        
        if (rint == 7) {
        GiveGun(p, "Barret-50");
        }
        
        if (rint == 8) {
        giveBandage(p, rint2);
        }
        
        if (rint == 9) {
        giveBandage(p, rint2);
        }
        
        if (rint == 10) {
        giveBandage(p, rint2);
        }
        
        if (rint == 11) {
        GiveGun(p, "m4a97");
        }
        
        if (rint == 12) {
        GiveGun(p, "ak-47");
        }
        
        if (rint == 13) {
        GiveGun(p, "m4a97");
        }
        
        if (rint == 14) {
        GiveGun(p, "ak-47");
        }
        
        if (rint == 15) {
        GiveGun(p, "m4a97");
        }
        
        if (rint == 16) {
        GiveGun(p, "ak-47");
        }
        
        if (rint == 17) {
        GiveGun(p, "Barret-50");
        }
        
        if (rint == 18) {
        GiveGun(p, "DAQ-12");
        }

        if (rint == 19) {
        GiveGun(p, "DAQ-12");
        }
        
        if (rint == 20) {
        GiveGun(p, "DAQ-12");
        }
        
        if (rint == 21) {
            GiveGun(p, "m4a97");
            }
            
            if (rint == 22) {
            GiveGun(p, "ak-47");
            }
            
            if (rint == 23) {
            GiveGun(p, "Grendae "+rint2);
            }
            
            if (rint == 25) {
            GiveGun(p, "ApalmGrenade "+rint2);
            }
            
            if (rint == 24) {
            GiveGun(p, "sandycluster "+rint2);
            }
            
            if (rint == 26) {
            GiveGun(p, "p90");
            }
            
            if (rint == 27) {
            GiveGun(p, "Barret-50");
            }
            
            if (rint == 28) {
            giveBandage(p, rint2);
            }
            
            if (rint == 29) {
            giveBandage(p, rint2);
            }
            
            if (rint == 30) {
            giveBandage(p, rint2);
            }
            
            if (rint == 31) {
            GiveGun(p, "m4a97");
            }
            
            if (rint == 32) {
            GiveGun(p, "ak-47");
            }
            
            if (rint == 33) {
            GiveGun(p, "m4a97");
            }
            
            if (rint == 34) {
            GiveGun(p, "ak-47");
            }
            
            if (rint == 35) {
            GiveGun(p, "m4a97");
            }
            
            if (rint == 36) {
            GiveGun(p, "ak-47");
            }
            
            if (rint == 37) {
            GiveGun(p, "Barret-50");
            }
            
            if (rint == 38) {
            GiveGun(p, "DAQ-12");
            }

            if (rint == 39) {
            GiveGun(p, "DAQ-12");
            }
            
            if (rint == 40) {
            GiveGun(p, "DAQ-12");
            }
        
        
        
        
	}
	

	public void Score() {

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			public void run() {

				int REDT = CNTIC;
				int UST = USTIC;

				String AN = "None";
				String BN = "None";
				String CN = "None";
				String DN = "None";
				String EN = "None";

				if (A.equals("CN")) {
					AN = "CN";
				}
				if (A.equals("US")) {
					AN = "US";
				}

				if (B.equals("CN")) {
					BN = "CN";
				}
				if (B.equals("US")) {
					BN = "US";
				}

				if (C.equals("CN")) {
					CN = "CN";
				}
				if (C.equals("US")) {
					CN = "US";
				}

				if (D.equals("CN")) {
					DN = "CN";
				}
				if (D.equals("US")) {
					DN = "US";
				}

				if (E.equals("CN")) {
					EN = "CN";
				}
				if (E.equals("US")) {
					EN = "US";
				}

				Bukkit.broadcastMessage(ChatColor.RED + "========"
						+ ChatColor.GREEN + "Score" + ChatColor.RED
						+ "========");
				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
						+ "CN Tickets: " + REDT);
				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
						+ "US Tickets: " + UST);

				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
						+ "Objective A: " + AN);

				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
						+ "Objective B: " + BN);

				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
						+ "Objective C: " + CN);

				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
						+ "Objective D: " + DN);

				Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
						+ "Objective E: " + EN);

				Bukkit.broadcastMessage(ChatColor.RED + "========"
						+ ChatColor.GREEN + "=====" + ChatColor.RED
						+ "========");
			}

		}, 0L, 600L);

	}

	public void CheckSign(Block b, Sign s) {

	}

	// SCapturePoint(PLAYER, POINT LETTER);

	// ======================
	// ===START=OF=SCAPTURE===
	// ======================

	public void SCapturePoint(Player p, String s) {

		Scores();
		
		if (s.equalsIgnoreCase("A")) {
			A = "Neutral";
		}
		
		if (s.equalsIgnoreCase("B")) {
			B = "Neutral";
		}
		
		if (s.equalsIgnoreCase("C")) {
			C = "Neutral";
		}
		
		if (s.equalsIgnoreCase("D")) {
			D = "Neutral";
		}
		
		if (s.equalsIgnoreCase("E")) {
			E = "Neutral";
		}

		if (s.equalsIgnoreCase("A")) {
			if (getpTeam(p).equals("CN")) {
				CaptureTimer(s, p);

			}
			if (getpTeam(p).equals("US")) {

				CaptureTimer(s, p);

			}
		}
		if (s.equalsIgnoreCase("B")) {
			if (getpTeam(p).equals("CN")) {
				CaptureTimer(s, p);

			}
			if (getpTeam(p).equals("US")) {
				CaptureTimer(s, p);

			}
		}
		if (s.equalsIgnoreCase("C")) {
			if (getpTeam(p).equals("CN")) {

				CaptureTimer(s, p);

			}
			if (getpTeam(p).equals("US")) {

				CaptureTimer(s, p);

			}
		}
		if (s.equalsIgnoreCase("D")) {
			if (getpTeam(p).equals("CN")) {
				CaptureTimer(s, p);

			}
			if (getpTeam(p).equals("US")) {

				CaptureTimer(s, p);

			}
		}
		if (s.equalsIgnoreCase("E")) {
			if (getpTeam(p).equals("CN")) {

				CaptureTimer(s, p);

			}
			if (getpTeam(p).equals("US")) {

				CaptureTimer(s, p);

			}
		}

	}

	public void GiveTokens(String N, int A) {

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tokengive " + N
				+ " " + A);

	}

	public void CaptureTimer(final String f, final Player p) {

	 Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {
						
						if (f.equalsIgnoreCase("A")) {
							if (getpTeam(p).equalsIgnoreCase(A)){
								
							}else{
								CapturePoint(p, f);
								p.sendMessage(ChatColor.GREEN + "Youve Captured This Point");
							}
							}
						
						if (f.equalsIgnoreCase("B")) {
							if (getpTeam(p).equalsIgnoreCase(B)){
								
							}else{
								CapturePoint(p, f);
								p.sendMessage(ChatColor.GREEN + "Youve Captured This Point");
							}
							}
						
						if (f.equalsIgnoreCase("C")) {
							if (getpTeam(p).equalsIgnoreCase(C)){
								
							}else{
								CapturePoint(p, f);
								p.sendMessage(ChatColor.GREEN + "Youve Captured This Point");
							}
							}
						
						if (f.equalsIgnoreCase("D")) {
							if (getpTeam(p).equalsIgnoreCase(D)){
								
							}else{
								CapturePoint(p, f);
								p.sendMessage(ChatColor.GREEN + "Youve Captured This Point");
							}
							}
						
						if (f.equalsIgnoreCase("E")) {
							if (getpTeam(p).equalsIgnoreCase(E)){
								
							}else{
								CapturePoint(p, f);
								p.sendMessage(ChatColor.GREEN + "Youve Captured This Point");
							}
							}
					}

				}, 120L);

	}

	// ======================
	// ===START=OF=CAPTURE===
	// ======================
	public void CapturePoint(Player p, String s) {

		String team = getpTeam(p);

		int OC = this.getConfig().getInt("PlayerData."+p.getName()+".ObjectiveCapture");
		
		this.getConfig().set("PlayerData."+p.getName()+".ObjectiveCapture", OC+1);
		this.saveConfig();
		
		if (s.equalsIgnoreCase("A")) {
			if (getpTeam(p).equals("CN")) {

				if (getpTeam(p).equals(A)) {
					// DO NOTHING
				} else {
					A = team;
					SetPointBlock("A", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
			if (getpTeam(p).equals("US")) {

				if (getpTeam(p).equals(A)) {
					// DO NOTHING
				} else {
					A = team;
					SetPointBlock("A", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
		}
		if (s.equalsIgnoreCase("B")) {
			if (getpTeam(p).equals("CN")) {

				if (getpTeam(p).equals(B)) {
					// DO NOTHING
				} else {
					B = team;
					SetPointBlock("B", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
			if (getpTeam(p).equals("US")) {

				if (getpTeam(p).equals(B)) {
					// DO NOTHING
				} else {
					B = team;
					SetPointBlock("B", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
		}
		if (s.equalsIgnoreCase("C")) {
			if (getpTeam(p).equals("CN")) {

				if (getpTeam(p).equals(C)) {
					// DO NOTHING
				} else {
					C = team;
					SetPointBlock("C", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
			if (getpTeam(p).equals("US")) {

				if (getpTeam(p).equals(C)) {
					// DO NOTHING
				} else {
					C = team;
					SetPointBlock("C", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
		}
		if (s.equalsIgnoreCase("D")) {
			if (getpTeam(p).equals("CN")) {

				if (getpTeam(p).equals(D)) {
					// DO NOTHING
				} else {
					D = team;
					SetPointBlock("D", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
			if (getpTeam(p).equals("US")) {

				if (getpTeam(p).equals(D)) {
					// DO NOTHING
				} else {
					D = team;
					SetPointBlock("D", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
		}
		if (s.equalsIgnoreCase("E")) {
			if (getpTeam(p).equals("CN")) {

				if (getpTeam(p).equals(E)) {
					// DO NOTHING
				} else {
					E = team;
					SetPointBlock("E", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
			if (getpTeam(p).equals("US")) {

				if (getpTeam(p).equals(E)) {
					// DO NOTHING
				} else {
					E = team;
					SetPointBlock("E", team);
					for (String CC : this.getConfig().getStringList("GameSettings.ObjectiveSettings.RewardKillCommands")) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(),  CC.replace("<PLAYERNAME>", p.getName()));
					}
					for (Player sp : Bukkit.getOnlinePlayers()) {
						sp.playSound(sp.getLocation(), Sound.ENDERDRAGON_GROWL, 10, 10);
					}
					return;
				}

			}
		}

	}

	public void SetPointBlock(String p, String t) {

		Location A = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.A.X"), this.getConfig().getInt("GameSettings.A.Y"), this.getConfig().getInt("GameSettings.A.Z"));
		Location D = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.B.X"), this.getConfig().getInt("GameSettings.B.Y"), this.getConfig().getInt("GameSettings.B.Z"));
		Location C = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.C.X"), this.getConfig().getInt("GameSettings.C.Y"), this.getConfig().getInt("GameSettings.C.Z"));
		Location B = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.D.X"), this.getConfig().getInt("GameSettings.D.Y"), this.getConfig().getInt("GameSettings.D.Z"));
		Location E = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.E.X"), this.getConfig().getInt("GameSettings.E.Y"), this.getConfig().getInt("GameSettings.E.Z"));

		if (p.equalsIgnoreCase("A")) {
			if (t.equalsIgnoreCase("CN")) {

				Block block = A.getBlock();
				block.setTypeIdAndData(95, (byte) 14, false);

			}
			if (t.equalsIgnoreCase("US")) {
				Block block = A.getBlock();
				block.setTypeIdAndData(95, (byte) 11, false);

			}

		}

		if (p.equalsIgnoreCase("B")) {

			if (t.equalsIgnoreCase("CN")) {

				Block block = B.getBlock();
				block.setTypeIdAndData(95, (byte) 14, false);

			}
			if (t.equalsIgnoreCase("US")) {
				Block block = B.getBlock();
				block.setTypeIdAndData(95, (byte) 11, false);

			}

		}

		if (p.equalsIgnoreCase("C")) {

			if (t.equalsIgnoreCase("CN")) {

				Block block = C.getBlock();
				block.setTypeIdAndData(95, (byte) 14, false);

			}
			if (t.equalsIgnoreCase("US")) {
				Block block = C.getBlock();
				block.setTypeIdAndData(95, (byte) 11, false);

			}

		}

		if (p.equalsIgnoreCase("D")) {

			if (t.equalsIgnoreCase("CN")) {

				Block block = D.getBlock();
				block.setTypeIdAndData(95, (byte) 14, false);

			}
			if (t.equalsIgnoreCase("US")) {
				Block block = D.getBlock();
				block.setTypeIdAndData(95, (byte) 11, false);

			}

		}

		if (p.equalsIgnoreCase("E")) {

			if (t.equalsIgnoreCase("CN")) {

				Block block = E.getBlock();
				block.setTypeIdAndData(95, (byte) 14, false);

			}
			if (t.equalsIgnoreCase("US")) {
				Block block = E.getBlock();
				block.setTypeIdAndData(95, (byte) 11, false);

			}
		}
	}

	public void ReSetPointBlock() {

		Location A = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.A.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.A.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.A.Z"));
		Location D = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.B.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.B.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.B.Z"));
		Location C = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.C.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.C.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.C.Z"));
		Location B = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.D.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.D.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.D.Z"));
		Location E = new Location(Bukkit.getWorld("world"), this.getConfig().getInt("GameSettings.ObjectiveSettings.E.X"), this.getConfig().getInt("GameSettings.ObjectiveSettings.E.Y"), this.getConfig().getInt("GameSettings.ObjectiveSettings.E.Z"));
		
		Block Ablock = A.getBlock();
		Ablock.setTypeIdAndData(20, (byte) 1, false);

		Block Bblock = B.getBlock();
		Bblock.setTypeIdAndData(20, (byte) 1, false);

		Block Cblock = C.getBlock();
		Cblock.setTypeIdAndData(20, (byte) 1, false);

		Block Dblock = D.getBlock();
		Dblock.setTypeIdAndData(20, (byte) 1, false);

		Block Eblock = E.getBlock();
		Eblock.setTypeIdAndData(20, (byte) 1, false);

	}

	public void GiveColor(Player p, String t) {
		if (t == null) {

		} else {
			if (p == null) {

			} else {
				if (t.equalsIgnoreCase("CN")) {
					ItemStack helm = new ItemStack(Material.LEATHER_CHESTPLATE);
					LeatherArmorMeta meta = (LeatherArmorMeta) helm
							.getItemMeta();
					meta.setColor(Color.RED);
					helm.setItemMeta(meta);

					p.getInventory().setChestplate(helm);
				}
				if (t.equalsIgnoreCase("US")) {
					ItemStack helm = new ItemStack(Material.LEATHER_CHESTPLATE);
					LeatherArmorMeta meta = (LeatherArmorMeta) helm
							.getItemMeta();
					meta.setColor(Color.BLUE);
					helm.setItemMeta(meta);

					p.getInventory().setChestplate(helm);
				}
			}
		}
	}

	public void FlagOperations() {

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			public void run() {
				if (A.equalsIgnoreCase("None")) {
					return;
				}
				if (B.equalsIgnoreCase("None")) {
					return;
				}
				if (C.equalsIgnoreCase("None")) {
					return;
				}
				if (D.equalsIgnoreCase("None")) {
					return;
				}
				if (E.equalsIgnoreCase("None")) {
					return;
				}

				if (A.equals("CN")) {
					if (CNTIC <= 49) {
						USTIC = USTIC - 1;
					}
				}
				if (B.equals("CN")) {
					if (CNTIC <= 49) {
						USTIC = USTIC - 1;
					}
				}
				if (C.equals("CN")) {
					if (CNTIC <= 49) {
						USTIC = USTIC - 1;
					}
				}
				if (D.equals("CN")) {
					if (CNTIC <= 49) {
						USTIC = USTIC - 1;
					}
				}
				if (E.equals("CN")) {
					if (CNTIC <= 49) {
						USTIC = USTIC - 1;
					}
				}

				if (A.equals("US")) {
					if (USTIC <= 49) {
						CNTIC = CNTIC - 1;
					}
				}
				if (B.equals("US")) {
					if (USTIC <= 49) {
						CNTIC = CNTIC - 1;
					}
				}
				if (C.equals("US")) {
					if (USTIC <= 49) {
						CNTIC = CNTIC - 1;
					}
				}
				if (D.equals("US")) {
					if (USTIC <= 49) {
						CNTIC = CNTIC - 1;
					}
				}
				if (E.equals("US")) {
					if (USTIC <= 49) {
						CNTIC = CNTIC - 1;
					}
				}
			}
		}, 0L, 600L);

	}

	public void AnWinter() {
		WinterTimer();
	}

	public void WinterTimer() {

		proc = Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {

						Bukkit.broadcastMessage(ChatColor.BOLD + ""
								+ ChatColor.RED + "=========================");
						Bukkit.broadcastMessage(ChatColor.BOLD + ""
								+ ChatColor.RED + "= " + ChatColor.GREEN
								+ "The Developer Has Joined! " + ChatColor.RED
								+ "=");
						Bukkit.broadcastMessage(ChatColor.BOLD + ""
								+ ChatColor.RED + "=======" + ChatColor.GREEN
								+ "Wintergrasped" + ChatColor.RED + "======");
						Bukkit.broadcastMessage(ChatColor.BOLD + ""
								+ ChatColor.RED + "=========================");

					}

				}, 20L);

	}

	public void AnTeam(Player p) {
		TeamATimer(p);
	}

	public void TeamATimer(final Player p) {

		proc = Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {

						TeamAnTeam(p);
					}

				}, 20L);

	}
	
	public void InfoTimer(final Player p) {

		proc = Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {

						p.sendMessage(ChatColor.RED
								+ "To get in a class Do /class help");
						p.sendMessage(ChatColor.RED
								+ "To get help on how to play do /bf help");
						p.sendMessage(ChatColor.RED + "Avilable Classes: " + ChatColor.GREEN
								+ " Assult, Medic, Engineer, Sniper");
						p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD
								+ "Devloped By Wintergrasped");

					}

				}, 20L);

	}

	public void ChatTimer() {

		proc = Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {

						CScore();

					}

				}, 20L);

	}

	public void SkillTimer(final Player p) {

		proc = Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {

						SSKill(p);

					}

				}, 10L);

	}

	public void Scores() {

		return;

	}

	public void CScore() {
		int REDT = CNTIC;
		int UST = USTIC;

		String AN = "None";
		String BN = "None";
		String CN = "None";
		String DN = "None";
		String EN = "None";

		if (A.equals("CN")) {
			AN = "CN";
		}
		if (A.equals("US")) {
			AN = "US";
		}

		if (B.equals("CN")) {
			BN = "CN";
		}
		if (B.equals("US")) {
			BN = "US";
		}

		if (C.equals("CN")) {
			CN = "CN";
		}
		if (C.equals("US")) {
			CN = "US";
		}

		if (D.equals("CN")) {
			DN = "CN";
		}
		if (D.equals("US")) {
			DN = "US";
		}

		if (E.equals("CN")) {
			EN = "CN";
		}
		if (E.equals("US")) {
			EN = "US";
		}

		Bukkit.broadcastMessage(ChatColor.RED + "========" + ChatColor.GREEN
				+ "Score" + ChatColor.RED + "========");
		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
				+ "CN Tickets: " + REDT);
		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
				+ "US Tickets: " + UST);

		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
				+ "Objective A: " + AN);

		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
				+ "Objective B: " + BN);

		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
				+ "Objective C: " + CN);

		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
				+ "Objective D: " + DN);

		Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD
				+ "Objective E: " + EN);

		Bukkit.broadcastMessage(ChatColor.RED + "========" + ChatColor.GREEN
				+ "=====" + ChatColor.RED + "========");
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {

		String m = e.getMessage();

		Player p = e.getPlayer();

		if (m.equalsIgnoreCase("!Score")) {
			ChatTimer();
		}

		if (m.equalsIgnoreCase("!suicide")) {
			SkillTimer(p);
		}
		
		if (m.equalsIgnoreCase("!Callout")) {
			if (CO == false) {
				COLTimer();
				p.sendMessage(ChatColor.GREEN + "Gathering Player Locations.");
			}
		}

		if (m.equalsIgnoreCase("@winr")) {
			e.setCancelled(true);
			win("CN");
		}

		if (m.equalsIgnoreCase("@winb")) {
			e.setCancelled(true);
			win("US");
		}
	}

	public void COTimer() {

		proc = Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {

						CO = false;

					}

				}, 3000L);

	}

	public void SSKill(Player p) {
		p.setHealth(0);
		Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.AQUA
				+ p.getName() + " Was a Discrase and commited suicide!");
		Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.AQUA
				+ p.getName() + " Thus they wont be missed");
	}

	public void COLTimer() {

		proc = Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {
						for (Player pl : Bukkit.getOnlinePlayers()) {
							CO = true;
							COTimer();
							Bukkit.broadcastMessage(ChatColor.RED
									+ " A Player Is At - X: "
									+ ChatColor.AQUA
									+ ""
									+ Integer.valueOf((int) pl.getLocation()
											.getX())
									+ ChatColor.RED
									+ " Y: "
									+ ChatColor.AQUA
									+ ""
									+ Integer.valueOf((int) pl.getLocation()
											.getY())
									+ ChatColor.RED
									+ " Z: "
									+ ChatColor.AQUA
									+ Integer.valueOf((int) pl.getLocation()
											.getZ()));
						}

					}

				}, 10L);

	}

	public void RTT() {

		proc = Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {
						Bukkit.broadcastMessage(ChatColor.GREEN + ""
								+ ChatColor.BOLD + "RELOADING");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "");

					}

				}, 10L);

	}

	public void Ftimer(String f) {

		proc = Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {
						FA = false;
						FB = false;
						FC = false;
						FD = false;
						FE = false;

					}

				}, 120L);

	}
	
	public void TeamAnTeam(Player p) {
		
		if (p.getName().equalsIgnoreCase("DragonMasterFu")) {

			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "= " + ChatColor.GREEN + "A Scrypted Member Has Joined! " + ChatColor.RED+ "=");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=======" + ChatColor.GREEN + p.getName() + ChatColor.RED + "======");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");

		}
		
		if (p.getName().equalsIgnoreCase("DarkerBlaze")) {

			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "= " + ChatColor.GREEN + "A Sniper Of Scrytped Has Joined! " + ChatColor.RED+ "=");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=======" + ChatColor.GREEN + p.getName() + ChatColor.RED + "======");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");
		}
		
		if (p.getName().equalsIgnoreCase("kitty69Lol")) {

			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "===============================");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "= " + ChatColor.GREEN + "Da Clown of Scrypted Has Joined! " + ChatColor.RED+ "=");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=======" + ChatColor.GREEN + p.getName() + ChatColor.RED + "======");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "===============================");

		}
		
		if (p.getName().equalsIgnoreCase("BunkerM4")) {

			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "= " + ChatColor.GREEN + "A Scrypted Member Has Joined! " + ChatColor.RED+ "=");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=======" + ChatColor.GREEN + p.getName() + ChatColor.RED + "======");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");

		}
		
		if (p.getName().equalsIgnoreCase("Mutiplyyou")) {

			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "= " + ChatColor.GREEN + "A Scrypted Member Has Joined! " + ChatColor.RED+ "=");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=======" + ChatColor.GREEN + p.getName() + ChatColor.RED + "======");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");

		}
		
		if (p.getName().equalsIgnoreCase("1redstonemaster")) {

			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "= " + ChatColor.GREEN + "A Scrypted Owner Has Joined! " + ChatColor.RED+ "=");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=======" + ChatColor.GREEN + p.getName() + ChatColor.RED + "======");
			Bukkit.broadcastMessage(ChatColor.BOLD + "" + ChatColor.RED + "=========================");

		}
		
	}
	
	public void RoundTimer() {

		Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {
						
						if (USTIC > CNTIC) {
							win("US");
						}
						
						if (CNTIC > USTIC) {
							win("CN");
						}
						
						if (CNTIC == USTIC) {
							win("TIE");
						}
						

					}

				}, 18000L);

	}
	
	public void HugerTimer() {

		Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {
						for (Player pl : Bukkit.getOnlinePlayers()) {
							pl.setFoodLevel(100);
						}

					}

				}, 20L);

	}
	
	
    @EventHandler
	public void SetHuner(FoodLevelChangeEvent e) {
		
    	
    	if (e.getEntity() instanceof Player) {
			
			e.setFoodLevel(20);
			
		}
    }
    
   
    
    static private void copyFile(InputStream in, File out) throws Exception {
    	InputStream fis = in;
    	FileOutputStream fos = new FileOutputStream(out);
    	try {
    	byte[] buf = new byte[1024];
    	int i = 0;
    	while ((i = fis.read(buf)) != -1) {
    	fos.write(buf, 0, i);
    	}
    	} catch (Exception e) {
    	throw e;
    	} finally {
    	if (fis != null) {
    	fis.close();
    	}
    	if (fos != null) {
    	fos.close();
    	}
    	}
    	}
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
    	
    	List<String> LS = new ArrayList();
    	LS.add("Right Click To Heal!");
    	ItemStack b = new ItemStack(Material.PAPER, e.getPlayer().getItemInHand().getAmount());
    	ItemMeta im = b.getItemMeta();
    	  	
    	im.setDisplayName(ChatColor.GOLD + "Bandage");
    	im.setLore(LS);
    	b.setItemMeta(im);
    	
    	if (e.getPlayer().getItemInHand().equals(b)) {
    		e.getPlayer().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "You Have been Bandaged");
    		e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount()-1);
    		if (e.getPlayer().getItemInHand().getAmount() == 1) {
    			e.getPlayer().setItemInHand(new ItemStack(Material.AIR, 0));
    		}
    		double HP = e.getPlayer().getHealth();
    		e.getPlayer().setHealth(HP+4.5);
    		
    		int band = this.getConfig().getInt("PlayerData."+e.getPlayer().getName()+".Bandaged");
    		
    		this.getConfig().set("PlayerData."+e.getPlayer().getName()+".Bandaged", band+1);
    		this.saveConfig();
    	}
    	
    }
    
    public void giveBandage(Player p, int a) {
    	List<String> LS = new ArrayList();
    	LS.add("Right Click To Heal!");
    	ItemStack b = new ItemStack(Material.PAPER, a);
    	ItemMeta im = b.getItemMeta();
    	
    	im.setDisplayName(ChatColor.GOLD + "Bandage");
    	im.setLore(LS);
    	b.setItemMeta(im);
    	
    	p.getInventory().addItem(b);
    	
    }

    
    public void CreatPlayerConfig(Player p) {
    	this.getConfig().set("PlayerData."+p.getName()+".BattlesPlayed", "0");
    	this.getConfig().set("PlayerData."+p.getName()+".ObjectivesCapture", "0");
    	this.getConfig().set("PlayerData."+p.getName()+".Deaths", "0");
    	this.getConfig().set("PlayerData."+p.getName()+".Kills", "0");
    	this.getConfig().set("PlayerData."+p.getName()+".Bandaged", "0");
    	this.saveConfig();
    }
   
    
    public void SQLUpdate(Player p) {
    	int BP = this.getConfig().getInt("PlayerData."+p.getName()+".BattlesPlayed");
    	int OBC = this.getConfig().getInt("PlayerData."+p.getName()+".ObjectivesCapture");
    	int PD = this.getConfig().getInt("PlayerData."+p.getName()+".Deaths");
    	int PK = this.getConfig().getInt("PlayerData."+p.getName()+".Kills");
    	int BA = this.getConfig().getInt("PlayerData."+p.getName()+".Bandaged");
    	
    	sql.setDeaths(p.getName(), PD);
    	sql.setOC(p.getName(), OBC);
    	sql.setKills(p.getName(), PK);
    	sql.setBandages(p.getName(), BA);
    	sql.setBP(p.getName(), BP);
    	this.saveConfig();
    }
    
	public void LoadSQL() {

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			public void run() {
				
				for (Player p : Bukkit.getOnlinePlayers()) {
					SQLUpdate(p);
				}
			}
		}, 0L, 600L);

	}
	
	public void StopINF() {
		if (CN.size() == US.size()) {
			
			return;
			
		}
		
		if (CN.size() == 0) {
			win("US");
		}

		if (US.size() == 0) {
			win("CN");
		}
	}
	
	public boolean CheckLicense () {
		String L = "None";
		Bukkit.getLogger().log(Level.INFO, "[Battle Feild] License Check Started");
			if (this.getConfig().contains("ServerInfo.License")) {
				 L = this.getConfig().getString("ServerInfo.License");
			}else{
				this.getConfig().set("ServerInfo.License", "YOUR LICENSE KEY HERE!");
				return false;
			}
		
		if (sql.getServer(L) <= 0) {
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Invaild License BattelFeild Shutting Down!");
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Please Check You Set The Right License Key In the Config.yml");
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Please Check You Set The Right Server ID  in your server.preperties");
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] If it IS the Right Key contact winter@wintereco.net about your key being Disabled");
			Bukkit.getPluginManager().disablePlugin(this);
		
		return false;
	}else{
		Bukkit.getLogger().log(Level.INFO, "[Battle Feild] ServerID Check Passed!");
	}
		
		if (sql.getLicense().equals(this.getConfig().getString("ServerInfo.License"))) {
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] License Check Passed!");
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] License: " + this.getConfig().getString("ServerInfo.License"));
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Server ID: " + Bukkit.getServerId());
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Thank You For Your Purchase!");
			return true;
		}else{
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Invaild License BattelFeild Shutting Down!");
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Please Check You Set The Right License Key In the Config.yml");
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] Please Check You Set The Right Server ID  in your server.preperties");
			Bukkit.getLogger().log(Level.INFO, "[Battle Feild] If it IS the Right Key contact winter@wintereco.net about your key being Disabled");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		return false;
}
	
	public void LicenseTimer() {

		Bukkit.getScheduler().scheduleSyncDelayedTask(this,
				new Runnable() {

					public void run() {
						
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamerule keepInventory true");

					}

				}, 40L);

	}
	
	
	@EventHandler
	public void onInv(InventoryMoveItemEvent e) {

		e.setCancelled(true);
	}
}