package irs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;

public class UserController  implements Initializable {

    @FXML private JFXTextField txtName;
    @FXML private JFXTextField txtLastName;
    @FXML private JFXTextField txtUserName;
    @FXML private JFXPasswordField txtPassword;
    @FXML private JFXPasswordField txtConfirmPass;
    @FXML private JFXRadioButton radioUser;
    @FXML private JFXRadioButton radioAdmin;
    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, Integer> idCol;
    @FXML private TableColumn<User, String> nameCol;
    @FXML private TableColumn<User, String> lastnamCol;
    @FXML private TableColumn<User, String> usernameCol;
    @FXML private TableColumn<User, String> passCol;
    @FXML private TableColumn<User, String> roleCol;
    @FXML private JFXTextField txtSearch;
    @FXML private JFXButton btnLogout;
    @FXML private JFXButton btnRecommendation;
    @FXML private JFXButton btnManageTest;
    @FXML private JFXButton btnLoadDashboard;


    private User userObj = new User();
    ToggleGroup roleGroup = new ToggleGroup();



    @FXML
    public void passUserToAdd(){
         if(txtName.getText().isEmpty() || txtLastName.getText().isEmpty() || txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty()||
               txtConfirmPass.getText().isEmpty() || ((RadioButton) roleGroup.getSelectedToggle()).getText().isEmpty()){
           validateUserInput();

         }else {

              if(txtPassword.getText().equals(txtConfirmPass.getText()) && !txtPassword.getText().isEmpty()&&txtPassword.getText()!=null){
                   userObj.addUser(txtName.getText(),txtLastName.getText(),txtUserName.getText(),txtPassword.getText(),
                           txtConfirmPass.getText(), ((RadioButton) roleGroup.getSelectedToggle()).getText());
                   clearFields();
                   userTableView.getItems().clear();
                   ObservableList<User> userList = FXCollections.observableArrayList(userObj.getUsers());
                   userTableView.setItems(userList);

               }else{

                  txtConfirmPass.setStyle("-fx-border-color:red");
                  Alert alert= new Alert(Alert.AlertType.WARNING);
                  alert.setTitle("Information");
                  alert.setHeaderText(null);
                  alert.setContentText("Confirm password doesn't match the password!");
                  alert.showAndWait();
             }
         }


        //Clear the input fields and upadate tableview after a successful creation of a record

    }

    private void validateUserInput(){
        if(txtName.getText()==null ||txtName.getText().isEmpty()){
            txtName.setStyle("-fx-border-color:red");
            txtName.setFocusTraversable(true);
        }else{
            txtName.setStyle("-fx-border-color:none");
        }
        if(txtLastName.getText()==null ||txtLastName.getText().isEmpty()){
            txtLastName.setStyle("-fx-border-color:red");
            txtLastName.setFocusTraversable(true);
        }else{
                txtLastName.setStyle("-fx-border-color:none");
            }

        if(txtUserName.getText()==null ||txtUserName.getText().isEmpty()){
            txtUserName.setStyle("-fx-border-color:red");
            txtUserName.setFocusTraversable(true);
        }else{
            txtUserName.setStyle("-fx-border-color:none");
        }
        if(txtPassword.getText()==null ||txtPassword.getText().isEmpty()){
            txtPassword.setStyle("-fx-border-color:red");
            txtPassword.setFocusTraversable(true);
        }else {
            txtPassword.setStyle("-fx-border-color:none");
        }
        if(txtConfirmPass.getText()==null ||txtConfirmPass.getText().isEmpty()){
                txtConfirmPass.setStyle("-fx-border-color:red");
                txtConfirmPass.setFocusTraversable(true);
        }else{
            txtConfirmPass.setStyle("-fx-border-color:none");

        }if(!radioUser.isSelected() && !radioAdmin.isSelected()){
            radioUser.setStyle("-fx-border-color:red");
            radioAdmin.setStyle("-fx-border-color:red");
        }else{
            radioUser.setStyle("-fx-border-color:none");
            radioAdmin.setStyle("-fx-border-color:none");
        }
    }

    @FXML
    private void clearFields() {
        validateUserInput();
        txtName.clear();txtLastName.clear();txtUserName.clear();txtPassword.clear();
        txtConfirmPass.clear();roleGroup.selectToggle(null);
        radioUser.setStyle("-fx-border-color:none");
        radioAdmin.setStyle("-fx-border-color:none");
        txtName.setStyle("-fx-border-color:none");
        txtConfirmPass.setStyle("-fx-border-color:none");
        txtPassword.setStyle("-fx-border-color:none");
        txtUserName.setStyle("-fx-border-color:none");
        txtLastName.setStyle("-fx-border-color:none");
        txtName.setStyle("-fx-border-color:none");

    }

