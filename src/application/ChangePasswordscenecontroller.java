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

public class ChangePasswordscenecontroller {

	@FXML
	PasswordField passwordFieldCurrentPassword, passwordFieldNewPassword, passwordFieldConfirmPassword;
	@FXML
	Circle circleErrorCurrentPassword, circleErrorNewPassword, circleErrorConfirmPassword;
	
	private String Id, Name, Email;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
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
	
	private boolean checkCurrentPassword() {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

			PreparedStatement stm = con.prepareStatement("Select Password from accounts where ID=?");
			stm.setInt(1, Integer.parseInt(Id));
			
			ResultSet rs = stm.executeQuery();
			
			if (rs.next()) {
				if (passwordFieldCurrentPassword.getText().equals(rs.getString(1))) return true;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean checkNewPassword()
	{
		Signupscenecontroller signupscenecontroller = new Signupscenecontroller();
		if (signupscenecontroller.isPasswordValid(passwordFieldNewPassword.getText()) && signupscenecontroller.isPasswordValid(passwordFieldConfirmPassword.getText())) {
			if (passwordFieldNewPassword.getText().equals(passwordFieldConfirmPassword.getText())) return true;
			else {
				circleErrorNewPassword.setVisible(true);
				circleErrorConfirmPassword.setVisible(true);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Match");
				alert.setHeaderText("New Password and Confirm Password doesn't match");

				alert.showAndWait();
				return false;
			}
		}
		else {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Password atleast have 6 digit and must have atleast a uppercase alphabet, a lowercase alphabet!, a digit and a character which is not alphabet and digit!");

			alert.showAndWait();
			System.out.println("Password atleast have 6 digit and must have atleast a uppercase alphabet, a lowercase alphabet!, a digit and a character which is not alphabet and digit!");
			return false;
		}
	}
	
	private void updatePassword() {
		Connection con;
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");
			
			PreparedStatement stm = con.prepareStatement("Update accounts set Password=? where ID=?");
			stm.setString(1, passwordFieldNewPassword.getText());
			stm.setInt(2, Integer.parseInt(Id));
			
			stm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void buttonConfirmM(ActionEvent event) throws IOException {
		boolean result = checkCurrentPassword();
		
		if (result) {
			if (checkNewPassword()) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Change Password");
				alert.setHeaderText("Are you sure want to change password?");

				Optional<ButtonType> res = alert.showAndWait();
				if (res.get() == ButtonType.OK){
					updatePassword();
					
					FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
					root = loader.load();
					
					Welcomescenecontroller welcomescenecontroller = loader.getController();
					welcomescenecontroller.setData(Id, Email);
					
					System.out.println(Id+" "+Email);
					
					scene = new Scene(root);
					
					stage = (Stage)((Node)event.getSource()).getScene().getWindow();
					
					stage.setScene(scene);
					stage.show();
					
					alert.setAlertType(AlertType.INFORMATION);
					alert.setTitle("New password set");
					alert.setHeaderText("New password set successfully");
					
					alert.showAndWait();
					
					System.out.println("New password set successfully");
				}
				
			}
			else {
				
				System.out.println("new password and confirm password Doesn't match");
			}
		}
		else {
			circleErrorCurrentPassword.setVisible(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error Match");
			alert.setHeaderText("Current Password doesn't match!");

			alert.showAndWait();
			System.out.println("Password does not match");
		}
	}
	
}
