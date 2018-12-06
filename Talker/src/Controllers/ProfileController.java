package Controllers;

import DBConnection.DBHandler;
import javafx.event.ActionEvent;

import javafx.fxml.Initializable;


import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;


import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ProfileController implements Initializable {
    @FXML
    private Button edit;
    @FXML
    private PasswordField oldpassword;
    @FXML
    private TextField newpassword;
    @FXML
    private Label login;
    @FXML
    private TextField email;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst, pst2;
    private String profilename;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = new DBHandler();
        setUsername(LoginController.getInstance().username());
        getInformation();
    }

    public void setUsername(String user){ this.profilename = user; }

    @FXML
    void editAction(ActionEvent event) {
        String edit = "UPDATE users SET email=?, password=? WHERE username=?";
        connection = handler.getConnection();
        try {
            pst2 = connection.prepareStatement(edit);
            pst2.setString(1,email.getText());
            if(newpassword.getText().length() > 0){
                pst2.setString(2,newpassword.getText());
            }else{
                pst2.setString(2,oldpassword.getText());
            }
            pst2.setString(3,profilename);
            pst2.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        getInformation();
    }

    public void getInformation(){
        String info = "SELECT password, email FROM users WHERE username=?";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(info);
            pst.setString(1,profilename);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                oldpassword.setText(rs.getString("password"));
                email.setText(rs.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        login.setText(profilename);
    }
}
