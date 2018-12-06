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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;

import Client.ChatClient;
import Client.MessageListener;
import Client.UserStatusListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.application.Platform;


public class MainWindowController implements Initializable, UserStatusListener, MessageListener {
    @FXML
    private Button website;
    @FXML
    private Button profile;
    @FXML
    private Label textingarea;
    @FXML
    private Button sendtext;
    @FXML
    private Button call;
    @FXML
    private Button hangup;
    @FXML
    private Label timecall;
    @FXML
    private Label friendnamelabel;
    @FXML
    private Label friendstatuslabel;
    @FXML
    private Label namelabel;
    @FXML
    private Button addfriend;
    @FXML
    private TextField writetext;
    @FXML
    private Button logout;
    @FXML
    private ChoiceBox<String> friendchooser;
    @FXML
    private TextField addnametext;

    private Connection connection;
    private DBHandler handler;
    private PreparedStatement pst, pst2, pst3, pst4;
    private String friendslist = "";
    private List<String> friendArray = new ArrayList<String>();
	private ChatClient client;
	private DefaultListModel<String> userListModel;
	private DefaultListModel<String> textingplace;
	
	
	

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	ChatClient client = new ChatClient("localhost", 8818);
    	isOnline(client);
        handler = new DBHandler();
        setUsername(LoginController.getInstance().username());
        showFriends();
        showFriendslist();
        if(client.connect()) {
        	try {
				client.login(namelabel.getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        friendchooser.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
        	friendnamelabel.setText(newValue);
        	if(userListModel.contains(newValue)) {
        		friendstatuslabel.setText("ONLINE");
        		friendstatuslabel.setStyle("-fx-text-fill: #00ff44");
        	}else {
        		friendstatuslabel.setText("OFFLINE");
        		friendstatuslabel.setStyle("-fx-text-fill: #b60e0e");
        	}
        }); 
        textingplace = new DefaultListModel<>();
        client.addMessageListener(this);
        
    }
    public void setUsername(String user){
        this.namelabel.setText(user);
    }
    @FXML
    public void logoutAction(ActionEvent event) throws IOException {
        logout.getScene().getWindow().hide();
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/login.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
        signup.setTitle("Talker");
    }
    @FXML
    public void profileAction(ActionEvent event) throws IOException {
        profile.getScene().getWindow();
        Stage signup = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("/FXMLs/profile.fxml"));
        Scene scene = new Scene(root);
        signup.setScene(scene);
        signup.show();
        signup.setResizable(false);
        signup.setTitle("Talker");

    }

    public void showFriends(){
        String show = "SELECT friends FROM users WHERE username=?";
        connection = handler.getConnection();
        try {
            pst2 = connection.prepareStatement(show);
            pst2.setString(1,namelabel.getText());
            ResultSet rs = pst2.executeQuery();
            while(rs.next()){
                friendslist = rs.getString("friends");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        friendArray = Arrays.asList(friendslist.split(","));

    }

    @FXML
    public void addAction(ActionEvent event){
        String select = "SELECT user_id FROM users WHERE username=?";
        connection = handler.getConnection();
        try {
            pst = connection.prepareStatement(select);
            pst.setString(1,addnametext.getText());
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                if(friendslist.contains(rs.getString("user_id"))){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("You alredy have that friend in your friend list!");
                    alert.show();
                }else{
                    if(addnametext.getText().equals(namelabel.getText())){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText("You can not add yourself to the friend list!");
                        alert.show();
                    }else{
                        friendslist += rs.getString("user_id") + ",";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String update = "UPDATE users SET friends=? WHERE username=?";
        try {
            pst3 = connection.prepareStatement(update);
            pst3.setString(1,friendslist);
            pst3.setString(2,namelabel.getText());
            pst3.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        showFriends();
        showFriendslist();

    }

    public void showFriendslist(){
        String select2 = "SELECT username FROM users WHERE user_id=?";
        try {
            pst4 = connection.prepareStatement(select2);
            for(int i = 0; i < friendArray.size(); i++ ){
                pst4.setString(1,friendArray.get(i));
                ResultSet rs = pst4.executeQuery();
                while(rs.next()){
                    if(friendchooser.getItems().contains(rs.getString("username"))){

                    }else {
                        friendchooser.getItems().add(rs.getString("username"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        
	public void isOnline(ChatClient client) {
    	this.client = client;
    	this.client.addUserStatusListener(this);
    	
    	userListModel = new DefaultListModel<String>();
    }
    
    @Override
    public void online(String login) {
    	userListModel.addElement(login);
    	System.out.println(login + " zalogowal");
    }
    @Override
    public void offline(String login) {
    	userListModel.removeElement(login);
    	System.out.println(login + " wylogowal");
    }
    @FXML
    public void checkAction(ActionEvent event) {
    	if(userListModel.contains(friendnamelabel.getText())) {
    		friendstatuslabel.setText("ONLINE");
    	}else {
    		friendstatuslabel.setText("OFFLINE");
    	}
    }
    @FXML
    public void sendAction(ActionEvent event) {
    	try {
    		String text = writetext.getText();
    		if(text.length() > 0) {
			client.msg(friendnamelabel.getText(), text);
			textingplace.addElement("You: " + text + "\n");
			textingarea.setText(textingplace.toString().replaceAll("([,])", ""));
			writetext.setText("");
    		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	@Override
	public void onMessage(String fromLogin, String msgBody) {
		String line = fromLogin + ": " + msgBody + "\n";
		textingplace.addElement(line);
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				textingarea.setText(textingplace.toString().replaceAll("([,])", ""));
				
			}
			
		});
		
	}
    
}