    @FXML
    private void getUserTableData(MouseEvent event) {
        userObj = userTableView.getSelectionModel().getSelectedItem();
        if(userObj!=null){
            System.out.println(" Clicked here ");
            //int id = userObj.getId();
            txtName.setText(userObj.getFirstName());
            txtLastName.setText(userObj.getLastName());
            txtUserName.setText(userObj.getUsername());
            txtPassword.setText(userObj.getPassword());
            txtConfirmPass.setText(userObj.getConfirmPassword());

            String txt =userObj.getRole();
            if(txt.equals("User")){
                radioUser.setSelected(true);

            }else if(txt.equals("Admin")){
                radioUser.setSelected(true);

            }
        }

    }

    private void loadUserData(){

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastnamCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
         passCol.setCellValueFactory(new PropertyValueFactory<>("password"));
//        confirmCol.setCellValueFactory(new PropertyValueFactory<>("confrimPassword"));
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        //Call method getPatients  which returns an observable list of type Patient

        userTableView.setItems(userObj.getUsers());
    }

    @FXML
    void logout(ActionEvent event) {
        btnLogout.setOnAction(e->{
            DashboardController db = new DashboardController();
            db.logout(e);
        });

    }


    public void passUserToUpdate(){
        User row;
        //boolean updated;
        row= userTableView.getSelectionModel().getSelectedItem();


        if(row!=null){

            int id = row.getId();
            row.updateUser(id,txtName.getText(),txtLastName.getText(),txtUserName.getText(),txtPassword.getText(),
                    txtConfirmPass.getText(),((RadioButton) roleGroup.getSelectedToggle()).getText());

            //update table view
           // if(updated){
                userTableView.getItems().clear();
                System.out.println("Just updated the table and should update the view too: hah");
                //ObservableList<User> dataList = FXCollections.observableArrayList(userObj.getUsers());
                userTableView.setItems(userObj.getUsers());
                clearFields();
           // }

        }else{
            //Alert user
            System.out.println("There is no selection");
        }
    }


    @FXML
    public void passUserToDelete(){
        userObj = userTableView.getSelectionModel().getSelectedItem();

        if(userObj!=null) {
            int id = userObj.getId();
            userObj.deleteUser(id);
            // patientObj.deletePatient(patientObj.getId());
            clearFields();
        }
        ObservableList<User>allUsers, singleUser;
        allUsers=userTableView.getItems();
        singleUser=userTableView.getSelectionModel().getSelectedItems();
        singleUser.forEach(allUsers::remove);


    }

    @FXML
    void passUserToSearch() {//searched User


        ObservableList<User> dataOnTable = FXCollections.observableArrayList(userObj.getUsers());
        FilteredList<User> filteredData = new FilteredList<>(dataOnTable, e -> true);
        txtSearch.setOnKeyReleased(e->{
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(user -> {
                    //If filter text is empty, display all patients
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (user.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (user.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<User> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(userTableView.comparatorProperty());
            userTableView.setItems(sortedData);


        });


    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //initialize the GUI

        radioAdmin.setToggleGroup(roleGroup);
        radioUser.setToggleGroup(roleGroup);
        radioUser.setSelected(true);

        passUserToSearch();
        loadUserData();
        //Switch views/stages
        btnManageTest.setOnAction(e->{
            try {
                String fxml ="TestController.fxml";
                String windowTitle ="Manage IRS Test";
                WindowLoader winLoad = new WindowLoader();
                winLoad.loadWindow(e,userObj,windowTitle,fxml);
            }catch (Exception ex){
                //Debugg
                System.out.println("Loading Error");
            }
        });

        btnLoadDashboard.setOnAction(e->{
            try{
                String fxml = "dashboard.fxml";
                String windowTitle ="IRS Management Console";

                WindowLoader winLoad= new WindowLoader();
                winLoad.loadWindow(e, userObj,windowTitle,fxml);
            }catch(Exception ex){
                System.out.println("Loading Error");
            }
        });

        btnRecommendation.setOnAction(e->{
            try{
                String fxml = "RecommendationController.fxml";
                String windowTitle ="Manage Recommendation";

                WindowLoader winLoad= new WindowLoader();
                winLoad.loadWindow(e, userObj,windowTitle,fxml);
            }catch(Exception ex){
                System.out.println("Loading Error");
            }
        });

    }




}
