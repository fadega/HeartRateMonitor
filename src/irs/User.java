package irs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class User {


    private  int id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String confirmPassword;
    private String role;



    public User() {
        this.id=0;
        this.firstName=null;
        this.lastName=null;
        this.username = null;
        this.password = null;
        this.confirmPassword=null;
        this.role = null;

    }
    public User(int id, String firstname, String lastname, String username, String password, String confirmPass, String role) {
        this.id=id;
        this.firstName=firstname;
        this.lastName=lastname;
        this.username = username;
        this.password = password;
        this.confirmPassword=confirmPass;
        this.role = role;

    }

    public  User(int id){
        this.id=id;
    }

    private String MD5(String password){
        String hashedPass="";
        try{

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger number = new BigInteger(1,messageDigest);
            hashedPass =number.toString(16);
            while(hashedPass.length()<32){
                hashedPass="0"+hashedPass;
            }
        }catch(Exception e){
            System.out.println("Hashing failed");
        }
        return  hashedPass;
    }

    //push user data to database
    public void addUser(String fname,String lname,String username,String password, String confirmPassword,String role) {
        String hashedPass= MD5(password);
        confirmPassword =hashedPass;
        Connection conn = DbConnect.checkConnection();
        if (conn != null) {


            String sql = "INSERT INTO user(firstname,lastname,username,password,confirmpassword,role) VALUES(?,?,?,?,?,?)";
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, fname);
                    pstmt.setString(2, lname);
                    pstmt.setString(3, username);
                    pstmt.setString(4, hashedPass);
                    pstmt.setString(5, confirmPassword);
                    pstmt.setString(6, role);
                    int check = pstmt.executeUpdate();
                    if(check>0){
                        System.out.println("Data added to user table");
                    }


            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Issues with database :" + e);
            }
            //insert user data to user table

        }

    }

    public  void updateUser(int id, String fname, String lname, String username, String password, String confirPassword, String  role){
        String hashedPass= MD5(password);
        confirmPassword =hashedPass;
        Connection conn = DbConnect.checkConnection();
        if (conn != null) {
            try{

                String query = "UPDATE user SET firstname=?, lastname=?, username=?, password=?, confirmpassword=?, role=? WHERE id = '"+id+"'";
                PreparedStatement pstmt = conn.prepareStatement(query);

                pstmt.setString(1, fname);
                pstmt.setString(2, lname);
                pstmt.setString(3,username);
                pstmt.setString(4,hashedPass);
                pstmt.setString(5,confirPassword);
                pstmt.setString(6,role);

                //Debuggin
                System.out.println("psmt: "+pstmt);

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
                  //  updated =false;
                }

            }catch (Exception e){
                System.out.println(" Some update issue "+ e);
                //updated=false;
            }
        }
        //return updated;

    }

    public void deleteUser(int id){

        Alert alert;
        Connection conn = DbConnect.checkConnection();
        if(conn!=null){
            try{
                String query = "DELETE FROM user WHERE id =?";
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
                    alert.setContentText("Recorde deleted");
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



    public ObservableList<User> getUsers(){
        ObservableList<User> users = FXCollections.observableArrayList();


        try{ //check if connection is established
            Connection conn = DbConnect.checkConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM user ");
                while (rs.next()){
                    users.add(new User(rs.getInt("id"),rs.getString("firstname"),
                            rs.getString("lastname"),rs.getString("username"),
                            rs.getString("password"),rs.getString("confirmPassword"),rs.getString("role")));


                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return users;

    }

    public List<Integer> getUserId(){
        List<Integer> idList = new ArrayList<>();

        try{ //check if connection is established
            Connection conn = DbConnect.checkConnection();
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT id FROM user ");
                while (rs.next()){
                    idList.add(rs.getInt("id"));


                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        return idList;

    }




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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
