package irs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestResult {
    private int testId;
    private LocalDate testDate;
    private int testReading;
    private String testCenter;
    private int patientId;
    private int userId;



    public TestResult(){
        this.testId=0;
        this.testCenter=null;
        this.testReading=0;
        this.testDate=null;
        this.userId=0;
        this.patientId=0;


    }



    public TestResult(int testId, LocalDate testDate, int testReading,String testCenter,int patientId, int userId){
        this.testId=testId;
        this.testDate=testDate;
        this.testCenter=testCenter;
        this.testReading=testReading;
        this.patientId=patientId;
        this.userId=userId;

    }


    /**
     * Method implements fetching of rate records and returns records as ObservableList
     * @return  observablelist of type TestResult
     */

    public ObservableList<TestResult> getTests(){
        ObservableList<TestResult> results = FXCollections.observableArrayList();
        try{ //check if connection is established
            ResultSet rs;
            Connection conn = DbConnect.checkConnection();
            if (conn != null) {
               Statement stmt = conn.createStatement();
                rs =stmt.executeQuery("SELECT *FROM result");
                while (rs.next()){

                    results.add(new TestResult(rs.getInt("test_id"), rs.getDate("test_date").toLocalDate(),
                            rs.getInt("test_data"),rs.getString("test_center"),
                            rs.getInt("patient_fk"),rs.getInt("user_fk")));

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return results;

    }

    public  void updateTest(int id, Date date, int rate, int pid, int uid, String center){

        Connection conn = DbConnect.checkConnection();
        if (conn != null) {
            try{

                String query = "UPDATE result SET test_date=?, test_data=?, patient_fk=?, user_fk=?, test_center=? WHERE test_id = '"+id+"'";
                PreparedStatement pstmt = conn.prepareStatement(query);

                pstmt.setDate(1, (java.sql.Date) date);
                pstmt.setInt(2, rate);
                pstmt.setInt(3,pid);
                pstmt.setInt(4,uid);
                pstmt.setString(5,center);

                int confirm = pstmt.executeUpdate();

                if(confirm>0){

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("A record has been successfully updated.");
                    alert.showAndWait();
                    //updated=true;
                }else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("ohh Update failed!");
                    alert.showAndWait();

                }

            }catch (Exception e){
                System.out.println(" Some update issue "+ e);

            }
        }

    }



    /**
     * Method that adds test record to the database
     * @param date - date of the test
     * @param rate  - test reading
     * @param pId  - patient id for which the test is added to
     * @param uId - user id for the user who is adding the record
     * @param center  - test center
     */

    public void addTest( Date date, int rate, int pId, int uId, String center) {

        Connection conn = DbConnect.checkConnection();
        if (conn != null) {


            String sql = "INSERT INTO result(test_date,test_data,patient_fk,user_fk,test_center) VALUES(?,?,?,?,?)";
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setDate(1, (java.sql.Date) date);
                pstmt.setInt(2, rate);
                pstmt.setInt(3, pId);
                pstmt.setInt(4, uId);
                pstmt.setString(5, center);
                int check = pstmt.executeUpdate();
                if(check>0){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("New record has been added to the table");
                    alert.showAndWait();
                }


            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Issues with database :" + e);
            }

        }

    }

    /**
     * Method that is responsible for deleting a test record
     * @param id
     */
    public void deleteTest(int id){
        Alert alert;
        Connection conn = DbConnect.checkConnection();
        if(conn!=null){
            try{
                String query = "DELETE FROM result WHERE test_id =?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1,id);

                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Dialogue");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure to delete selected record?");

                Optional<ButtonType> action = alert.showAndWait();
                if(action.get()==ButtonType.OK){
                    pstmt.executeUpdate();
                    alert=new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialogue");
                    alert.setHeaderText(null);
                    alert.setContentText("Record deleted");
                    alert.showAndWait();

                }

            }catch (Exception e){
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Something has gone wrong with your query!");
                alert.showAndWait();

            }

        }


    }


    /**
     * This method calculates the recommendation for heart rate test
     * @param id  - the id of the patient for which the recommendation is calculated
     * @param days - this a user specified number of days ( k - as in in the document )  for which the check is performed
     * @param userPercent - the is the user specified parameter mentioned in the document as p%
     * @param date - users have a choice to enter any date to check
     * @param max  - The maximum normal  heart rate value - this can change if the users wishes to but by default it's set to 100
     * @param min - The minimum normal  heart rate value - this can change if the users wishes to but by default it's set to 60
     * @return returns a string with recommendation message
     */
    public String calculateRecommendation(int id, int days, int userPercent, LocalDate date, int min, int max){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");

        LocalDate enteredDate =null, calculatedDate=null;
        System.out.println("Date entered is => "+date);
        int  abnormal=0, normal=0;

        //A list to store all the rate test for the range of days that user has chosen
        List<Integer> rateList = new ArrayList();

        //Calculating  p% of pickedDays = (day*userSpecifiedPercent)/100

        int taken = (days*userPercent)/100;
        Connection conn = DbConnect.checkConnection();
        ResultSet rs;
        if(conn!=null){
            try{
                String query="";
                if(date!=null){
                    // This code use the user specified date and days and calculate a date between which the check should be done
                     enteredDate = date;
                     calculatedDate = date.minusDays(days);
                    System.out.println("Record between calucaltedate date - "+formatter.format(calculatedDate)+ " and entered date - "+formatter.format(enteredDate)+" are: ");

                    query= "Select test_id,FORMAT(test_date,'DD/MM/YYYY'),test_data,patient_fk,user_fk,test_center FROM result WHERE patient_fk='"+id +"' AND test_date BETWEEN '" + formatter.format(calculatedDate) + "' AND '" + enteredDate + "'  ORDER BY test_date DESC ";
                }else{
                        //this query executes if user doesn't enter a date as a parameter
                    query = "SELECT *FROM result where patient_fk='"+id +"'  ORDER BY test_date DESC ";
                }

                Statement stmt = conn.createStatement();
                rs = stmt.executeQuery(query);
                //if records are found, store them to list to check the number of normal and abnormal readings
                while (rs.next()){
                    if(days>0){
                    rateList.add(Integer.parseInt(rs.getString("test_data")));
                    days --;
                    }

                }

            }catch (Exception e){
                System.out.println("Error on your connection or bla "+e);

            }

            //Check if records are found and if found count all the normal and abnormal rates
            if(rateList.size()>0){
                for(int i: rateList){
                    if(i>=min && i<=max){
                        normal++;
                    }else{
                        abnormal++;
                    }
                }
            }else{
                return "No Records Found for the input provided";
            }


        }

        // Debugging - print some values
        System.out.println("Date between "+calculatedDate+" and "+enteredDate);
        System.out.println("Patient Rates :"+rateList);
        System.out.println("Record count : "+rateList.size());
        System.out.println("Normal rates : "+normal);
        System.out.println("Abnoraml rates :"+abnormal);
        System.out.println("Percent days(days*userPercent)/100):"+taken);

        /*
            Check if all taken tests in the given days are normal
            Also check if normal is no less that p% of the days

         */
        if(normal==rateList.size()){
             return "No Test Needed";
        }else if(normal>=taken && taken<=rateList.size()){
            return "No Test Needed";
        } else{
            return "Test Needed";
        }


    }



    //Getters and Setters

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public int getTestReading() {
        return testReading;
    }

    public void setTestReading(int testReading) {
        this.testReading = testReading;
    }

    public String getTestCenter() {
        return testCenter;
    }

    public void setTestCenter(String testCenter) {
        this.testCenter = testCenter;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

