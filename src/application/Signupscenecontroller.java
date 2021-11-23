package application;

import application.Main;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Signupscenecontroller {
	
	@FXML
	RadioButton radioButtonGenderFemale, radioButtonGenderMale, radioButtonGenderOther;
	@FXML
	DatePicker datePickerDateOfBirth;
	@FXML
	TextField textFieldName, textFieldDepartment, textFieldId, textFieldSecurityCode, textFieldMobile;
	@FXML
	PasswordField passwordFieldPassword, passwordFieldConfirmPassword;
	@FXML
	Circle circleErrorName, circleErrorDepartment, circleErrorId, circleErrorSecurityCode, circleErrorDateOfBirth, circleErrorGender, circleErrorMobile, circleErrorPassword, circleErrorConfirmPassword;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private String getGender()
	{
		if (radioButtonGenderFemale.isSelected()) return radioButtonGenderFemale.getText();
		else if (radioButtonGenderMale.isSelected()) return radioButtonGenderMale.getText();
		else if (radioButtonGenderOther.isSelected()) return radioButtonGenderOther.getText();
		return "";
	}
	
	private boolean checkSecurityCode(String id, String securityCode)
	{
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server","root","");
			
			PreparedStatement stm = con.prepareStatement("Select ID, Code from security_code where ID=?");
			stm.setInt(1, Integer.parseInt(id));
			
			ResultSet rs = stm.executeQuery();
			
			while (rs.next()) {
				if (rs.getInt(1) == Integer.parseInt(id) && rs.getString(2).equals(securityCode)) return true;
				else return false;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean isAccountExist(String id) {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server","root","");
			
			PreparedStatement stm = con.prepareStatement("Select ID from accounts where ID=?");
			stm.setInt(1, Integer.parseInt(id));
			
			ResultSet rs = stm.executeQuery();
			
			while (rs.next()) {
				if (rs.getInt(1) == Integer.parseInt(id)) return true;
				else return false;
			}
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean isThisFieldHaveCharacter(String id) {
		for (int i = 0; i < id.length(); i++) {
			char ch = id.charAt(i);
			if (ch >= '0' && ch <= '9') continue;
			else return true;
		}
		return false;
	}
	
	public boolean isPasswordValid(String password) {
		
		if (password.length() < 6) return false;
		
		boolean small=false, big=false, ch=false, digit=false;
		
		for (int i = 0; i < password.length(); i++) {
			char character=password.charAt(i);
			if (character >= 'a' && character <= 'z') small = true;
			else if (character >= 'A' && character <= 'Z') big = true;
			else if (character >= '0' && character <= '9') ch = true;
			else digit = true;
		}
		
		if (small&&big&&ch&digit) return true;
		return false;
	}
	
	private boolean checkDetails() {
		
		String name, dept, id, securityCode, gender, mobile, password, confirmPassword;
		
		name = textFieldName.getText();
		dept = textFieldDepartment.getText();
		id = textFieldId.getText();
		securityCode = textFieldSecurityCode.getText();
		LocalDate localDate = datePickerDateOfBirth.getValue();
		gender = getGender();
		mobile = textFieldMobile.getText();
		password = passwordFieldPassword.getText();
		confirmPassword = passwordFieldConfirmPassword.getText();
		
		if (name.length() == 0 || dept.length() == 0 || id.length() == 0 || securityCode.length() == 0 || localDate == null || gender.length() == 0 || mobile.length() == 0 || password.length() == 0 || confirmPassword.length() == 0) {
			String alertText = "";
			if (name.length() == 0) {
				circleErrorName.setVisible(true);
				alertText += "Name Field is Empty!\n";
				System.out.println("Name Field is Empty!");
			}
			if (dept.length() == 0) {
				alertText += "Department Field is Empty!\n";
				circleErrorDepartment.setVisible(true);
				System.out.println("Department Field is Empty");
			}
			if (id.length() == 0) {
				alertText += "Id Field is Empty!\n";
				circleErrorId.setVisible(true);
				System.out.println("Id Field is Empty");
			}
			if (securityCode.length() == 0) {
				alertText += "Security Code Field is Empty!\n";
				circleErrorSecurityCode.setVisible(true);
				System.out.println("Security Code Field is Empty!");
			}
			if (localDate == null) {
				alertText += "Date Of Birth Field is Empty!\n";
				circleErrorDateOfBirth.setVisible(true);
				System.out.println("Date Of Birth Field is Empty!");
			}
			if (gender.length() == 0) {
				alertText += "Gender Field is Empty!\n";
				circleErrorGender.setVisible(true);
				System.out.println("Gender Field is Empty!");
			}
			if (mobile.length() == 0) {
				alertText += "Mobile is Empty!\n";
				circleErrorMobile.setVisible(true);
				System.out.println("Mobile Field is Empty!");
			}
			if (password.length() == 0 || confirmPassword.length() == 0) {
				alertText += "Password Field is Empty!\n";
				circleErrorPassword.setVisible(true);
				circleErrorConfirmPassword.setVisible(true);
				System.out.println("Password Field is Empty!");
			}
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText(alertText);

			alert.showAndWait();
			return false;
		}
		if (id.length() != 7) {
			circleErrorId.setVisible(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Id must have 7 digits!");

			alert.showAndWait();
			System.out.println("Id must have 7 digits");
			return false;
		}
		if (isThisFieldHaveCharacter(id)) {
			circleErrorId.setVisible(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Id field must not have characters!");

			alert.showAndWait();
			System.out.println("Id field must not have characters!");
			return false;
		}
		if (checkSecurityCode(id, securityCode) == false) {
			circleErrorSecurityCode.setVisible(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Security Code does not match!!!");

			alert.showAndWait();
			System.out.println("Security Code does not match!!!");
			return false;
		}
		if (mobile.length() != 11) {
			circleErrorMobile.setVisible(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Mobile field must contain 11 digits!");

			alert.showAndWait();
			System.out.println("Mobile field must contain 11 digits");
			return false;
		}
		if (isThisFieldHaveCharacter(mobile)) {
			circleErrorMobile.setVisible(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Mobile field must not have characters!");

			alert.showAndWait();
			System.out.println("Mobile field must not have characters");
			return false;
		}
		if (!password.equals(confirmPassword)) {
			circleErrorPassword.setVisible(true);
			circleErrorConfirmPassword.setVisible(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Password does not match!");

			alert.showAndWait();
			System.out.println("Password does not match");
			return false;
		}
		if (isAccountExist(id)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Already exists an account!");

			alert.showAndWait();
			System.out.println("Already exists an account");
			return false;
		}
		if (!isPasswordValid(password)) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Password atleast have 6 digit and must have atleast a uppercase alphabet, a lowercase alphabet!, a digit and a character which is not alphabet and digit!");

			alert.showAndWait();
			System.out.println("Password atleast have 6 digit and must have atleast a uppercase alphabet, a lowercase alphabet!, a digit and a character which is not alphabet and digit!");
			return false;
		}
		return true;
		
	}
	
	private String stringReverse(String s)
	{
		StringBuilder str = new StringBuilder(s);
		str.reverse();
		return str.toString();
	}
	
	private String createEmailAddress()
	{
		String temp = textFieldName.getText(), email = "";
		for (int i = temp.length()-1; i >= 0; i--) {
			char ch = temp.charAt(i);
			if (ch == ' ') break;
			email += ch;
		}
		email = stringReverse(email);
		email = email.toLowerCase();
		email += textFieldId.getText();
		return email;
	}
	
	private void updateNewUserDetails(String email)
	{
		String name, dept, id, dateOfBirth, gender, mobile, password;
		
		name = textFieldName.getText();
		dept = textFieldDepartment.getText();
		id = textFieldId.getText();
		dateOfBirth = ((LocalDate)datePickerDateOfBirth.getValue()).toString();
		gender = getGender();
		mobile = textFieldMobile.getText();
		password = passwordFieldPassword.getText();
		
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");
			
			PreparedStatement stm = con.prepareStatement("Insert into accounts_details (ID, Name, Department, Date_of_birth, Gender, Mobile, Email) values (?,?,?,?,?,?,?)");
			
			stm.setInt(1, Integer.parseInt(id));
			stm.setString(2, name);
			stm.setString(3, dept);
			stm.setString(4, dateOfBirth);
			stm.setString(5, gender);
			stm.setString(6, mobile);
			stm.setString(7, email);
			
			stm.executeUpdate();
			
			stm = con.prepareStatement("Insert into accounts (ID, Email, Password) values (?,?,?)");
			
			stm.setInt(1, Integer.parseInt(id));
			stm.setString(2, email);
			stm.setString(3, password);
			
			stm.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buttonSubmitM(ActionEvent event) throws IOException {
		
		boolean flag = checkDetails();
		
		if (flag) {
			String email = createEmailAddress()+Main.Domain;
			
			updateNewUserDetails(email);
			
			System.out.println("Successfully Sign Up!");
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Congratulation");
			alert.setHeaderText("Congratulation:)");
			alert.setContentText("Sign Up successfully.");

			alert.showAndWait();
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
			root = loader.load();
			
			Welcomescenecontroller welcomescenecontroller = loader.getController();
			
			welcomescenecontroller.setData(textFieldId.getText(), email);
			
			scene = new Scene(root);
			
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			
			stage.setScene(scene);
			stage.show();
			
			
			
		}
		else {
			System.out.println("Invalid Info");
		}
		
	}
	
	public void buttonLogInM(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		scene = new Scene(root);
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
	
	public void buttonBackM(ActionEvent event) throws IOException {
		root = FXMLLoader.load(getClass().getResource("Main.fxml"));
		scene = new Scene(root);
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		stage.setScene(scene);
		stage.show();
	}
}
