/**
 *
 * @author : Fadega
 * @version : 1.0
 */
package irs;
import com.jfoenix.controls.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.Date;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class DashboardController  implements Initializable{

    @FXML private TableView<Patient> patientTableView;
    @FXML private TableColumn<Patient, Integer> pIdCol;
    @FXML private TableColumn<Patient, String> pFirstnameCol;
    @FXML private TableColumn<Patient, String> pLastnameCol;
    @FXML private TableColumn<Patient, String> pGenderCol;
    @FXML private TableColumn<Patient, Date> pBirthdateCol;
    @FXML private TableColumn<Patient, String> pPhoneCol;
    @FXML private TableColumn<Patient, String> pAddressCol;
    @FXML private JFXTextField txtSearch;
    @FXML private JFXTextField txtId;
    @FXML private JFXTextField txtFirstName;
    @FXML private JFXDatePicker datePicker;
    @FXML RadioButton btnFemaleRadio;
    @FXML RadioButton btnOtherRadio;
    @FXML RadioButton btnMaleRadio;
    @FXML private JFXTextField txtLastName;
    @FXML private JFXTextField txtAddress;
    @FXML private JFXTextField txtPhone;
    @FXML private JFXButton btnAddPatient;
    @FXML private JFXButton btnManageUsers;
    @FXML private JFXButton btnLogout,btnManageTests;
    @FXML private Button btnDisplay;
    @FXML private JFXButton btnRecommendation;
    @FXML private Label lblWarning;
    private ToggleGroup genderGroup = new ToggleGroup();
    private Patient patientObj = new Patient();
    private User userObj= new User();



   
    /**
     * This method will collect data from the input fields and pass them to 'addPatient' method in class Patient
     * addPatient is ultimately responsible for adding records to the database
     */

    @FXML
    void passPatientToAdd(){//passInputData
        Boolean added;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        lblWarning.setVisible(false);
        lblWarning.setText("");


        if(txtFirstName.getText().isEmpty()||txtLastName.getText().isEmpty()||txtPhone.getText().isEmpty()||
        txtAddress.getText().isEmpty()||datePicker.getValue().toString().isEmpty()){
            validatePatientInput();
            lblWarning.setVisible(true);
            lblWarning.setText("Please fill the required fields");
        } else{
            Date birthDate = Date.valueOf(datePicker.getValue());

             added = patientObj.addPatient(txtFirstName.getText(),txtLastName.getText(),
                    ((RadioButton) genderGroup.getSelectedToggle()).getText(),
                    birthDate,txtAddress.getText(),txtPhone.getText());

            if(added==true){
                //Clear the input fields and upadate tableview after a successful creation of a record

                if(patientTableView.getItems()!=null){
                    patientTableView.getItems().clear();
                    //patientTableView.refresh();
                }

               //set to tableview to reflect renewed data
                patientTableView.setItems(patientObj.getPatients());
                clearFields();
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("A new patient record has been added.");
                alert.showAndWait();

            }else{

                alert.setTitle("Warning Dialogue");
                alert.setHeaderText(null);
                alert.setContentText("Oops,record wasn't added!");
                alert.showAndWait();
            }


        }



    }

    /**
     * This method implements the signing off the session for the user who has been logged
     * @param event - it will receive an ActionEvent as a parameter to determine the source of the event
     * It automatically opens the login prompt window by calling a loadWinow method from class WindowLoader
     */

    @FXML
    void logout(ActionEvent event) {

        WindowLoader windLoad = new WindowLoader();
        String loginTitle = "IRS System Login";
        String loginFxml = "login.fxml";
        try {
            windLoad.loadWindow(event, userObj, loginTitle, loginFxml);
        }catch (Exception e){
             System.out.println("Something went wrong in here");
            e.printStackTrace();
        }


    }


    /**
     * This method implements a search functionality on the patient data (in patient table)
     */

    @FXML
    void passPatientToSearch() {//searchedPatient


        ObservableList<Patient> dataOnTable = FXCollections.observableArrayList(patientObj.getPatients());
        FilteredList<Patient> filteredData = new FilteredList<>(dataOnTable, e -> true);
        txtSearch.setOnKeyReleased(e->{
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(patient -> {
                    //If filter text is empty, display all patients
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (patient.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (patient.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<Patient> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(patientTableView.comparatorProperty());
            patientTableView.setItems(sortedData);


        });


    }

    /**
     * The method gets the selected row/patient and passes to a deletePatient method class Patient
     */

    @FXML
    public void passPatientToDelete(){//passForDeletion
        patientObj = patientTableView.getSelectionModel().getSelectedItem();
        boolean deleted=false;
        if(patientObj!=null) {
            int id = patientObj.getId();
             deleted= patientObj.deletePatient(id);
           // patientObj.deletePatient(patientObj.getId());

        }
        if(deleted) {
            clearFields();
            ObservableList<Patient> allPatients, singlePatient;
            allPatients =FXCollections.observableArrayList(patientObj.getPatients());
            patientTableView.setItems(allPatients);
            allPatients = patientTableView.getItems();
            singlePatient = patientTableView.getSelectionModel().getSelectedItems();
            singlePatient.forEach(allPatients::remove);
        }


    }


    /**
     * This method binds the tableColumns to the variables of the Patient class - thus their values
     */



    public void loadPatientData(){
        pIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        pFirstnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        pLastnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        pGenderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        pBirthdateCol.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
        pAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        pPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

            //Call method getPatients  which returns an observable list of type Patient
       patientTableView.setItems(patientObj.getPatients());


    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Setting basic functionalities when the dashboard loads
        btnMaleRadio.setToggleGroup(genderGroup);
        btnFemaleRadio.setToggleGroup(genderGroup);
        btnOtherRadio.setToggleGroup(genderGroup);
        btnMaleRadio.setSelected(true);//default choice
        lblWarning.setVisible(false);
        lblWarning.setText("");

        passPatientToSearch();
        loadPatientData();

        btnManageTests.setOnAction(e->{
            try {
                String fxml = "TestController.fxml";
                String windowTitle = "Manage Rate Tests";

                WindowLoader winLoad = new WindowLoader();
                winLoad.loadWindow(e, patientObj, windowTitle, fxml);
            }catch (Exception ex){
                //Debugg
                System.out.println("Loading Error");
            }
        });


        btnManageUsers.setOnAction(e->{
            try{
                String fxml = "UserController.fxml";
                String windowTitle ="Manage Users";

                WindowLoader winLoad= new WindowLoader();
                winLoad.loadWindow(e, patientObj,windowTitle,fxml);
            }catch(Exception ex){
                System.out.println("Loading Error");
            }
        });



        btnRecommendation.setOnAction(e->{
            try{
                WindowLoader windLoad = new WindowLoader();
                String windowTitle = "Manage IRS Test";
                String fxmlLocation = "RecommendationController.fxml";
                windLoad.loadWindow(e,patientObj,windowTitle,fxmlLocation);
            }catch(Exception ex){
                System.out.println("Loading Error");
            }
        });


    }

    /**
     * Mmethod that implements user validation
     */


    private void validatePatientInput(){

        if(txtFirstName.getText()==null ||txtFirstName.getText().isEmpty()){
            txtFirstName.setStyle("-fx-border-color:red");
            txtFirstName.setFocusTraversable(true);
        }else{
            txtFirstName.setStyle("-fx-border-color:none");
        }
        if(txtLastName.getText()==null ||txtLastName.getText().isEmpty()){
            txtLastName.setStyle("-fx-border-color:red");
            txtLastName.setFocusTraversable(true);
        }else{
            txtLastName.setStyle("-fx-border-color:none");
        }

        if(txtAddress.getText()==null ||txtAddress.getText().isEmpty()){
            txtAddress.setStyle("-fx-border-color:red");
            txtAddress.setFocusTraversable(true);
        }else{
            txtAddress.setStyle("-fx-border-color:none");
        }
        if(txtPhone.getText()==null ||txtPhone.getText().isEmpty()){
            txtPhone.setStyle("-fx-border-color:red");
            txtPhone.setFocusTraversable(true);
        }else {
            txtPhone.setStyle("-fx-border-color:none");
        }
        if(datePicker.getValue()==null ||datePicker.getValue().toString().isEmpty()){
            datePicker.setStyle("-fx-border-color:red");
            datePicker.setFocusTraversable(true);
        }else{
            datePicker.setStyle("-fx-border-color:none");

        }
        if(!btnMaleRadio.isSelected() &&!btnFemaleRadio.isSelected()&& !btnOtherRadio.isSelected()){
            btnOtherRadio.setStyle("-fx-border-color:red");
            btnMaleRadio.setStyle("-fx-border-color:red");
            btnFemaleRadio.setStyle("-fx-border-color:red");
        }else{
            btnFemaleRadio.setStyle("-fx-border-color:none");
            btnMaleRadio.setStyle("-fx-border-color:none");
            btnOtherRadio.setStyle("-fx-border-color:none");
        }
    }


    //Clear dashboard fields
    @FXML
    public void clearFields(){

        txtId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        genderGroup.selectToggle(null);
        datePicker.setValue(null);
        txtAddress.setText("");
        txtPhone.setText("");
        txtFirstName.setStyle("-fx-border-color:none");
        txtLastName.setStyle("-fx-border-color:none");
        txtAddress.setStyle("-fx-border-color:none");
        txtPhone.setStyle("-fx-border-color:none");
        datePicker.setStyle("-fx-border-color:none");
        btnMaleRadio.setStyle("-fx-border-color:none");
        btnFemaleRadio.setStyle("-fx-border-color:none");
        btnOtherRadio.setStyle("-fx-border-color:none");
        lblWarning.setText("");
        lblWarning.setVisible(false);
        txtSearch.clear();

    }


    /**
     * This method implements binding of selected  patient data to correspoding fields on the GUI
     */
    @FXML
    public void getPatientTableData(){//getTableData

        patientObj = patientTableView.getSelectionModel().getSelectedItem();
        if(patientObj!=null){
            int id = patientObj.getId();
            txtId.setText(String.valueOf(id));
            txtFirstName.setText(patientObj.getFirstName());
            txtLastName.setText(patientObj.getLastName());
            String txt =patientObj.getGender();
            if(txt.equals("Male")){
                btnMaleRadio.setSelected(true);

            }else if(txt.equals("Female")){
                btnFemaleRadio.setSelected(true);

            }else{
                btnOtherRadio.setSelected(true);
            }

            datePicker.setValue(patientObj.getDateOfBirth().toLocalDate());
            txtAddress.setText(patientObj.getAddress());
            txtPhone.setText(patientObj.getPhone());
        }


    }

    @FXML
    public void passPatientToUpdate(){//btnUpdatedClicked
        validatePatientInput();
        Patient row;
        row= patientTableView.getSelectionModel().getSelectedItem();

        if(row!=null){

            int id = row.getId();
            row.updatePatient(id,txtFirstName.getText(),txtLastName.getText(),((RadioButton) genderGroup.getSelectedToggle()).getText(),Date.valueOf(datePicker.getValue()),txtAddress.getText(),txtPhone.getText());
            //update table view
            System.out.println("Just updated the table and should update the view too: hah");
            ObservableList<Patient> dataList = FXCollections.observableArrayList(patientObj.getPatients());
            patientTableView.setItems(dataList);
            clearFields();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialogue");
            alert.setHeaderText(null);
            alert.setContentText("Please select a record for update");
            alert.showAndWait();
        }
    }



}