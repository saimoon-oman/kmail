package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class AccountRecovery {
	@FXML
	TextField textFieldId, textFieldSecurityCode;
	@FXML
	Circle circleErrorId, circleErrorSecurityCode;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	public void buttonBackM(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
		root = loader.load();
		
		scene = new Scene(root);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		
		stage.setScene(scene);
		stage.show();
		
	}
	
	private boolean checkIdAndSecurityCode() {
		
		String id, securityCode;
		id = textFieldId.getText();
		securityCode = textFieldSecurityCode.getText();
		
		if (id.length() == 0 || securityCode.length() == 0) {
			String alertText = "";
			if (id.length() == 0) {
				circleErrorId.setVisible(true);
				alertText += "Id Field is empty!\n";
			}
			if (securityCode.length() == 0) {
				circleErrorSecurityCode.setVisible(true);
				alertText += "Security Code Field is empty!\n";
			}
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText(alertText);

			alert.showAndWait();
			return false;
		}
		
		Signupscenecontroller signupscenecontroller = new Signupscenecontroller();
		if (signupscenecontroller.isThisFieldHaveCharacter(id)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Id Must not contain character.");

			alert.showAndWait();
			return false;
		}
		
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");
			
			PreparedStatement stm = con.prepareStatement("Select * from accounts where ID=?");
			stm.setInt(1, Integer.parseInt(id));
			
			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
			
				stm = con.prepareStatement("Select * from security_code where ID=? and Code=?");
				stm.setInt(1, Integer.parseInt(id));
				stm.setString(2, securityCode);
			
				rs = stm.executeQuery();
				if (rs.next()) return true;
				else {
					circleErrorId.setVisible(true);
					circleErrorSecurityCode.setVisible(true);
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Invalid Info!");
					alert.setHeaderText("ID and Security Code doesn't match!");

					alert.showAndWait();
					return false;
				}
			}
			else {
				circleErrorId.setVisible(true);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Invalid Info!");
				alert.setHeaderText("Account doesn't exist!");

				alert.showAndWait();
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void buttonNextM(ActionEvent event) throws IOException {
		
		boolean result = checkIdAndSecurityCode();
		
		if (result) {
			System.out.println("True");
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AccountRecoveryPassword.fxml"));
			root = loader.load();
			
			AccountRecoveryPassword accountrecoverypassword = loader.getController();
			accountrecoverypassword.setData(textFieldId.getText());
			
			scene = new Scene(root);
			
			scene.getStylesheets().add(getClass().getResource("AccountRecoveryPassword.css").toExternalForm());
			
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			
			stage.setScene(scene);
			stage.show();
			
		}
		else {
			System.out.println("Invalid Match");
		}
		
	}
	
}
