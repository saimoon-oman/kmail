package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

public class AccountRecoveryPassword {
	
	private String Id, Password;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	@FXML
	PasswordField passwordFieldPassword, passwordFieldConfirmPassword;
	@FXML
	Circle circleErrorNewPassword, circleErrorConfirmPassword;
	
	public void setData(String id) {
		Id = id;
	}
	
	public void buttonBackM(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("AccountRecovery.fxml"));
		root = loader.load();
		
		scene = new Scene(root);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		
		stage.setScene(scene);
		stage.show();
	}
	
	private boolean checkIfPasswordMatched() {
		
		String newPassword, confirmPassword;
		newPassword = passwordFieldPassword.getText();
		confirmPassword = passwordFieldConfirmPassword.getText();
		
		if (newPassword.length() == 0 || confirmPassword.length() == 0) {
			
			String alertText = "";
			if (newPassword.length() == 0) {
				circleErrorNewPassword.setVisible(true);
				alertText += "New Password Field is empty!\n";
			}
			if (confirmPassword.length() == 0) {
				circleErrorConfirmPassword.setVisible(true);
				alertText += "Confirm Password Field is empty!\n";
			}
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Password");
			alert.setHeaderText("Invalid Password");
			alert.setContentText(alertText);

			alert.showAndWait();
			
			return false;
		}
		
		Signupscenecontroller signupscenecontroller = new Signupscenecontroller();
		if (signupscenecontroller.isPasswordValid(newPassword)) {
			if (newPassword.equals(confirmPassword)) {
				return true;
			}
			else {
				circleErrorNewPassword.setVisible(true);
				circleErrorConfirmPassword.setVisible(true);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Match!");
				alert.setHeaderText("New password and confirm password doesn't match!");
				
				alert.showAndWait();
				System.out.println("New password and confirm password doesn't match!");
				return false;
				
			}
		}
		else {
			circleErrorNewPassword.setVisible(true);
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
		
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");
			
			PreparedStatement stm = con.prepareStatement("Update accounts set Password=? where ID=?");
			stm.setString(1, passwordFieldPassword.getText());
			stm.setInt(2, Integer.parseInt(Id));
			
			stm.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buttonNextM(ActionEvent event) throws IOException {
		boolean result = checkIfPasswordMatched();
		
		if (result) {
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Reset Password");
			alert.setHeaderText("Are you sure to reset your password?");

			Optional<ButtonType> rst = alert.showAndWait();
			if (rst.get() == ButtonType.OK){
				updatePassword();
				System.out.println("Password Updated Successfully!");
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
				root = loader.load();
				
				scene = new Scene(root);
				
				stage = (Stage)((Node)event.getSource()).getScene().getWindow();
				
				stage.setScene(scene);
				stage.show();
				
				alert.setAlertType(AlertType.INFORMATION);
				alert.setTitle("Reset password successful");
				alert.setHeaderText("Reset password successfully");

				alert.showAndWait();
			
			}	
		}
		else {
			
			System.out.println("Not Matched!");
		}
		
	}
}
