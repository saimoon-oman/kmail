package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class EditInfo {
	
	@FXML
	TextField textFieldName, textFieldDepartment, textFieldMobile;
	@FXML
	DatePicker datePickerDateOfBirth;
	@FXML
	RadioButton radioButtonGenderFemale, radioButtonGenderMale, radioButtonGenderOther;
	@FXML
	Circle circleErrorName, circleErrorDepartment, circleErrorDateOfBirth, circleErrorGender, circleErrorMobile;
	
	private String Id, Name, Email;
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	private LocalDate birthDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    LocalDate localDate = LocalDate.parse(date, formatter);
	    return localDate;
	}
	
	public void getData() {
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");
			PreparedStatement stm = con.prepareStatement("Select Name,Department,Date_of_birth,Gender,Mobile from accounts_details where ID=?");
			stm.setInt(1, Integer.parseInt(Id));
			
			ResultSet rs = stm.executeQuery();
			
			if (rs.next()) {
				textFieldName.setText(rs.getString(1));
				textFieldDepartment.setText(rs.getString(2));
				datePickerDateOfBirth.setValue(birthDate(rs.getString(3)));
				String gender = rs.getString(4);
				if (gender.equals("Male")) radioButtonGenderMale.setSelected(true);
				else if (gender.equals("Female")) radioButtonGenderFemale.setSelected(true);
				else radioButtonGenderOther.setSelected(true);
				textFieldMobile.setText(rs.getString(5));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setData(String id, String name, String email) {
		
		this.Id = id;
		this.Name = name;
		this.Email = email;
		
		
	}
	
	public void buttonSeeDetailsM(ActionEvent event) throws IOException {
		getData();
	}
	
	public void buttonBackM(ActionEvent event) throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
		root = loader.load();
		
		Welcomescenecontroller welcomescenecontroller = loader.getController();
		welcomescenecontroller.setData(Id, Email);
		
		scene = new Scene(root);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		
		stage.setScene(scene);
		stage.show();
		
	}
	
	private String getGender()
	{
		if (radioButtonGenderFemale.isSelected()) return radioButtonGenderFemale.getText();
		else if (radioButtonGenderMale.isSelected()) return radioButtonGenderMale.getText();
		else if (radioButtonGenderOther.isSelected()) return radioButtonGenderOther.getText();
		return "";
	}
	
	private boolean checkDetails() {
		String name, department, gender, mobile;
		name = textFieldName.getText();
		department = textFieldDepartment.getText();
		LocalDate localDate = datePickerDateOfBirth.getValue();
		gender = getGender();
		mobile = textFieldMobile.getText();
		
		if (name.length() == 0 || department.length() == 0 || localDate == null || gender.length() == 0 || mobile.length() == 0) {
			String alertText = "";
			if (name.length() == 0) {
				circleErrorName.setVisible(true);
				alertText += "Name Field is Empty!\n";
				System.out.println("Name Field is Empty!");
			}
			if (department.length() == 0) {
				alertText += "Department Field is Empty!\n";
				circleErrorDepartment.setVisible(true);
				System.out.println("Department Field is Empty");
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
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText(alertText);

			alert.showAndWait();
			return false;
		}
		Signupscenecontroller signupscenecontroller = new Signupscenecontroller();
		if (signupscenecontroller.isThisFieldHaveCharacter(mobile)) {
			circleErrorMobile.setVisible(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Invalid Info!");
			alert.setHeaderText("Invalid Info!");
			alert.setContentText("Mobile field must not have characters!");

			alert.showAndWait();
			System.out.println("Mobile field must not have characters");
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
		return true;
	}
	
	private void updateDetails() {

		String name, dept, dateOfBirth, gender, mobile;
		
		name = textFieldName.getText();
		dept = textFieldDepartment.getText();
		dateOfBirth = ((LocalDate)datePickerDateOfBirth.getValue()).toString();
		gender = getGender();
		mobile = textFieldMobile.getText();
		
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");
			PreparedStatement stm = con.prepareStatement("Update accounts_details set Name=? where ID=?");
			stm.setString(1, name); stm.setInt(2, Integer.parseInt(Id));
			stm.executeUpdate();
			
			stm = con.prepareStatement("Update accounts_details set Department=? where ID=?");
			stm.setString(1, dept); stm.setInt(2, Integer.parseInt(Id));
			stm.executeUpdate();
			
			stm = con.prepareStatement("Update accounts_details set Date_of_birth=? where ID=?");
			stm.setString(1, dateOfBirth); stm.setInt(2, Integer.parseInt(Id));
			stm.executeUpdate();
			
			stm = con.prepareStatement("Update accounts_details set Gender=? where ID=?");
			stm.setString(1, gender); stm.setInt(2, Integer.parseInt(Id));
			stm.executeUpdate();
			
			stm = con.prepareStatement("Update accounts_details set Mobile=? where ID=?");
			stm.setString(1, mobile); stm.setInt(2, Integer.parseInt(Id));
			stm.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void buttonUpdateM(ActionEvent event) throws IOException {
		boolean result = checkDetails();
		
		if (result) {
			updateDetails();
			System.out.println("Updated Successfully!");
			buttonBackM(event);
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Updated successfully");
			alert.setHeaderText("Information Updated Successfully.");
			
			alert.showAndWait();
		}
		else {
			System.out.println("Invalid Info");
		}
	}
	
	public void buttonCLICKHEREM(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
		root = loader.load();
		
		ChangePasswordscenecontroller changepasswordscenecontroller = loader.getController();
		changepasswordscenecontroller.setData(Id, Name, Email);
		
		scene = new Scene(root);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		
		stage.setScene(scene);
		stage.show();
	}
	
	public void buttonDELETEM(ActionEvent event) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("DeleteAccount.fxml"));
		root = loader.load();
		
		DeleteAccountscenecontroller deleteaccountscenecontroller = loader.getController();
		deleteaccountscenecontroller.setData(Id, Name, Email);
		
		scene = new Scene(root);
		
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		
		stage.setScene(scene);
		stage.show();
	}
	
}
