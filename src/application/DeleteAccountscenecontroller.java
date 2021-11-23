package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class DeleteAccountscenecontroller {
	
	@FXML
	PasswordField passwordFieldPassword;
	@FXML
	Circle circleErrorPassword;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private String Id, Name, Email;
	
	public void setData(String id, String name, String email) {
		Id = id;
		Name = name;
		Email = email;
	}
	
	public void buttonBackM(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("EditInfo.fxml"));
		root = loader.load();
		
		EditInfo editInfo = loader.getController();
		editInfo.setData(Id, Name, Email);
		
		scene = new Scene(root);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		
		stage.setScene(scene);
		stage.show();
	}
	
	private boolean checkPassword() {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");
			
			PreparedStatement stm = con.prepareStatement("Select Password from accounts where ID=?");
			stm.setInt(1, Integer.parseInt(Id));
			
			ResultSet rs = stm.executeQuery();
			
			if (rs.next()) {
				if (passwordFieldPassword.getText().equals(rs.getString(1))) return true;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private void deleteAccount() {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");
			
			PreparedStatement stm = con.prepareStatement("Delete from accounts where Email=?");
			stm.setString(1, Email);
			stm.executeUpdate();
			
			stm = con.prepareStatement("Delete from accounts_details where Email=?");
			stm.setString(1, Email);
                        stm.executeUpdate();
                        
                        stm = con.prepareStatement("Delete from mails where (FromAddress=? or ToAddress=?)");
			stm.setString(1, Email);
                        stm.setString(2, Email);
			stm.executeUpdate();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buttonConfirmM(ActionEvent event) throws IOException {
		boolean result = checkPassword();
		
		if (result) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Delete Account");
			alert.setHeaderText("Delete Account Permanetly!!!");

			Optional<ButtonType> res = alert.showAndWait();
			if (res.get() == ButtonType.OK){
								
				deleteAccount();
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
				root = loader.load();
				
				scene = new Scene(root);
				
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				
				stage.setScene(scene);
				stage.show();
				
				alert.setAlertType(AlertType.INFORMATION);
				alert.setTitle("Account Deleted");
				alert.setHeaderText("Account Deleted Successfully");

				alert.showAndWait();
				
			    System.out.println("Account Deleted Successfully");
			
			} else {
			    System.out.println("Cancle Deletion");
			}
		}
		else {
			circleErrorPassword.setVisible(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Password");
			alert.setHeaderText("Password doesn't match!");

			alert.showAndWait();
		}
	}
}
