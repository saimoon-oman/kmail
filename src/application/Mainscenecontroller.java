package application;

import java.io.FileInputStream;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Mainscenecontroller {

    @FXML
    TextField textFieldEmail;
    @FXML
    PasswordField passwordFieldPassword;
    @FXML
    Circle circleErrorEmail, circleErrorPassword;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void bottonSignUpM(ActionEvent event) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Signup.fxml"));
        try {
            root = fxmlLoader.load();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        scene = new Scene(root);

        scene.getStylesheets().add(getClass().getResource("Signup.css").toExternalForm());

        scene.getStylesheets().add(getClass().getResource("Signup.css").toExternalForm());

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    private String checkEmailPassword() {
        String email, password;
        email = textFieldEmail.getText();
        password = passwordFieldPassword.getText();

        try {

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Select Email, Password from accounts where Email=? and Password=?");

            stm.setString(1, email);
            stm.setString(2, password);

            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                return "true";
            } else {
                return "false";
            }

        } catch (Exception e) {
            System.out.println(e);
            return e.getMessage();
        }
    }

    private String getId() {
        String email = textFieldEmail.getText();
        String id = new String();

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Select ID from accounts where Email=?");
            stm.setString(1, email);

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                id = Integer.toString(rs.getInt(1));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return id;
    }

    public void buttonLogInM(ActionEvent event) throws IOException {
        try {

            String result = checkEmailPassword();

            if (result == "true") {

                String id = getId();
                String email = textFieldEmail.getText();
                System.out.println("Successfully Log In");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));
                root = loader.load();

                Welcomescenecontroller welcomescenecontroller = loader.getController();

                welcomescenecontroller.setData(id, email);

                scene = new Scene(root);

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                stage.setScene(scene);
                stage.show();

            } else {
                System.out.println("Invaild user Email and Password");

                circleErrorEmail.setVisible(true);
                circleErrorPassword.setVisible(true);
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Invalid Info!");
                alert.setHeaderText("Invalid Info!");
                alert.setContentText("Invalid Email and Password!");

                alert.showAndWait();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void buttonResetPasswordM(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AccountRecovery.fxml"));

            root = loader.load();

            scene = new Scene(root);

            scene.getStylesheets().add(getClass().getResource("AccountRecovery.css").toExternalForm());

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
