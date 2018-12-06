package Controllers;

import DBConnection.DBHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password2;
    @FXML
    private TextField mail;
    @FXML
    private Button register;
    @FXML
    private Button login;
    @FXML
    private PasswordField password;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        handler = new DBHandler();
    }
    @FXML
    void loginAction(ActionEvent event) throws IOException {
        login.getScene().getWindow().hide();
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/login.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
        signup.setTitle("Talker");
    }

    @FXML
    void registerAction(ActionEvent event) throws IOException {
        String correctPassword;
        if (password.getText().equals(password2.getText())){
            correctPassword = password.getText();
            String insert = "INSERT INTO users(username,password,email)"
                    + "VALUES (?,?,?)";
            connection = handler.getConnection();
            try {
                pst = connection.prepareStatement(insert);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                pst.setString(1, username.getText());
                pst.setString(2, password.getText());
                pst.setString(3, mail.getText());
                pst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ///////////////////////////////////////
            login.getScene().getWindow().hide();
            Stage signup = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/login.fxml"));
            Scene scene = new Scene(root);
            signup.setScene(scene);
            signup.show();
            signup.setResizable(false);
            signup.setTitle("Talker");

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Passwords are not the same");
            alert.show();
        }
    }
}
