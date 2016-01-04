package net.wmcsn.DeathMatch;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

import net.wmcsn.DeathMatch.MainClass;

public class Mysql {

	
	

	
	public Mysql(MainClass mC, String Host, int porta, String databasea, String usera, String passa){
		main = mC;
		host = Host;
		port = porta;
		database = databasea;
		user = usera;
		pass = passa;
		MySQL = new MySQLBase(main, host, String.valueOf(port), database, user, pass);
	}
	MainClass main = null;
	String host = "";
	String database = "";
	String user = "";
	String pass = "";
	int port = 0;
	MySQLBase MySQL = null;
	Connection c = null;
	public void openConnection(){
		c = MySQL.openConnection();
		
	}
	
	public void closeConnection(){
		MySQL.closeConnection();
	}
	
	public int getTokens(String Player){
		
		try {
			Statement statement = c.createStatement();
		
		int money = 0;
		ResultSet res;
		
			res = statement.executeQuery("SELECT * FROM `NSHC_Points` WHERE  `name`='" + Player + "';");
		
		res.next();
		money = res.getInt("points");
		return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
		
		
	}
	
	public void setTokens(String Player, int a){
		
		try {
			Statement statement = c.createStatement();
		
		
	
		
			statement.executeUpdate("UPDATE `" + this.database + "`.`NSHC_Points` SET `points`=" + a + " WHERE  `name`='" + Player + "';");
			
		
		
		
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addGameTotal(){
		try {
			Statement statement = c.createStatement();
		
		int money = 0;
		ResultSet res;
		
			res = statement.executeQuery("SELECT * FROM `Game` WHERE  `Game`='Breacher';");
		
		res.next();
		money = res.getInt("TotalGames");
		money++;
		statement.executeUpdate("UPDATE `" + this.database + "`.`Game` SET `TotalGames`=" + money + " WHERE  `Game`='Breacher';");
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public String getGameTag(){
		try {
			Statement statement = c.createStatement();
		
		String money = "";
		ResultSet res;
		
			res = statement.executeQuery("SELECT * FROM `Game` WHERE  `Game`='Breacher';");
		
		res.next();
		money = res.getString("GameTag");
		return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
		
	}
	
	
   public void setupGameTable(String table){
		
		
		try {
			Statement statement = c.createStatement();
		
		
		
			
			statement.executeUpdate("CREATE TABLE " + table + " (`Game` TEXT,`GameTag` TEXT,`TotalGames` BIGINT,`MinPlayers` INT,`StartTimeSeconds` INT ,`GameTimeSeconds` INT, `SecureSeconds` INT)");

			statement.executeUpdate("INSERT INTO " + table + " (`Game`, `GameTag`, `TotalGames`, `MinPlayers`, `StartTimeSeconds`, `GameTimeSeconds`, `SecureSeconds`) VALUES ('Breacher', '&7[&6Breach&7]', '0', '5', '120', '600', '120');");
		
		
		
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
   
   public int getSecureSeconds(){
		try {
			Statement statement = c.createStatement();
		
		int money = 0;
		ResultSet res;
		
			res = statement.executeQuery("SELECT * FROM `Game` WHERE  `Game`='Breacher';");
		
		res.next();
		money = res.getInt("SecureSeconds");
		return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
   }
   
   public int getStartSeconds(){
	   try {
			Statement statement = c.createStatement();
		
		int money = 0;
		ResultSet res;
		
			res = statement.executeQuery("SELECT * FROM `Game` WHERE  `Game`='Breacher';");
		
		res.next();
		money = res.getInt("StartTimeSeconds");
		return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
   }
   
   public int getGameSeconds(){
	   try {
			Statement statement = c.createStatement();
		
		int money = 0;
		ResultSet res;
		
			res = statement.executeQuery("SELECT * FROM `Game` WHERE  `Game`='Breacher';");
		
		res.next();
		money = res.getInt("GameTimeSeconds");
		return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
   }
   
   public int getMinPlayers(){
	   try {
			Statement statement = c.createStatement();
		
		int money = 0;
		ResultSet res;
		
			res = statement.executeQuery("SELECT * FROM `Game` WHERE  `Game`='Breacher';");
		
		res.next();
		money = res.getInt("MinPlayers");
		return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
   }
   
   public int getTotalGames(){
		try {
			Statement statement = c.createStatement();
		
		int money = 0;
		ResultSet res;
		
			res = statement.executeQuery("SELECT * FROM `Game` WHERE  `Game`='Breacher';");
		
		res.next();
		money = res.getInt("TotalGames");
		return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
   }
	public boolean isSetup(String player){
	
			
			try {
				Statement statement = c.createStatement();
			
			
			ResultSet res;
		    	statement.executeUpdate("USE " + this.database); 
				res = statement.executeQuery("SELECT * FROM players  WHERE PlayerName='" + player + "';");
				
			
			
			
			return res.next();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		
		
	}
	
	public void setupPlayer(String player){
		try {
			Statement statement = c.createStatement();
		
		
		
			statement.executeUpdate("INSERT INTO NSHC_Points (`name`, `points`) VALUES ('" + player + "', '0');");
			
		
		
		
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void setupTable(String table){
		
		
		try {
			Statement statement = c.createStatement();
		
		
		
			
			statement.executeUpdate("CREATE TABLE " + table + " (`PlayerName` TEXT,`Money` BIGINT )");
			
		
		
		
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public boolean tableExist(String tablename){

		try {
			Statement statement = c.createStatement();
		
		
		 
		
			ResultSet res = statement.executeQuery("SELECT count(*) FROM information_schema.TABLES WHERE (TABLE_SCHEMA = '" + this.database + "') AND (TABLE_NAME = '" + tablename + "');");
		
			DatabaseMetaData dbm = c.getMetaData();
			ResultSet tables = dbm.getTables(null, null, tablename, null);
		
		
			if(tables.next()){
				return true;
			}else{
				return false;
			}
			
		
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//=======
	//=Kills=
	//=======
	public void setKills(String Player, int a){
		
		try {
			Statement statement = c.createStatement();
		
		int money = 0;
		ResultSet res;
		
		statement.executeUpdate("UPDATE `" + this.database + "`.`BF_PlayerStats` SET `Kills`=" + a + " WHERE  `Name`='" + Player + "';");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getKills(String Player){
		
		try {
			Statement statement = c.createStatement();
		
		int money = 0;
		ResultSet res;
		
			res = statement.executeQuery("SELECT `Kills` FROM BF_PlayerStats  WHERE Name='" + Player + "';");
		
		res.next();
		money = res.getInt("Kills");
		return money;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
		//=======
		//=Deaths=
		//=======
		public void setDeaths(String Player, int a){
			
			try {
				Statement statement = c.createStatement();
			
			int money = 0;
			ResultSet res;
			
			statement.executeUpdate("UPDATE `" + this.database + "`.`BF_PlayerStats` SET `Deaths`=" + a + " WHERE  `Name`='" + Player + "';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public int getDeaths(String Player, int a){
			
			try {
				Statement statement = c.createStatement();
			
			int money = 0;
			ResultSet res;
			
				res = statement.executeQuery("SELECT `Deaths` FROM BF_PlayerStats  WHERE Name='" + Player + "';");
			
			res.next();
			money = res.getInt("Deaths");
			return money;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return 0;
		}
		
		//=======
		//=ShotsFired=
		//=======
		public void setShotsFire(String Player, int a){
			
			try {
				Statement statement = c.createStatement();
			
			int money = 0;
			ResultSet res;
			
			statement.executeUpdate("UPDATE `" + this.database + "`.`BF_PlayerStats` SET `ShotsFired`=" + a + " WHERE  `Name`='" + Player + "';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public int getShotsFired(String Player){
			
			try {
				Statement statement = c.createStatement();
			
			int money = 0;
			ResultSet res;
			
				res = statement.executeQuery("SELECT `ShotsFired` FROM BF_PlayerStats  WHERE Name='" + Player + "';");
			
			res.next();
			money = res.getInt("ShotsFired");
			return money;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return 0;
		}
		
		//=======
		//=Kills=
		//=======
		public void setBP(String Player, int a){
			
			try {
				Statement statement = c.createStatement();
			
			int money = 0;
			ResultSet res;
			
			statement.executeUpdate("UPDATE `" + this.database + "`.`BF_PlayerStats` SET `Battles_Played`=" + a + " WHERE  `Name`='" + Player + "';");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		public int getBP(String Player){
			
			try {
				Statement statement = c.createStatement();
			
			int money = 0;
			ResultSet res;
			
				res = statement.executeQuery("SELECT `Battles_Played` FROM BF_PlayerStats  WHERE Name='" + Player + "';");
			
			res.next();
			money = res.getInt("Battles_Played");
			return money;
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return 0;
		}
		
		//============
		//=SetPlayers=
		//============
		public void setNewPlayer(String Player){
			
			try {
				Statement statement = c.createStatement();
			
			int money = 0;
			ResultSet res;
			
			statement.execute("INSERT INTO `winterec_gmod`.`BF_PlayerStats` (`Name`, `Battles_Played`, `ShotsFired`, `Objectives_Captured`, `Kills`, `Deaths`) VALUES ('"+Player+"', 0, 0, 0, 0, 0);");

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//============
		//=SetPlayersNew=
		//============
		public boolean isPlayerThere(String Player){
			
			boolean b = false;
			
			try {
				Statement statement = c.createStatement();
			
			int money = 0;
			ResultSet res;
			
				res = statement.executeQuery("SELECT `Battles_Played` FROM BF_PlayerStats  WHERE Name='" + Player + "';");

				res.next();
				if (res.equals(null)) {
					
				}else{
				money = res.getInt("Battles_Played");
				}
				if (money == 0) {
					b = false;
				}else{
					b = true;
				}
		
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return b;
		}
		
				//=======
				//=Objectives=
				//=======
				public void setOC(String Player, int a){
					
					try {
						Statement statement = c.createStatement();
					
					int money = 0;
					ResultSet res;
					
					statement.executeUpdate("UPDATE `" + this.database + "`.`BF_PlayerStats` SET `Objectives_Captured`=" + a + " WHERE  `Name`='" + Player + "';");

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				public int getOC(String Player){
					
					try {
						Statement statement = c.createStatement();
					
					int money = 0;
					ResultSet res;
					
						res = statement.executeQuery("SELECT `Objectives_Captured` FROM BF_PlayerStats  WHERE Name='" + Player + "';");
					
					res.next();
					money = res.getInt("Objectives_Captured");
					return money;
					} catch (SQLException e) {
						e.printStackTrace();
					}
					return 0;
				}

				public void setBandages(String Player, int a){
					
					try {
						Statement statement = c.createStatement();
					
					int money = 0;
					ResultSet res;
					
					statement.executeUpdate("UPDATE `" + this.database + "`.`BF_PlayerStats` SET `Bandaged`=" + a + " WHERE  `Name`='" + Player + "';");

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				
				public int getServer(String L){
					int ints = 0;
					int intis = 0;
					try {
						ResultSet res;
						Statement statement = c.createStatement();
						Statement statement2 = c.createStatement();
					
					
					
						res = statement.executeQuery("SELECT `Active` FROM BatteFeild_License  WHERE IP='" + Bukkit.getServerId() + "';");
						statement2.execute("INSERT INTO `winterec_gmod`.`BattelFeild_Log` (`ServerID`, `License`) VALUES ('"+Bukkit.getServerId()+"','"+L+"');");
						if (res.equals(null)) {
							return intis;
						}	
						
						res.next();	
						ints = res.getInt("Active");
						if (ints == 0) {
							return intis;
						}else{
						return ints;
						}
					} catch (SQLException e) {
						return 0;
					}
				
}
				
				public String getLicense(){
					String ints = "NONE";
					String intis = "NONE";
					try {
						ResultSet res;
						Statement statement = c.createStatement();
						Statement statement2 = c.createStatement();
					
					
					
						res = statement.executeQuery("SELECT `License` FROM BatteFeild_License  WHERE IP='" + Bukkit.getServerId() + "';");
						if (res.equals(null)) {
							return intis;
						}	
						
						res.next();	
						ints = res.getString("License");
						if (ints == "NONE") {
							return intis;
						}else{
						return ints;
						}
					} catch (SQLException e) {
						return "NONE";
					}
}
}