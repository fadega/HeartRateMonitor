package irs;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class Main extends Application{

    @Override
    public void start(Stage window) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        window.setTitle("IRS System Login");
        window.setScene(new Scene(root));
        window.setResizable(false);
        window.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        window.centerOnScreen();
        window.show();

    }

    public static void main(String[] args)  {
        //Connect and create tables
        DbConnect dbase = new DbConnect();

        try{
            dbase.createDatabase();
            Connection con = dbase.checkConnection();
            if(con!=null){
                dbase.createTables();
            }

        }catch (SQLException se){
            se.printStackTrace();
            //Debugging code
            System.out.println("SQL EXCEPTION");
        } catch (Exception e) {
            e.printStackTrace();
            //Debugging code
            System.out.println("Any non SQL EXCEPTION");
        }

        launch(args);
    }









}
