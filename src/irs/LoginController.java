package irs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.regex.*;


public class LoginController implements Initializable {

    //Controls

    @FXML private JFXTextField txtUserName;
    @FXML private JFXPasswordField txtPasssword;
    @FXML private JFXRadioButton radioUser;
    @FXML private JFXRadioButton radioAdmin;
    @FXML private Label lblWarning, lblIndicate;
    private final ToggleGroup levelGroup = new ToggleGroup();



    @FXML
    void login(ActionEvent event) throws Exception {
         Patient patientObj= new Patient();
        //These styles will retain the normal state after validation styles are applied
        txtUserName.setStyle("-fx-border-color:none");
        txtPasssword.setStyle("-fx-border-color:none");
        radioUser.setStyle("-fx-text-fill:black");
        radioAdmin.setStyle("-fx-text-fill:black");

        //This method will return true if the user exists in the database, false otherwise
        Boolean valid = validateCredentials();

        if(valid) {
            WindowLoader winLoad = new WindowLoader();
            String windowTitle = "IRS Management Console";
            String fxmlLacation = "dashboard.fxml";

            //The method laodWindow from WindowLoader class determines which window/stage to load
            winLoad.loadWindow(event,patientObj,windowTitle,fxmlLacation);
        }

    }


    public Boolean validateCredentials() throws SQLException{
        Boolean bool=false;
        Connection conn = DbConnect.checkConnection();
        ResultSet  rs=null;
        if(txtUserName.getText().isEmpty() | txtPasssword.getText().isEmpty()|levelGroup.getSelectedToggle()==null){
            if(txtUserName.getText().isEmpty()){
                txtUserName.setStyle("-fx-border-color:red");

            }else if(txtPasssword.getText().isEmpty()){
                txtPasssword.setStyle("-fx-border-color:red");

            }else if(levelGroup.getSelectedToggle()==null){
                radioAdmin.setStyle("-fx-text-fill:red");
                radioUser.setStyle("-fx-text-fill:red");

            }
            lblWarning.setStyle("-fx-text-fill:red");
            lblWarning.setText("Please enter  username,  password and pick user level");
            bool = false;

        }else if(!txtPasssword.getText().isEmpty() && !txtPasssword.getText().isEmpty() && levelGroup.getSelectedToggle()!=null){
            String user;

            if (conn != null) {
                try {
                    RadioButton selectedRadioButton = (RadioButton) levelGroup.getSelectedToggle();
                    String role = selectedRadioButton.getText();

                    //Find a user in the user table with matching credentials with the input
                    //Statement stmt = conn.createStatement();
                    //rs  = stmt.executeQuery("SELECT * FROM user WHERE username= '" + txtUserName.getText() +
                      //      "' AND password='"+txtPasssword.getText()+"'AND role='"+role+"'");
                    PreparedStatement  pstmt= conn.prepareStatement("SELECT *FROM user where username=? AND password=? and role=?");
                    pstmt.setString(1,txtUserName.getText());
                    pstmt.setString(2,txtPasssword.getText());
                    pstmt.setString(3,role);

                    rs =pstmt.executeQuery();

                    if(rs.next()) {
                        user = rs.getString(4);
                        System.out.println("Logged user is => "+user);
                        bool =true;

                    }
                    else{
                        bool =false;
                        lblIndicate.setStyle("-fx-text-fill:red");
                        lblWarning.setText("Incorrect username, password or user level");

                    }


                } catch (Exception e) {
                    //Tell user that there is either connection or authentication issue
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Issue connecting|querying database");
                    alert.setHeaderText(null);
                    alert.setContentText("No such user or pass : Querying unsuccessful!");

                    alert.showAndWait();
                }
                rs.close();
                conn.close();
            }else{
                lblIndicate.setStyle("-fx-text-fill:red");
                lblWarning.setText("");
                lblIndicate.setText("Check your connection to server");
            }
        }

 return bool;
    }



    @FXML
    void cancelLogin(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        // Setting the role/user-level radiobuttons to a ToggleGroup
        radioUser.setToggleGroup(levelGroup);
        radioAdmin.setToggleGroup(levelGroup);
        radioAdmin.setToggleGroup(levelGroup);

        //Resetting the label styles
        lblWarning.setText("");
        lblIndicate.setText("");


    }
}



