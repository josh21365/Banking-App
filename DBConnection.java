package com.example.banking_app;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DBConnection {


	

	    private static final String URL = "jdbc:mysql://localhost:3306/finance_tracker";
	    private static final String USER = "root";
	    private static final String PASSWORD = "Java#cake2156josh";
	    
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
