package irs;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.time.*;
import java.util.ResourceBundle;

public class RecommendationController implements Initializable {

    //Table and table columns for TestResult
    @FXML private TableView<TestResult> testTableView;
    @FXML private TableColumn<TestResult, Integer> testIdCol;
    @FXML private TableColumn<TestResult, Integer> patientIdCol;
    @FXML private TableColumn<TestResult, Integer> testReadingCol;
    @FXML private TableColumn<TestResult, LocalDate> testDateCol;
    @FXML private TableColumn<TestResult, String> testCenterCol;
    @FXML private TableColumn<TestResult, String> regisUserCol;
    //Controls
    @FXML private JFXButton btnDashboard;
    @FXML private JFXButton btnAddUser,btnManageUsers;
    @FXML private JFXTextField txtId;
    @FXML private JFXTextField txtDays;
    @FXML private JFXTextField txtPercent;
    @FXML private JFXTextField txtMin;
    @FXML private JFXTextField txtMax;
    @FXML private JFXButton btnClear,btnManageTests;
    @FXML private JFXTextArea txtRecomendationMessage;
    @FXML private JFXDatePicker dateFrom;
    @FXML private Label lblInfo;


   //Object of type TestResult for accessing
    private  TestResult  testResultObj = new TestResult();



 void loadTestResultData(){

     testIdCol.setCellValueFactory(new PropertyValueFactory<>("testId"));
     patientIdCol.setCellValueFactory(new PropertyValueFactory<>("patientId"));
     testReadingCol.setCellValueFactory(new PropertyValueFactory<>("testReading"));
     testDateCol.setCellValueFactory(new PropertyValueFactory<>("testDate"));
     testCenterCol.setCellValueFactory(new PropertyValueFactory<>("testCenter"));
     regisUserCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

     //Call method getTestResults  which returns an observable list of type TestResult

     testTableView.setItems(testResultObj.getTests());
 }
    /**
     * This method does the following
     * 1. Accept user data
     * 2. Passes user datat to calculateRecommendation method of class TestResult
     * 3. waits for a message from the method to reflect it in the GUI
     */

    public void calculateRecommendation(){
     //user specified parameters
     int id, days, percent=0;
     LocalDate date;
     int max =100;
     int min = 60;
     String recommendationMessage;

     try { //check if use has put valid input
         if (txtId.getText().isEmpty() || txtDays.getText().isEmpty() || txtPercent.getText().isEmpty()||
                 txtMin.getText().isEmpty() || txtMax.getText().isEmpty()) {
             lblInfo.setVisible(true);
             validateSpecifiedInput();
         }
         else if(Integer.parseInt(txtPercent.getText()) < 0 || Integer.parseInt(txtPercent.getText())>100){
             validateSpecifiedInput();
             }

         else{
             lblInfo.setVisible(false);
             validateSpecifiedInput();
             id = Integer.parseInt(txtId.getText());
             days = Integer.parseInt(txtDays.getText());
             max = Integer.parseInt(txtMax.getText());
             min = Integer.parseInt(txtMin.getText());
             percent = Integer.parseInt(txtPercent.getText());
             date = dateFrom.getValue();

             recommendationMessage = testResultObj.calculateRecommendation(id, days, percent,date, min, max);
             if(recommendationMessage.contains("No Test")){
                 txtRecomendationMessage.setStyle("-fx-text-inner-color: green;");

             } else if (recommendationMessage.contains("No Records")){
                 txtRecomendationMessage.setStyle("-fx-text-inner-color: none;");

             }else{
                 txtRecomendationMessage.setStyle("-fx-text-inner-color: red;");
             }
             txtRecomendationMessage.setText(recommendationMessage);
             txtRecomendationMessage.setEditable(false);

         }
     }catch (Exception e){

         validateSpecifiedInput();
     }
 }

    /**
     * This method validate the user input. Date field is optional - no validation for the field
     * There is no limit on the number days a user can input or the choice of patient id
     */

 public void validateSpecifiedInput(){
     lblInfo.setText("Please fill the required fields");
    if(txtDays.getText().isEmpty()||txtDays.getText()==null){
        txtDays.setStyle("-fx-border-color:red");
        txtDays.focusedProperty();
    }else{
        txtDays.setStyle("-fx-border-color:none");
    }
     if(txtId.getText().isEmpty()||txtId.getText()==null){
         txtId.setStyle("-fx-border-color:red");
         txtId.focusedProperty();
     }else{
         txtId.setStyle("-fx-border-color:none");
     }
     if(txtPercent.getText().isEmpty()||txtPercent.getText()==null){
         txtPercent.setStyle("-fx-border-color:red");
         txtPercent.focusedProperty();
     }else if(Integer.parseInt(txtPercent.getText()) < 0 || Integer.parseInt(txtPercent.getText())>100){
         Alert alert = new Alert(Alert.AlertType.INFORMATION);
         alert.setTitle("Warning Dialog");
         alert.setHeaderText(null);
         alert.setContentText("The value of percent should be between 0 and 100");
         alert.showAndWait();
     }else{
         txtPercent.setStyle("-fx-border-coler:none");
     }
     if(txtMax.getText().isEmpty()||txtMax.getText()==null){
         txtMax.setStyle("-fx-border-color:red");
         txtMax.focusedProperty();
     }else{
         txtMax.setStyle("-fx-border-color:none");
     }
     if(txtMin.getText().isEmpty()||txtMin.getText()==null){
         txtMin.setStyle("-fx-border-color:red");
         txtMin.focusedProperty();
     }else{
         txtMin.setStyle("-fx-border-color:none");
     }

 }

    @FXML
    void logOut(ActionEvent event) throws Exception{
        DashboardController dbController = new DashboardController();
        dbController.logout(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Patient patientObj=new Patient();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        //load Test result Data
        loadTestResultData();


        //Switch views/stages
        btnManageTests.setOnAction(e->{
            try {
                String fxml = "TestController.fxml";
                String windowTitle ="Manage Test Rates";
                WindowLoader winLoad= new WindowLoader();
                winLoad.loadWindow(e, patientObj,windowTitle,fxml);
            }catch (Exception ex){
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Loading Error!");
                alert.showAndWait();
            }
        });

        btnManageUsers.setOnAction(e->{
            try{
                String fxml = "UserController.fxml";
                String windowTitle ="Manage Users";
                WindowLoader winLoad= new WindowLoader();
                winLoad.loadWindow(e, patientObj,windowTitle,fxml);
            }catch(Exception ex){
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Loading Error!");
                alert.showAndWait();
            }
        });

        btnDashboard.setOnAction(e->{
            try{
                String fxml = "dashboard.fxml";
                String windowTitle ="IRS Management Console";
                WindowLoader winLoad= new WindowLoader();
                winLoad.loadWindow(e, patientObj,windowTitle,fxml);
            }catch(Exception ex){
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Loading Error!");
                alert.showAndWait();
            }
        });
        btnClear.setOnAction(e->{
            txtDays.clear();
            txtPercent.clear();
            txtPercent.clear();
            dateFrom.setValue(null);
        });

    }

}