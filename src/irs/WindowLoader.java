package irs;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class WindowLoader {


  /**
   * This method and this class are entirely responsible which stage/window is to be loaded into
   * the user screen with the exception of the login screen for the first time. It has four parameters and receives
   * an event, an object, and two strings as arguements and decides which stage/window to display
   */

    public void loadWindow(ActionEvent event, Object obj, String windowTitle, String fxmlLocation)throws Exception{

        Parent dashboardWindow = FXMLLoader.load(getClass().getResource(fxmlLocation));
        Scene dashboardView = new Scene(dashboardWindow);

        //Determine from which Stage the event is coming and taking that stage
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(dashboardView);


        /**
         * This Checks the passed object's type. After determining its type call the right
         * get method to fetch patient details or Test data
         */
        if (obj instanceof Patient){

            ((Patient) obj).getPatients();

        }else if(obj instanceof TestResult){
            ((TestResult) obj).getTests();
        }else if(obj instanceof User){
            ((User)obj).getUsers();
        }

        window.setTitle(windowTitle);
        window.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        window.setResizable(false);
        window.centerOnScreen();
        window.show();


    }
}
