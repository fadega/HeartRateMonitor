package irs;

import com.jfoenix.controls.*;
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

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class TestController implements Initializable {

    @FXML private JFXTextField txtId;
    @FXML private JFXDatePicker testDatePicker;
    @FXML private JFXTextField txtReading;
    @FXML private JFXComboBox<Patient>comboPatientId;
    @FXML private JFXComboBox<User> comboUserId;
    @FXML private JFXTextField txtTestCenter;
    @FXML private TableView <TestResult> testTableview;
    @FXML private TableColumn<TestResult, Integer> idCol;
    @FXML private TableColumn<TestResult, LocalDate> dateCol;
    @FXML private TableColumn<TestResult, Integer> rateCol;
    @FXML private TableColumn<TestResult, Integer> patientIdCol;
    @FXML private TableColumn<TestResult, String> testCenterCol;
    @FXML private TableColumn<TestResult, Integer> userIdCol;
    @FXML private JFXButton btnLoadDashboard;
    @FXML private JFXButton btnLoadRecommendation;
    @FXML private JFXButton btnLoadUsers;
    @FXML private JFXTextField txtSearch;


    private TestResult testResultObj = new TestResult();
    private Patient patientObj = new Patient();
    private User userObj = new User();

    ObservableList<TestResult> tests = FXCollections.observableArrayList();
    List patientList = new ArrayList();
    List userList = new ArrayList();

    /**
     * Binds data to the ueser table
     */

  private void loadTestData(){

      idCol.setCellValueFactory(new PropertyValueFactory<>("testId"));
      dateCol.setCellValueFactory(new PropertyValueFactory<>("testDate"));
      rateCol.setCellValueFactory(new PropertyValueFactory<>("testReading"));
      patientIdCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
      testCenterCol.setCellValueFactory(new PropertyValueFactory<>("testCenter"));
      userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

      //Call method getTestResults  which returns an observable list of type TestResult

      testTableview.setItems(testResultObj.getTests());


      //Get and bind already Existing patients' IDs to combobxes
      patientList = patientObj.getPatientId();
      userList = userObj.getUserId();
      ObservableList pObsList = FXCollections.observableArrayList(patientList);
      ObservableList uObsList = FXCollections.observableArrayList(userList);

      comboPatientId.setItems(pObsList);
      comboUserId.setItems(uObsList);

  }

    /**
     * Pass test data to be added to the database
     */

    @FXML
    private void passTestToAdd(){
        if(testDatePicker.getValue()==null || txtReading.getText().isEmpty() || comboPatientId.getValue()==null ||
                txtTestCenter.getText().isEmpty()|| comboUserId.getValue()==null){

            validateTestInput();

        }else{
            //Convert the selected id of type Patient object to String and then integer
            Object selectedPatient = comboPatientId.getSelectionModel().getSelectedItem();
            Object selectedUser = comboUserId.getSelectionModel().getSelectedItem();
            String selP = selectedPatient.toString();
            String selU = selectedUser.toString();
            int pid = Integer.parseInt(selP);
            int uid = Integer.parseInt(selU);


            //Call pushTestData method to add the record

            testResultObj.addTest(Date.valueOf(testDatePicker.getValue().toString()), Integer.parseInt(txtReading.getText()),
                    pid,uid,txtTestCenter.getText());

            //Clear fields and loadTestData after adding a record
            clearFields();
            testTableview.setItems(testResultObj.getTests());
            loadTestData();

            }

    }

    /**
     * passes validated data to be added to the database
     */

    @FXML
    public void passTestToUpdate(){
        TestResult row;

        //check if user has selected a row in an tableview to update

        row= testTableview.getSelectionModel().getSelectedItem();

        if(row!=null){

            //Convert the selected id of type Patient object to String and then integer
            Object selectedPatient = comboPatientId.getSelectionModel().getSelectedItem();
            Object selectedUser = comboUserId.getSelectionModel().getSelectedItem();
            String selP = selectedPatient.toString();
            String selU = selectedUser.toString();
            int pid = Integer.parseInt(selP);
            int uid = Integer.parseInt(selU);

            int id = row.getTestId();

            row.updateTest(id,Date.valueOf(testDatePicker.getValue()),Integer.parseInt(txtReading.getText()),pid,uid,txtTestCenter.getText());

            //update table view
            testTableview.getItems().clear();
            System.out.println("Just updated the table and should update the view too: hah");
           // ObservableList<TestResult> dataList = FXCollections.observableArrayList(testResultObj.getTestResults());
            testTableview.setItems(testResultObj.getTests());
            clearFields();


        }else{ //if no row is selected and update button is clicked
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Please select a record for update.");
            alert.showAndWait();
        }
    }


    private void validateTestInput(){
      if(testDatePicker.getValue()==null){
          testDatePicker.setStyle("-fx-border-color:red");
      }else{
          testDatePicker.setStyle("-fx-border-color:none");
      }
      if(txtReading.getText().isEmpty()){
          txtReading.setStyle("-fx-border-color:red");
      }else{
          txtReading.setStyle("-fx-border-color:none");
      }
      if(comboPatientId.getValue()==null){
          comboPatientId.setStyle("-fx-border-color:red");
        }else{
          comboPatientId.setStyle("-fx-border-color:none");
      }
      if(comboUserId.getValue()==null){
          comboUserId.setStyle("-fx-border-color:red");
      }else{
          comboUserId.setStyle("-fx-border-color:none");
      }

      if(txtTestCenter.getText().isEmpty()) {
            txtTestCenter.setStyle("-fx-border-color:red");
        }else{
          txtTestCenter.setStyle("-fx-border-color:none");
      }

    }


    @FXML
    private void passTestToDelete(){

        testResultObj = testTableview.getSelectionModel().getSelectedItem();

        if(testResultObj!=null) {
            int id = testResultObj.getTestId();
            System.out.println("Test id for deletion :"+id);
            testResultObj.deleteTest(id);
            clearFields();
        }
        ObservableList<TestResult>allTests, singleTest;
        allTests=testTableview.getItems();
        singleTest=testTableview.getSelectionModel().getSelectedItems();
        singleTest.forEach(allTests::remove);

    }


    //Clear and reset controls

    @FXML
    private void clearFields(){

         //Clear controls
        txtId.clear(); testDatePicker.setValue(null);txtReading.clear();
        txtTestCenter.clear();

        //Clear lists that feed the comboboxes
        patientList.clear();
        userList.clear();

        //Clear selected values of comboboxes if any
        comboUserId.getSelectionModel().clearSelection();
        comboPatientId.getSelectionModel().clearSelection();

        //Clear the tableview and reload the TestResults
        testTableview.getItems().clear();
        loadTestData();
        testDatePicker.setStyle("-fx-border-color:none");
        txtTestCenter.setStyle("-fx-border-color:none");
        comboUserId.setStyle("-fx-border-color:none");
        txtReading.setStyle("-fx-border-color:none");
        comboPatientId.setStyle("-fx-border-color:none");



    }

    /**
     * Method implements loading of Test data from tableview to corresponding fields for update and review
     * @param event
     */

    @FXML
    private void getTestTableData(MouseEvent event) {
        testResultObj = testTableview.getSelectionModel().getSelectedItem();
        if(testResultObj!=null){
            int id = testResultObj.getTestId();
            txtId.setText(String.valueOf(id));
            testDatePicker.setValue(testResultObj.getTestDate());
            int rate = testResultObj.getTestReading();
            txtReading.setText(String.valueOf(rate));

            int patientId= testResultObj.getPatientId();
            List pList = new ArrayList();
            pList.add(patientId);
            ObservableList pObList = FXCollections.observableArrayList(pList);

            comboPatientId.setItems(pObList);
            comboPatientId.getSelectionModel().selectFirst();


            int userId= testResultObj.getUserId();
            List uList = new ArrayList();
            uList.add(userId);
            ObservableList uObList = FXCollections.observableArrayList(uList);

            comboUserId.setItems(uObList);
            comboUserId.getSelectionModel().selectFirst();

//            comboPatientId.getSelectionModel().getSelectedItem();

         //   int userId= testResultObj.getUserId();
//            comboUserId.getItems().add(userId);
            txtTestCenter.setText(testResultObj.getTestCenter());

        }

    }

    /**
     * This method will implement a search for test centers
     */

    @FXML
    void passTestCenterToSearch() {//searched User


        ObservableList<TestResult> dataOnTable = FXCollections.observableArrayList(testResultObj.getTests());
        FilteredList<TestResult> filteredData = new FilteredList<>(dataOnTable, e -> true);
        txtSearch.setOnKeyReleased(e->{
            txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(center -> {
                    //If filter text is empty, display all patients
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (center.getTestCenter().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    } else if (center.getTestCenter().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
            SortedList<TestResult> sortedData = new SortedList<>(filteredData);
            sortedData.comparatorProperty().bind(testTableview.comparatorProperty());
            testTableview.setItems(sortedData);


        });


    }


    @FXML
    private void logout(ActionEvent e){
        DashboardController db = new DashboardController();
        db.logout(e);
    }

    // initialiaze method: initializes the window when it loads

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);


        passTestCenterToSearch();
        loadTestData();


        //Switch views/stages
        btnLoadRecommendation.setOnAction(e->{
            try {
                String fxml = "RecommendationController.fxml";
                String windowTitle ="Manage Recommendation";
                WindowLoader winLoad= new WindowLoader();
                winLoad.loadWindow(e, userObj,windowTitle,fxml);
            }catch (Exception ex){
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Loading Error!");
                alert.showAndWait();
            }
        });

        btnLoadUsers.setOnAction(e->{
            try{
                String fxml = "UserController.fxml";
                String windowTitle ="Manage users";
                WindowLoader winLoad= new WindowLoader();
                winLoad.loadWindow(e, userObj,windowTitle,fxml);
            }catch(Exception ex){
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Loading Error!");
                alert.showAndWait();
            }
        });

        btnLoadDashboard.setOnAction(e->{
            try{
                String fxml ="dashboard.fxml";
                String windowTitle ="IRS Management Console";
                WindowLoader winLoad = new WindowLoader();
                winLoad.loadWindow(e,userObj,windowTitle,fxml);
            }catch(Exception ex){
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Loading Error!");
                alert.showAndWait();
            }
        });





    }
}
