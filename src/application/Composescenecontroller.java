package application;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class Composescenecontroller {

    @FXML
    Label labelEmailFrom;
    @FXML
    TextField textFieldSubject;
    @FXML
    ComboBox comboBoxEmailTo;
    @FXML
    TextArea textAreaContent;
    @FXML
    ImageView imageViewImage;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private FileChooser fcImage;
    private File fileImage;
    private Image image;

    private String Id, Email, Name;

    public void setData(String id, String email, String name) {
        Id = id;
        Email = email;
        Name = name;
        labelEmailFrom.setText(email);
        textAreaContent.setWrapText(true);
        setComboBoxList();
        comboBoxEmailTo.setEditable(true);

    }

    public void setSubjectAndContent(String subject, String content, File file) {
        textFieldSubject.setText(subject);
        textAreaContent.setText(content);
        this.fileImage = file;

        if (fileImage != null) {
            image = new Image(fileImage.toURI().toString());

            imageViewImage.setImage(image);
            imageViewImage.setSmooth(true);
            imageViewImage.setCache(true);
        }
    }

    public void buttonLogOutM(ActionEvent event) {

        Welcomescenecontroller welcomescenecontroller = new Welcomescenecontroller();
        welcomescenecontroller.buttonLogOutM(event);

    }

    public void buttonUploadImageM(ActionEvent event) {
        fcImage = new FileChooser();

        fcImage.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.jpg"));

        fileImage = fcImage.showOpenDialog(new Stage());

        if (fileImage != null) {

            image = new Image(fileImage.toURI().toString());

            imageViewImage.setImage(image);
            imageViewImage.setSmooth(true);
            imageViewImage.setCache(true);

        }

    }

    public void buttonRemoveImageM(ActionEvent event) {
        imageViewImage.setImage(null);

    }

    public void buttonBackM(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Welcome.fxml"));

            root = loader.load();

            Welcomescenecontroller welcomescenecontroller = loader.getController();
            welcomescenecontroller.setData(Id, Email, Name);
            welcomescenecontroller.showNameAndEmail();

            scene = new Scene(root);

            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);

            stage.show();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void setComboBoxList() {
        new AutoCompleteBox(comboBoxEmailTo);
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Select Email from accounts_details");

            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                comboBoxEmailTo.getItems().add(rs.getString(1));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean sentMail() {
        String emailTo, subject, content, dateAndTime;

        subject = textFieldSubject.getText();
        content = textAreaContent.getText();
        emailTo = (String) comboBoxEmailTo.getValue();

        if (emailTo == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Info");
            alert.setHeaderText("Sent mail address is not set!");

            alert.showAndWait();
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        dateAndTime = formatter.format(now);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mail_server", "root", "");

            PreparedStatement stm = con.prepareStatement("Insert Into mails values (?,?,?,?,?,?,?,?,?,?)");
            stm.setString(1, Email);
            stm.setString(2, emailTo);
            stm.setString(3, subject);
            stm.setString(4, content);
            stm.setString(5, dateAndTime);
            stm.setInt(6, 0);
            stm.setInt(7, 0);
            stm.setInt(8, 0);
            stm.setInt(9, 0);

            Image img = new Image(fileImage.toURI().toString());
            
            if (img != null) {
                FileInputStream fileinputstream;
                try {
                    fileinputstream = new FileInputStream(fileImage);

                    try {
                        stm.setBinaryStream(10, fileinputstream, fileinputstream.available());
                    } catch (IOException ex) {
                        Logger.getLogger(Composescenecontroller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Composescenecontroller.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                stm.setInt(10, 1);
            }

            stm.executeUpdate();

            return true;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public void buttonSendM(ActionEvent event) throws IOException {

        if (sentMail()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Successfully Sent Mail");
            alert.setHeaderText("Mail sent successfully.");

            alert.showAndWait();
        }
    }

    public void buttonSendToManyM(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SendToMany.fxml"));

        root = loader.load();

        SendToManyscenecontroller sendtomanyscenecontroller = loader.getController();
        sendtomanyscenecontroller.setData(Id, Name, Email, textFieldSubject.getText(), textAreaContent.getText(), fileImage);

        Scene scene = new Scene(root);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
