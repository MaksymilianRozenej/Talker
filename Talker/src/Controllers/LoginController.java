package Controllers;

import DBConnection.DBHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LoginController implements Initializable {
    @FXML
    private PasswordField password;
    @FXML
    private Hyperlink internet;
    @FXML
    private TextField username;
    @FXML
    private Button login;
    @FXML
    private Button Register;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;
    private static LoginController instance;

    public LoginController(){
        instance = this;
    }
    public static LoginController getInstance(){
        return instance;
    }
    public String username(){
        return username.getText();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = new DBHandler();
    }
    @FXML
    void registerAction(ActionEvent event) throws IOException {
        Register.getScene().getWindow().hide();

        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/register.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
        signup.setTitle("Talker");
    }
    @FXML
    void loginAction(ActionEvent event){
        connection = handler.getConnection();
        String q1 = "SELECT * from users where username=? and password=?";
        try {
            pst = connection.prepareStatement(q1);
            pst.setString(1, username.getText());
            pst.setString(2,password.getText());
            ResultSet rs = pst.executeQuery();

            int count = 0;
            while(rs.next()){
                count=count+1;
            }
            if(count==1){
                login.getScene().getWindow().hide();
                Stage home = new Stage();
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/mainWindow.fxml"));
                    Scene scene = new Scene(root);
                    home.setScene(scene);
                    home.show();
                    home.setResizable(false);
                    home.setTitle("talker");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText("Username or Password is Not Correct!");
                alert.show();
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        finally{
            try {
                connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
    @FXML
    public void en(KeyEvent en1) {
        if (en1.getCode() == KeyCode.ENTER) {
            connection = handler.getConnection();
            String q1 = "SELECT * from users where username=? and password=?";
            try {
                pst = connection.prepareStatement(q1);
                pst.setString(1, username.getText());
                pst.setString(2,password.getText());
                ResultSet rs = pst.executeQuery();

                int count = 0;
                while(rs.next()){
                    count=count+1;
                }
                if(count==1){
                    login.getScene().getWindow().hide();
                    Stage home = new Stage();
                    try {
                        Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/mainWindow.fxml"));
                        Scene scene = new Scene(root);
                        home.setScene(scene);
                        home.show();
                        home.setResizable(false);
                        home.setTitle("talker");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Username or Password is Not Correct!");
                    alert.show();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            finally{
                try {
                    connection.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
