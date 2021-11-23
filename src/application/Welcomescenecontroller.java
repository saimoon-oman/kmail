package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Welcomescenecontroller implements Initializable {

    @FXML
    Label labelName, labelEmail;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String Id, Email, Name;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        deleteMessages();
    }

    private void deleteMessages() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Delete from mails where (InboxDeleteFlag=? and SentMailDeleteFlag=?)");

            stm.setInt(1, 1);
            stm.setInt(2, 1);
            
            stm.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(Welcomescenecontroller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String getName() {
        String name = "";

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Select Name from accounts_details where id=?");
            stm.setInt(1, Integer.parseInt(Id));

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                name = rs.getString(1);
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return name;
    }

    public void setData(String id, String email) {

        Id = id;
        Email = email;
        Name = getName();

        showNameAndEmail();
    }

    public void setData(String id, String email, String name) {
        Id = id;
        Email = email;
        Name = name;
    }

    public void showNameAndEmail() {
        labelName.setText(Name);
        labelEmail.setText(Email);
    }

    public void buttonLogOutM(ActionEvent event) {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Log Out");
        alert.setHeaderText("Are you want to Log Out?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            try {
                root = loader.load();

                scene = new Scene(root);

                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                Alert alt = new Alert(AlertType.INFORMATION);
                alt.setTitle("Log Out");
                alt.setHeaderText("Log Out Successfully");

                alt.showAndWait();

                System.out.println("Successfully Log Out");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {

        }

    }

    public void buttonInboxM(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Inbox.fxml"));

        root = loader.load();

        Inboxscenecontroller inboxscenecontroller = loader.getController();
        inboxscenecontroller.setData(Id, Name, Email);

        scene = new Scene(root);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        stage.setScene(scene);
        stage.show();
    }

    public void buttonSentMailsM(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SentMails.fxml"));

        try {
            root = loader.load();

            SentMailscenecontroller sentmailscenecontroller = loader.getController();
            sentmailscenecontroller.setData(Id, Name, Email);

            scene = new Scene(root);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void buttonComposeM(ActionEvent event) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Compose.fxml"));

        try {
            root = loader.load();

            Composescenecontroller composescenecontroller = loader.getController();
            composescenecontroller.setData(Id, Email, Name);

            scene = new Scene(root);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void buttonEditInfoM(ActionEvent event) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("EditInfo.fxml"));

        try {
            root = loader.load();

            EditInfo editInfo = loader.getController();
            editInfo.setData(Id, Name, Email);

            scene = new Scene(root);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
