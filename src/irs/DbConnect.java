package irs;
/**
 * This class implements connection to the database
 * @author Fadega
 * @version 1.0
 */
import java.sql.*;

public class DbConnect {

    //Variables that hold important info to create database and connect to server(localhost)
     private static String driver= "com.mysql.cj.jdbc.Driver";
     private static String dbUrl = "jdbc:mysql://localhost:3306/irsdb?createDatabaseIfNotExist=true";
     private static String user = "root";
     private static String password ="";

     /**
      * create database irsdb if it doesn't exist
      */
     public static void createDatabase()throws SQLException, Exception{
         try {
             //load and initialize classes in the jdbc driver
             Class.forName(driver);
             //get connection from the registered connection string through DriverManger
             DriverManager.getConnection(dbUrl, user, password);

         }catch (ClassNotFoundException cne) {
             //catch CNF exception if driver class is not found or set properly
             cne.printStackTrace();
         }
         catch (SQLException se) {
                //SQL exception
             se.printStackTrace();
         }catch (Exception e){
             //Any other exception
             e.printStackTrace();
         }
     }
    /**
     * This methods returns a connection if connection is established successfully and null otherwise
     */
    public static Connection checkConnection(){
        try{
            Class.forName(driver);
            Connection con= DriverManager.getConnection(dbUrl,user,password);
            return con;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create tables patient, user and result if they don't exist
     */
    public static void createTables()throws Exception{
        Connection conn = null;
        Statement stmt = null;
        String queryPatient ="CREATE TABLE IF NOT EXISTS patient"+
                "(patient_id int(11) NOT NULL AUTO_INCREMENT,"+
                 "patient_firstname varchar(255) COLLATE utf8mb4_bin NOT NULL,"+
                 "patient_lastname varchar(255) COLLATE utf8mb4_bin NOT NULL,"+
                 "patient_gender varchar(50) COLLATE utf8mb4_bin NOT NULL,"+
                 "patient_birthdate date NOT NULL,"+
                 "patient_address varchar(255) COLLATE utf8mb4_bin NOT NULL,"+
                 "patient_phone varchar(20) COLLATE utf8mb4_bin NOT NULL,"+
                 "PRIMARY KEY (patient_id),"+
                 "KEY patient_id (patient_id)) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin";


        String queryUser ="CREATE TABLE IF NOT EXISTS user (" +
                "  id int(11) NOT NULL AUTO_INCREMENT," +
                "  firstname varchar(250) COLLATE utf8mb4_bin NOT NULL," +
                "  lastname varchar(250) COLLATE utf8mb4_bin NOT NULL," +
                "  username varchar(50) COLLATE utf8mb4_bin NOT NULL," +
                "  password varchar(255) COLLATE utf8mb4_bin NOT NULL," +
                "  confirmpassword varchar(250) COLLATE utf8mb4_bin NOT NULL," +
                "  role varchar(10) COLLATE utf8mb4_bin NOT NULL," +
                "  PRIMARY KEY (id)," +
                "  KEY id (id) " +
                ") ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin";

        String queryResult ="CREATE TABLE IF NOT EXISTS result (" +
                "  test_id int(11) NOT NULL AUTO_INCREMENT," +
                "  test_date date NOT NULL," +
                "  test_data int(11) NOT NULL," +
                "  test_center varchar(250) COLLATE utf8mb4_bin NOT NULL," +
                "  patient_fk int(11) NOT NULL," +
                "  user_fk int(11) NOT NULL," +
                " PRIMARY KEY (test_id)," +
                " KEY test_id (test_id)," +
                " KEY patient_fk (patient_fk)," +
                " KEY user_fk (user_fk)," +
                " CONSTRAINT result_ibfk_2 FOREIGN KEY (user_fk) REFERENCES user (id) ON UPDATE CASCADE," +
                " CONSTRAINT result_ibfk_3 FOREIGN KEY (patient_fk) REFERENCES patient (patient_id) ON UPDATE CASCADE" +
                ") ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin";


        try{
             Class.forName("com.mysql.cj.jdbc.Driver");
             conn = checkConnection();
             stmt = conn.createStatement();
             stmt.executeUpdate(queryPatient);
             stmt.executeUpdate(queryUser);
             stmt.executeUpdate(queryResult);

             //If no record exists in user table, insert credentials for first time user
             ResultSet rs = stmt.executeQuery("Select *from user");
             if(!rs.next()){
                 String query = "INSERT INTO user(firstname,lastname,username,password,confirmpassword,role) " +
                         "VALUES('John','Doe','admin','admin123','admin123','Admin')";
                 stmt.executeUpdate(query);
             }

        }catch(SQLException se){

            System.out.println("SQL Error creating a table");
            se.printStackTrace();

        }catch (Exception e){
            System.out.println("Other Error creating a table");
            e.printStackTrace();

        }finally {
            try{
                if(stmt!=null){
                    conn.close();
                }
            }catch (SQLException se){
                //do nothing
            }
            try{
                if(conn!=null){
                    conn.close();

                }

            }catch (SQLException se){
               //do nothing
            }

        }

    }



}