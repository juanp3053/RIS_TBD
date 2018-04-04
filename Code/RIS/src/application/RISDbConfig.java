	package application;

	import java.sql.Connection;
	import java.sql.DriverManager;
	import java.sql.SQLException;

	public class RISDbConfig {

		private static final String USERNAME = "ris_app";
		private static final String PASSWORD = "987654321ris";
		private static final String CONN_STRING = "jdbc:mysql://ris2.cjvk36afmzyl.us-east-2.rds.amazonaws.com:3306/risdb";
		
		// connection  method that connects to the MySQL database
		public static Connection getConnection() throws SQLException{

			return DriverManager.getConnection(CONN_STRING, USERNAME, PASSWORD);
		}

		// displays error in more detail if connection to database fails
		public static void displayException(SQLException ex){

			System.err.println("Error Message: " + ex.getMessage());
			System.err.println("Error Code: " + ex.getErrorCode());
			System.err.println("SQL Status: " + ex.getSQLState());

		}
	}

