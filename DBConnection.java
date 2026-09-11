package com.example.banking_app;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {


	

	    private static final String URL = "";
	    private static final String USER = "";
	    private static final String PASSWORD = "";
	    
	    public Connection getConnection() {
	        try {	        	
		            // Try to connect
			       Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
	        	
			       return conn;
	        } 
	        catch (SQLException e) {
	            System.err.println("❌ Connection failed. Check your DB info.");
	            e.printStackTrace();
	            return null;
	        }
	    }
	}
