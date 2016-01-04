package net.wmcsn.DeathMatch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import net.wmcsn.DeathMatch.MainClass;



/**
 * Connects to and uses a MySQL database
 * 
 * @author -_Husky_-
 * @author tips48
 */
public class MySQLBase extends Database {
    private  String user;
    private  String database;
    private  String password;
    private  String port;
    private  String hostname;

    private MainClass main;
    private Connection connection;

    /**
     * Creates a new MySQL instance
     * 
     * @param main2
     *            Plugin instance
     * @param hostname
     *            Name of the host
     * @param port
     *            Port number
     * @param database
     *            Database name
     * @param username
     *            Username
     * @param password
     *            Password
     */
    public MySQLBase(MainClass main2, String hostname, String port, String database, String username, String password) {
        super(main2);
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
        this.connection = null;
        this.main = main2;
    }

    @Override
    public Connection openConnection() {
        try {
        	
        	Class.forName("com.mysql.jdbc.Driver").newInstance();
        	connection = DriverManager.getConnection("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database, this.user, this.password);
        } catch (SQLException e) {
           
        	//plugin.getLogger().log(Level.SEVERE, "Could not connect to MySQL server! because: " + e.getMessage());
        e.printStackTrace();
        } catch (ClassNotFoundException e) {
           plugin.getLogger().log(Level.SEVERE, "JDBC Driver not found!");
        } catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		}

        return connection;
    }

    @Override
    public boolean checkConnection() {
        return connection != null;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Error closing the MySQL Connection!");
                e.printStackTrace();
            }
        }
    }

    public ResultSet querySQL(String query) {
        Connection c = null;

        if (checkConnection()) {
            c = getConnection();
        } else {
            c = openConnection();
        }

        Statement s = null;

        try {
            s = c.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        ResultSet ret = null;

        try {
            ret = s.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void updateSQL(String update) {

        Connection c = null;

        if (checkConnection()) {
            c = getConnection();
        } else {
            c = openConnection();
        }

        Statement s = null;

        try {
            s = c.createStatement();
            s.executeUpdate(update);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        closeConnection();

    }

}