/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 *
 * @author USER
 */
public class MailDetailsSentMailscenecontroller implements Initializable {

    @FXML
    Label labelName, labelEmail, labelSubject, labelDateAndTime;
    @FXML
    TextArea textAreaContent;
    @FXML
    ImageView imageViewImage;

    private String Id, Name, Email, EmailSubject, EmailContent;

    private Stage stage;
    private Scene scene;
    private Parent root;
    

    FileChooser fc;
    File fileImage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    private String getNameWhoSentMail(String email) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

        PreparedStatement stm = con.prepareStatement("Select Name from accounts_details where Email=?");
        stm.setString(1, email);

        ResultSet rs = stm.executeQuery();
        if (rs.next()) {
            return rs.getString(1);
        } else {
            return " ";
        }
    }

    public void setData(String id, String name, String email, InboxMail mail) throws SQLException {
        Id = id;
        Name = name;
        Email = email;
        textAreaContent.setWrapText(true);

        labelName.setText(getNameWhoSentMail(mail.getFrom()));

        labelEmail.setText(mail.getFrom());

        EmailSubject = mail.getSubject();
        labelSubject.setText(EmailSubject);

        labelDateAndTime.setText(mail.getDateAndTime());

        EmailContent = mail.content;
        textAreaContent.setText(EmailContent);

        fileImage = mail.getFileImage();
        
        if (mail.isF()) {

            Image image = new Image(fileImage.toURI().toString());
            
            imageViewImage.setImage(image);
            imageViewImage.setSmooth(true);
            imageViewImage.setCache(true);

        } else {
            imageViewImage.setImage(null);
        }

    }

    public void buttonDownloadImageM(ActionEvent event) {
        if (imageViewImage.getImage() == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("No Image!");
            alert.setHeaderText("No Image in email!");
            
            alert.showAndWait();
        } else {
            fc = new FileChooser();
            fc.setTitle("Save Image");

            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.jpg"));
            File f2 = fc.showSaveDialog(new Stage());
            if (f2 != null) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(imageViewImage.getImage(),
                            null), "png", f2);
                    
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Download");
                    alert.setHeaderText("Image downloaded successfully");
                    
                    alert.showAndWait();

                    System.out.println("downloaded");
                } catch (IOException ex) {
                    System.out.println("ERROR DOWNLOAD");
                }
            }
        }
    }

    public void buttonForwardM(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Compose.fxml"));

        root = loader.load();

        Composescenecontroller composescenecontroller = loader.getController();
        composescenecontroller.setData(Id, Email, Name);
        composescenecontroller.setSubjectAndContent(EmailSubject, EmailContent, fileImage);

        scene = new Scene(root);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

}
