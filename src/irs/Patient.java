/**
 * @author - fadega
 * @version - 1.0
 */

package irs;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Date;
import java.util.*;
import java.sql.*;

public class Patient {

    private int id;
    private String firstName;
    private String lastName;
    private String gender;
    private Date dateOfBirth;
    private String address;
    private String phone;


    public Patient(){
        this.id=0;
        this.firstName=null;
        this.lastName=null;
        this.gender=null;
        this.dateOfBirth=null;
        this.address=null;
        this.phone=null;
    }

    public Patient(int id){
        this.id=id;

    }

    public Patient(int id, String firstName, String lastName, String sex, Date dateOfBirth, String address, String phone){
        this.id = id;
        this.firstName =firstName;
        this.lastName = lastName;
        this.gender=sex;
        this.dateOfBirth=dateOfBirth;
        this.address=address;
        this.phone= phone;

    }

    /**
     * Method that implements the functionality of creating a new record of type patient and adds it to database
     * @param fname - firstname
     * @param lname - lastname
     * @param gender- gender
     * @param dob - dateofbirth
     * @param address - address
     * @param phone -phone
     */

    public boolean addPatient(String fname,String lname, String gender, Date dob, String address, String phone){
        boolean added=false;
        Connection conn = DbConnect.checkConnection();
        if(conn!=null){
            try {

                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO patient (patient_firstname,patient_lastname,patient_gender," +
                        "patient_birthdate,patient_address,patient_phone) VALUES (?,?,?,?,?,?)");
                pstmt.setString(1,fname);
                pstmt.setString(2,lname);
                pstmt.setString(3,gender);
                pstmt.setDate(4,dob);
                pstmt.setString(5,address);
                pstmt.setString(6,phone);

                int check = pstmt.executeUpdate();


                if(check>0){
                    added=true;


                }else{
                    added=false;

                }

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialogue");
                alert.setHeaderText(null);
                alert.setContentText("Oops,check your connection!");
                alert.showAndWait();;
            }

        }
        return added;

    }


    /**
     * A method that gets all the patients in the table and returns them as an observablelist
     * @return - observablelist
     */
     public   ObservableList<Patient> getPatients(){
         ObservableList<Patient> patients = FXCollections.observableArrayList();
         //check if connection is established
        try{
            Connection conn = DbConnect.checkConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM patient ");
                while (rs.next()){
                    patients.add(new Patient(rs.getInt("patient_id"), rs.getString("patient_firstname"),
                            rs.getString("patient_lastname"),rs.getString("patient_gender"),
                            rs.getDate("patient_birthdate"),rs.getString("patient_address"),rs.getString("patient_phone")));

                }
            }
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialogue");
            alert.setHeaderText(null);
            alert.setContentText("Oops,check your connection!");
            alert.showAndWait();
        }

        return patients;

    }

    /**
     * A method that collects the values of patientID column and stores them in a list
     * @return - list
     */
    public   List<Integer> getPatientId(){
        List<Integer>idList = new ArrayList<>();
        try{
            Connection conn = DbConnect.checkConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT patient_id FROM patient ");
                while (rs.next()){
                      idList.add(rs.getInt("patient_Id"));

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

       return idList;

    }

    /**
     * Method that updates basic patient information
     * @param id - patient ID
     * @param fname - firstname
     * @param lname - lastname
     * @param gender - gender
     * @param dob  - date of birth
     * @param address - address
     * @param phone  - phone
     */

    public  void updatePatient(int id, String fname, String lname, String gender, Date dob, String address, String  phone){
    //public  void updatePatientData(String fname){
        Connection conn = DbConnect.checkConnection();
        if (conn != null) {
            try{

                String query = "UPDATE patient SET patient_firstname=?, patient_lastname=?, patient_gender=?, patient_birthdate=?, patient_address=?, patient_phone=? WHERE patient_id = '"+id+"'";
                PreparedStatement pstmt = conn.prepareStatement(query);

                pstmt.setString(1, fname);
                pstmt.setString(2, lname);
                pstmt.setString(3,gender);
                pstmt.setDate(4,dob);
                pstmt.setString(5,address);
                pstmt.setString(6,phone);

                    //Debuggin
                System.out.println("psmt: "+pstmt);

                int confirm = pstmt.executeUpdate();
                if(confirm>0){
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("A record has been successfully updated.");
                    alert.showAndWait();
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
     * Method that deletes a patient record from a database table
     * @param id
     */
    public Boolean deletePatient(int id){
        boolean deleted = false;
        Alert alert;
        Connection conn = DbConnect.checkConnection();
        if(conn!=null){
            try{
                String query = "DELETE FROM patient WHERE patient_id =?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setInt(1,id);

                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Dialogue");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure to delete selected record?");

                Optional<ButtonType> action = alert.showAndWait();
                if(action.get()==ButtonType.OK){
                    pstmt.executeUpdate();
                    deleted = true;

                }

            }catch (Exception e){
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Something has gone wrong with your query!");
                alert.showAndWait();

            }

        }
        return deleted;

    }

    /**
     * Method that implements search functionality in the Patient database
     * @param name - the input string which a user types on a serch field
     * @return - if record is found return it as an observablelist of type patient
     */

    public ObservableList<Patient> searchPatient(String name){
        Connection conn = DbConnect.checkConnection();
        ObservableList<Patient> searched = FXCollections.observableArrayList();
        if(conn!=null){


          try{


              String sql = "SELECT * FROM patient WHERE patient_firstname LIKE ? OR patient_lastname LIKE ?";

              PreparedStatement pstmt = conn.prepareStatement(sql);
              pstmt.setString(1, "%" + name + "%");
              pstmt.setString(2, "%" + name + "%");
              ResultSet rs = pstmt.executeQuery();

              if(rs.next()){
                  System.out.println("IF result set is not empty");
                searched.add(new Patient(rs.getInt("patient_id"), rs.getString("patient_firstname"),
                        rs.getString("patient_lastname"),rs.getString("patient_gender"),
                        rs.getDate("patient_birthdate"),rs.getString("patient_address"),rs.getString("patient_phone")));

            }else{
                  //do nothing
              }

            }catch(Exception e){
              System.out.println("Issues with search \n "+e);


            }
        }
        return searched;

    }




    //Getters and setters


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

